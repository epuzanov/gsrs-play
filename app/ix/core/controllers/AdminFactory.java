package ix.core.controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.*;
import play.db.ebean.*;
import play.mvc.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import ix.core.models.*;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AdminFactory extends Controller {
    static final public Model.Finder<Long, Namespace> resFinder =
            new Model.Finder(Long.class, Namespace.class);
    static final public Model.Finder<Long, Acl> aclFinder =
            new Model.Finder(Long.class, Acl.class);
    static final public Model.Finder<Long, Principal> palFinder =
            new Model.Finder(Long.class, Principal.class);
    static final public Model.Finder<Long, SecurityRole> roleFinder =
            new Model.Finder(Long.class, SecurityRole.class);

    @BodyParser.Of(value = BodyParser.Json.class)
    public static Result createUser() {
        if (request().body().isMaxSizeExceeded()) {
            return badRequest("Json content too large!");
        }

        Principal pal = null;
        try {
            JsonNode node = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            pal = mapper.treeToValue(node, Principal.class);
            pal.save();

            return ok(mapper.valueToTree(pal));
        } catch (Exception ex) {
            Logger.error("Can't create new user", ex);
            if (pal != null)
                return badRequest
                        ("Principal \"" + pal.uri + "\" is not available!");

            return internalServerError
                    ("Unable to process request due to internal server error!");
        }
    }

    public static Result getUser(Long id) {
        Principal pal = palFinder.byId(id);
        if (pal != null) {
            ObjectMapper mapper = new ObjectMapper();
            return ok(mapper.valueToTree(pal));
        }
        return notFound("Unknown principal: " + id);
    }

    public static Result deleteUser(Long id) {
        Principal pal = palFinder.byId(id);
        if (pal != null) {
            try {
                pal.delete();
                ObjectMapper mapper = new ObjectMapper();
                return ok(mapper.valueToTree(pal));
            } catch (Exception ex) {
                return badRequest(ex.getMessage());
            }
        }
        return notFound("Unknown principal: " + id);
    }

    public static Result getRole(Long id) {
        SecurityRole role = roleFinder.byId(id);
        if (role != null) {
            ObjectMapper mapper = new ObjectMapper();
            return ok(mapper.valueToTree(role));
        }
        return notFound("Unknown role: " + id);
    }

    public static Result deleteRole(Long id) {
        SecurityRole role = roleFinder.byId(id);
        if (role != null) {
            try {
                role.delete();
                ObjectMapper mapper = new ObjectMapper();
                return ok(mapper.valueToTree(role));
            } catch (Exception ex) {
                return badRequest(ex.getMessage());
            }
        }
        return notFound("Unknown role: " + id);
    }

    public static Result getNamespace(Long id) {
        Namespace res = resFinder.byId(id);
        if (res != null) {
            ObjectMapper mapper = new ObjectMapper();
            return ok(mapper.valueToTree(res));
        }
        return notFound("Unknown resource: " + id);
    }

    public static Result deleteNamespace(Long id) {
        Namespace res = resFinder.byId(id);
        if (res != null) {
            try {
                //res.save("archive");

                res.delete();
                ObjectMapper mapper = new ObjectMapper();
                return ok(mapper.valueToTree(res));
            } catch (Exception ex) {
                Logger.error("Can't delete Resource " + id, ex);
                return badRequest(ex.getMessage());
            }
        }
        return notFound("Unknown resource: " + id);
    }

    static String pkey() {
        return random(16);
    }

    static String random(int length) {
        java.security.SecureRandom rand = new java.security.SecureRandom();
        byte[] buf = new byte[length / 2];
        rand.nextBytes(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; ++i) {
            sb.append(String.format("%1$02x", buf[i] & 0xff));
        }
        return sb.toString();
    }

    public static Result createTest1() {

        Transaction tx = Ebean.beginTransaction();
        Principal user1 = new Principal("jdoe@army.mil");
        user1.username = "Joe Doe";

        Principal user2 = new Principal("jdoe@navy.mil");
        user2.username = "Jane Doe";

        Principal user3 = new Principal("jdoe@marine.mil");
        user3.username = "Jimmy Doe";

        Principal user4 = new Principal(true, "jdean@nsa.mil");
        user4.username = "Jimmy Dean";

        Principal user5 = new Principal();

        Group group1 = new Group("Illuminati");
        group1.members.add(user1);
        group1.members.add(user3);

        Group group2 = new Group("FreeMason");
        group2.members.add(user2);
        group2.members.add(user3);
        group2.members.add(user4);

        Group group3 = new Group("FaternalOrder");
        group3.members.add(user1);
        group3.members.add(user2);
        group3.members.add(user3);

        Acl acl1 = Acl.newRead();
        acl1.principals.add(user4);
        acl1.groups.add(group1);

        Acl acl2 = Acl.newWrite();
        acl2.principals.add(user2);

        Acl acl3 = Acl.newAdmin();
        acl3.groups.add(group3);

        Acl acl4 = Acl.newReadWrite();
        acl4.principals.add(user1);
        acl4.principals.add(user3);

        Acl acl5 = Acl.newExecute();
        acl5.groups.add(group2);

        Acl acl6 = Acl.newRead();
        acl6.principals.add(user1);
        acl6.principals.add(user2);
        acl6.principals.add(user3);

        Acl acl7 = Acl.newNone();
        acl7.principals.add(user4);

        Acl acl8 = Acl.newNone();
        acl8.principals.add(user1);
        acl8.principals.add(user5);

        SecurityRole role1 = SecurityRole.newGuest();
        role1.principal = user1;

        SecurityRole role2 = SecurityRole.newUser();
        role2.principal = user2;

        SecurityRole role3 = SecurityRole.newOwner();
        role3.principal = user3;

        SecurityRole role4 = SecurityRole.newAdmin();
        role4.principal = user4;

        tx.commit();

        return ok("Test1 data created!");
    }

    public static Result get(String name) {
        Namespace resource = resFinder
                .where().eq("name", name)
                .findUnique();
        if (resource != null) {
            ObjectMapper mapper = new ObjectMapper();
            return ok(mapper.valueToTree(resource));
        }
        return badRequest("Unknown resource: " + name);
    }

    public static List<SecurityRole> rolesByPrincipal(Principal cred) {
        Long userId = cred.id;
        return roleFinder.where().eq("principal.id", cred.id).findList();
    }


    public static List<Acl> permissionByPrincipal(Principal cred) {
        Long userId = cred.id;
        List<Acl> perByUser = new ArrayList<>();
        String sql = "select IX_CORE_ACL_ID from IX_CORE_ACL_PRINCIPAL ap where ap.IX_CORE_PRINCIPAL_ID = :userId";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("userId", userId);
        Set<SqlRow> sqlRows = sqlQuery.findSet();

        for (SqlRow row : sqlRows) {
            perByUser.add(aclFinder.byId((Long) row.get("ix_core_acl_id")));
        }
        return perByUser;
    }
}
