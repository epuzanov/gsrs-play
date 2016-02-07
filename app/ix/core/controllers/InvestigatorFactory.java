package ix.core.controllers;

import java.io.*;
import java.security.*;
import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import play.*;
import play.db.ebean.*;
import play.data.*;
import play.mvc.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.avaje.ebean.*;

import ix.core.NamedResource;
import ix.core.models.Investigator;


@NamedResource(name="investigators",
               type=Investigator.class,
               description="This resource provides investigator's contact information")
public class InvestigatorFactory extends EntityFactory {
    public static Model.Finder<Long, Investigator> finder = 
        new Model.Finder (Long.class, Investigator.class);

    public static List<Investigator> all () { return all (finder); }
    public static Result count () { return count (finder); }
    public static Result page (int top, int skip, String filter) {
        return page (top, skip, filter, finder);
    }

    public static Result get (Long id, String select) {
        return get (id, select, finder);
    }

    public static Result field (Long id, String path) {
        return field (id, path, finder);
    }

    public static Integer getCount () {
        try {
            return getCount (finder);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Result create () {
        return create (Investigator.class, finder);
    }

    public static Result delete (Long id) {
        return delete (id, finder);
    }

    public static Result update (Long id, String field) {
        return update (id, field, Investigator.class, finder);
    }
}
