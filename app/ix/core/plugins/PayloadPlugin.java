package ix.core.plugins;

import java.io.*;
import java.util.List;
import java.security.MessageDigest;
import java.security.DigestInputStream;
    


import play.Logger;
import play.Plugin;
import play.Application;
import play.mvc.Http;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;

import ix.core.models.Payload;
import ix.core.controllers.PayloadFactory;
import ix.utils.Util;

public class PayloadPlugin extends Plugin {
    private final Application app;
    private IxContext ctx;

    public PayloadPlugin (Application app) {
        this.app = app;
    }

    public void onStart () {
        Logger.info("Loading plugin "+getClass().getName()+"...");
        ctx = app.plugin(IxContext.class);
        if (ctx == null)
            throw new IllegalStateException
                ("IxContext plugin is not loaded!");
    }

    public void onStop () {
        Logger.info("Plugin "+getClass().getName()+" stopped!");
    }

    public boolean enabled () { return true; }
    public Payload createPayload (String name, String mime, InputStream is)
        throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        File tmp = File.createTempFile("___", ".tmp", ctx.payload);
        FileOutputStream fos = new FileOutputStream (tmp);
        DigestInputStream dis = new DigestInputStream (is, md);
        
        byte[] buf = new byte[2048];
        Payload payload = new Payload ();            
        payload.size = 0l;
        for (int nb; (nb = dis.read(buf, 0, buf.length)) > 0; ) {
            fos.write(buf, 0, nb);
            payload.size += nb;
        }
        dis.close();
        fos.close();
        
        payload.sha1 = Util.toHex(md.digest());
        List<Payload> found =
            PayloadFactory.finder.where().eq("sha1", payload.sha1).findList();
        
        boolean save=true;
        if (!found.isEmpty()){
            payload = found.iterator().next();
            Logger.debug("payload already loaded as "+payload.id);
            try{
            	File f=PayloadFactory.getFile(payload);
            	if(!f.exists())throw new IllegalStateException("payload deleted");
            	save=false;
            }catch(Exception e){
            	Logger.debug(payload.name+" file not found");
            }
        }
        
        if (save) {
            payload.name = name;
            payload.mimeType = mime;
            
            payload.save();
            if (payload.id != null) {
                tmp.renameTo(new File (ctx.payload, payload.id.toString()));
            }
            Logger.debug(payload.name+" => "+payload.id + " " +payload.sha1);
        }
        
        
        return payload;
    }

    public Payload createPayload (String name, String mime, byte[] content)
        throws Exception {
        return createPayload (name, mime, new ByteArrayInputStream (content));
    }

    public Payload createPayload (String name, String mime, String content)
        throws Exception {
        return createPayload (name, mime, content.getBytes("utf8"));
    }
    
    /**
     * Create a payload from a form submission. If there is no
     * multi-part data associated with that field, returns null.
     * 
     * @param field
     * @param request
     * @return
     * @throws IOException
     */
    public Payload parseMultiPart (String field, Http.Request request)
        throws IOException {
        
        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart part = null;    
        if (body != null) {
            part = body.getFile(field);
            if (part == null) {
                Logger.warn("Unable to parse field "
                            +field+" in multi-part request!");
                return null;
            }
        }
        else {
            Logger.warn("Request is not multi-part!");
            return null;
        }
        Logger.debug("file="+part.getFilename()
                     +" content="+part.getContentType());
        
        Payload payload = null;
        try {
            payload = createPayload (part.getFilename(),
                                     part.getContentType(),
                                     new FileInputStream (part.getFile()));
        }
        catch (Throwable t) {
            Logger.trace("Can't save payload", t);
        }
        
        return payload;
    }

    public File getPayload (Payload pl) {
        File file = new File (ctx.payload, pl.id.toString());
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public InputStream getPayloadAsStream (Payload pl) {
        File file = getPayload (pl);
        if (file != null) {
            try {
                
                return new FileInputStream (file);
            }
            catch (IOException ex) {
                Logger.trace("Can't open file "+file, ex);
            }
        }
        return null;
    }
    public static void qq(InputStream is) throws IOException{
        System.out.println("########## SDF");
        BufferedReader buff = new BufferedReader(new InputStreamReader(is));
        String line;
        int c=0;
                
        while((line=buff.readLine())!=null){
            System.out.println(line);
            c++;
            if(c>10)break;
        }
    }
    
    public InputStream getPayloadAsStreamUncompressed(Payload pl){
        try{
            return getPayloadAsStreamUncompressedT(pl);
        }catch(Exception e){

        }
        return null;
    }
    public InputStream getPayloadAsStreamUncompressedT(Payload pl){
        

        InputStream is = getPayloadAsStream(pl);
        if(is==null)return null;
        try {

            return ix.utils.Util.getUncompressedInputStreamRecursive(is);
        } catch (IOException e) {
            Logger.trace("Problem uncompressing stream", e);
        }
        return null;
    }
}
