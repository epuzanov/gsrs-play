package ix.core.controllers;

import java.io.*;
import java.security.*;
import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import play.*;
import play.db.ebean.*;
import play.data.*;
import play.mvc.*;
import ix.core.models.Figure;
import ix.core.models.Payload;
import ix.core.NamedResource;
import ix.core.plugins.PayloadPlugin;

@NamedResource(name="payload",
               type=Payload.class,
               description="Resource for handling payload")
public class PayloadFactory extends EntityFactory {
    public static final Model.Finder<UUID, Payload> finder = 
        new Model.Finder(UUID.class, Payload.class);
    static PayloadPlugin payloadPlugin =
        Play.application().plugin(PayloadPlugin.class);

    public static Payload getPayload (UUID id) {
        return getEntity (id, finder);
    }
    
    public static Result count () { return count (finder); }
    public static Result page (int top, int skip, String filter) {
        return page (top, skip, filter, finder);
    }

    public static class FileWithNewName extends File{
    	String name;
    	public FileWithNewName(File f, String name){
    		super(f.toURI());
    		this.name=name;
    	}
		public String getName() {
			return name;
		}
    }
    
    public static Result get (UUID id, String select) {
        String format = request().getQueryString("format");
        if (format != null) {
        	if (format.equalsIgnoreCase("raw")) {
                Payload payload = finder.byId(id);
                if (payload != null) {
                    response().setContentType(payload.mimeType);
                    return ok (new FileWithNewName(getFile(payload), payload.name));
                }
            }
            else {
                return badRequest ("Unknown format \""+format+"\"!");
            }
        }
        return get (id, select, finder);
    }

    public static Result field (UUID id, String path) {
        return field (id, path, finder);
    }

    public static Result create () {
        throw new UnsupportedOperationException
            ("create operation not supported!");
    }

    public static Result delete (UUID id) {
        throw new UnsupportedOperationException
            ("delete operation not supported!");
    }

    public static File getFile (UUID id) {
        Payload payload = getPayload (id);
        if (payload != null) {
            return payloadPlugin.getPayloadFile(payload);
        }
        return null;
    }

    public static File getFile (Payload payload) {
        return payloadPlugin.getPayloadFile(payload);
    }

    public static InputStream getStream (String id) {
        return getStream (UUID.fromString(id));
    }
    
    public static InputStream getStream (UUID id) {
        Payload payload = getPayload (id);
        if (payload != null)
            return payloadPlugin.getPayloadAsStreamUncompressed(payload);
        return null;
    }
    
    /**
     * Get an input stream based on the payload, uncompressing
     * if necessary
     *  
     * @param payload
     * @return
     */
    public static InputStream getStream (Payload payload) {
        if (payload.id != null)
            return getStream (payload.id);
        throw new IllegalArgumentException ("Invalid payload with no id!");
    }   
    
    public static InputStream getStreamAsIs (Payload payload) {
        Payload payload2 = getPayload (payload.id);
        if (payload2 != null)
            return payloadPlugin.getPayloadAsStream(payload2);
        return null;
    }

    public static String getString (String id) {
        return id != null ? getString (UUID.fromString(id)) : null;
    }
    
    public static String getString (UUID id) {
        InputStream is = getStream (id);
        if (is != null) {
            try {
                StringBuilder sb = new StringBuilder ();
                byte[] buf = new byte[1024];
                for (int nb; (nb = is.read(buf, 0, buf.length)) > 0; ) {
                    sb.append(new String (buf, 0, nb));
                }
                is.close();
                
                return sb.toString();
            }
            catch (IOException ex) {
                Logger.trace("Can't process stream for payload "+id, ex);
            }
        }
        return null;
    }
}
