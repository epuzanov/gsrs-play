package ix.idg.controllers;

import com.avaje.ebean.Expr;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ix.core.controllers.PredicateFactory;
import ix.core.models.Predicate;
import ix.core.models.Session;
import ix.core.models.XRef;
import ix.core.plugins.IxCache;
import ix.idg.models.Disease;
import ix.idg.models.Ligand;
import ix.idg.models.Target;
import ix.ncats.controllers.App;
import play.Logger;
import play.db.ebean.Model;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class DossierApp extends App implements Commons {
    public static final String IDG_BUCKET = "IDG BUCKET";
    
    static final String SESSION_ID = "idgsession";
    static final Model.Finder<Long, Session> finder =
        new Model.Finder(UUID.class, Session.class);

    @Transactional
    static Session newSession () {
        Session session = new Session ();
        String where = request().getHeader("X-Real-IP");
        if (where == null)
            where = request().remoteAddress();
        session.location = where;
        session.save();
        session().put(SESSION_ID, session.id.toString());        
        Logger.debug("New session "+session.id+" created!");
        return session;
    }

    public static Session getSession () {
        final String id = session().get(SESSION_ID);
        try {
            Session session = null;
            if (id == null) {
                session = newSession ();
            }
            else {
                session = getOrElse (id, new Callable<Session>() {
                        public Session call () throws Exception {
                            List<Session> sessions =  finder.where()
                                .eq("id", id).findList();
                            Session session = null;
                            if (!sessions.isEmpty()) {
                                session = sessions.iterator().next();
                            }
                            return session;
                        }
                    });
                
                if (session == null) {
                    Logger.warn("Bogus session requested: "+id);
                    IxCache.remove(id);
                    session = newSession ();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't create session", ex);
        }
        return null;
    }

    @Transactional
    public static <T> boolean add (final Session session, T entity) {
        try {
            Predicate pred = getOrElse
                (session.id.toString(), new Callable<Predicate>() {
                        public Predicate call () throws Exception {
                            List<Predicate> buckets =
                            PredicateFactory.finder.where
                            (Expr.and(Expr.eq("predicate", IDG_BUCKET),
                                      Expr.eq("subject.refid",
                                              session.id.toString())))
                            .findList();
                            Predicate bucket = null;
                            if (buckets.isEmpty()) {
                                bucket = new Predicate (IDG_BUCKET);
                                bucket.subject = new XRef (session);
                                bucket.save();
                            }
                            else {
                                bucket = buckets.iterator().next();
                            }
                            return bucket;
                        }
                    });
            XRef ref = new XRef (entity);
            if (ref != pred.addIfAbsent(ref)) {
                Logger.debug(ref.kind+": "+ref.refid
                             +" already exist in bucket "+pred.id);
                // already have it
                return false;
            }
            pred.update();
            Logger.debug(ref.kind+": "+ref.refid+" added to bucket "+pred.id);
            
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't update bucket", ex);
        }
        return false;
    }

    static ObjectMapper mapper = new ObjectMapper();

    public static Result error(int code, String mesg) {
        return ok(ix.idg.views.html.error.render(code, mesg));
    }

    public static Result _notFound(String mesg) {
        return notFound(ix.idg.views.html.error.render(404, mesg));
    }

    public static Result _badRequest(String mesg) {
        return badRequest(ix.idg.views.html.error.render(400, mesg));
    }

    public static Result _internalServerError(Throwable t) {
        t.printStackTrace();
        return internalServerError
                (ix.idg.views.html.error.render
                        (500, "Internal server error: " + t.getMessage()));
    }

    static ArrayNode getCartFromSession() throws IOException {
        String cartStr = session().get("cart");
        // if we get nothing, we create an empty cart with a single folder
        ArrayNode cart;
        if (cartStr == null) {
            cart = mapper.createArrayNode();
            ObjectNode folder = mapper.createObjectNode();
            folder.put("folder", "Default");
            folder.put("entities", mapper.createArrayNode());
            cart.add(folder);
        } else cart = (ArrayNode) mapper.readTree(cartStr);
        return cart;
    }

    static ObjectNode getFolderFromCart(ArrayNode cart, String folderName) {
        ObjectNode folderNode = null;
        for (int i = 0; i < cart.size(); i++) {
            ObjectNode tmp = (ObjectNode) cart.get(i);
            if (tmp.get("folder").textValue().equals(folderName)) {
                folderNode = tmp;
                break;
            }
        }
        return folderNode;
    }

    static boolean cartContains(ArrayNode cart, String folder, String type, String id) {
        ObjectNode folderNode = null;
        for (int i = 0; i < cart.size(); i++) {
            ObjectNode tmp = (ObjectNode) cart.get(i);
            if (tmp.get("folder").textValue().equals(folder)) {
                folderNode = tmp;
                break;
            }
        }
        if (folderNode == null) return false;
        ArrayNode entities = (ArrayNode) folderNode.get("entities");
        for (int i = 0; i < entities.size(); i++) {
            ObjectNode node = (ObjectNode) entities.get(i);
            if (node.get("type").textValue().equals(type) && node.get("entity").textValue().equals(id)) return true;
        }
        return false;
    }

    static int _countEntities() throws IOException {
        ArrayNode cart = getCartFromSession();
        int n = 0;
        for (int i = 0; i < cart.size(); i++) {
            ObjectNode folder = (ObjectNode) cart.get(i);
            ArrayNode entities = (ArrayNode) folder.get("entities");
            n += entities.size();
        }
        return n;
    }

    static int _countFolders() throws IOException {
        ArrayNode cart = getCartFromSession();
        return cart.size();
    }

    public static List<String> getFolderNames() throws IOException {
        ArrayNode cart = getCartFromSession();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < cart.size(); i++) {
            ObjectNode tmp = (ObjectNode) cart.get(i);
            String name = tmp.get("folder").textValue();
            if (!name.toLowerCase().equals("default")) names.add(name);
        }
        return names;
    }

    // TODO should return a more informative json structure
    public static Result count() throws IOException {
        response().setContentType("text/plain");
        return ok(String.valueOf(_countEntities()));
    }

    @BodyParser.Of(value = BodyParser.FormUrlEncoded.class,
            maxLength = 100000)
    public static Result addEntities() throws IOException {
        if (request().body().isMaxSizeExceeded()) {
            return badRequest("Input is too large!");
        }

        Map<String, String[]> params = request().body().asFormUrlEncoded();
        String[] tmp = params.get("json");
        if (tmp.length != 1) return _badRequest("Must specify a single json argument");
        String json = tmp[0];
        if (json.equals("")) return _badRequest("Must specify a JSON string");
        ArrayNode root = (ArrayNode) mapper.readTree(json);

        // get which folder we want it in. If nothing is specified go in to Default
        String folderName = "Default";
        tmp = params.get("folder");
        if (tmp.length != 1) return _badRequest("Must specify a single folder argument");
        folderName = tmp[0];

        // get cart from the seesion
        ArrayNode cart = getCartFromSession();

        ObjectNode folder = getFolderFromCart(cart, folderName);
        if (folder == null) {
            if (folderName.trim().equals(""))
                return _badRequest("Must specify a non-empty folder name");
            folder = mapper.createObjectNode();
            folder.put("folder", folderName);
            folder.put("entities", mapper.createArrayNode());
            cart.add(folder);
            Logger.debug("Added new dossier: "+folderName);
        }

        ArrayNode entities = (ArrayNode) folder.get("entities");
        // add any new entities
        for (int i = 0; i < root.size(); i++) {
            ObjectNode node = (ObjectNode) root.get(i);
            if (cartContains(cart, folderName, node.get("type").textValue(), node.get("entity").textValue()))
                continue;
            entities.add(node);
        }

        // write cart back to session
        session().put("cart", mapper.writeValueAsString(cart));

        // send back summary of what we have
        ObjectNode ret = mapper.createObjectNode();
        ret.put("total_entries", _countEntities());
        ret.put("total_collections", _countFolders());
        return ok(ret);
    }

    public static Result view(String folderName) throws IOException {
        if (folderName == null) folderName = "Default";

        ArrayNode cart = getCartFromSession();

        // get all folder names
        List<String> folderNames = new ArrayList<>();
        folderNames.add("Default");
        for (int i = 0; i < cart.size(); i++) {
            ObjectNode folder = (ObjectNode) cart.get(i);
            String name = folder.get("folder").textValue();
            if (name.equals("Default")) continue;
            else folderNames.add(name);
        }

        if (!folderNames.contains(folderName))
            return _notFound("No collection found with name " + folderName);

        // for the current folder pull in the various entities into separate arrays
        ObjectNode folder = getFolderFromCart(cart, folderName);
        ArrayNode entities = (ArrayNode) folder.get("entities");

        Model.Finder<Long, Target> targetFinder = TargetFactory.finder;
        Model.Finder<Long, Disease> diseaseFinder = DiseaseFactory.finder;
        Model.Finder<Long, Ligand> ligandFinder = LigandFactory.finder;

        List<Target> targets = new ArrayList<>();
        List<Disease> diseases = new ArrayList<>();
        List<Ligand> ligands = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            ObjectNode node = (ObjectNode) entities.get(i);
            String type = node.get("type").textValue();
            String id = node.get("entity").textValue();
            if (type.equals(Target.class.getName())) {
                targets.addAll(targetFinder.where(Expr.and(Expr.eq("synonyms.label", Commons.UNIPROT_ACCESSION),
                        Expr.eq("synonyms.term", id))).findList());
            }
//            else if (type.equals(Disease.class.getName()))
//                diseases.addAll(diseaseFinder.where(Expr.and(Expr.eq("synonyms.label", "DOID"),
//                        Expr.eq("synonyms.term", id))).findList());
            // TODO ligands
        }

        return ok(ix.idg.views.html.cart.render(folderName, folderNames, targets, diseases, ligands, null));
    }

    public static Result emptyCart () throws IOException {
        session().remove("cart");
        ArrayNode cart = getCartFromSession();
        session().put("cart", mapper.writeValueAsString(cart));
        return ok ("Cart emptied");
    }
}
