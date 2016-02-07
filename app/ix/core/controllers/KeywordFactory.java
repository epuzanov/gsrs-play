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
import com.avaje.ebean.Expr;

import ix.core.NamedResource;
import ix.core.models.Keyword;

@NamedResource(name="keywords",
               type=Keyword.class,
               description="This resource is for handling of keywords, which is a specific resource of Value.")
public class KeywordFactory extends EntityFactory {
    public static final Model.Finder<Long, Keyword> finder = 
        new Model.Finder(Long.class, Keyword.class);

    public static List<Keyword> all () { return all (finder); }
    public static Result count () { return count (finder); }
    public static Integer getCount () {
        try {
            return getCount (finder);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static Result page (int top, int skip, String filter) {
        return page (top, skip, filter, finder);
    }

    public static Result get (Long id, String select) {
        return get (id, select, finder);
    }

    public static Result field (Long id, String path) {
        return field (id, path, finder);
    }

    public static Result edits (Long id) {
        return edits (id, Keyword.class);
    }

    public static Result create () {
        return create (Keyword.class, finder);
    }

    public static Result delete (Long id) {
        return delete (id, finder);
    }

    public static Result update (Long id, String field) {
        return update (id, field, Keyword.class, finder);
    }

    public static Keyword registerIfAbsent
        (String label, String term, String url) {
        List<Keyword> keywords =
            finder.where(Expr.and(Expr.eq("label", label),
                                  Expr.eq("term", term))).findList();
        if (keywords.isEmpty()) {
            Keyword kw = new Keyword (label, term);
            kw.href = url;
            kw.save();
            return kw;
        }
        
        return keywords.iterator().next();
    }
}
