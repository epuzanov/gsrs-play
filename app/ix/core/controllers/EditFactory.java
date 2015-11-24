package ix.core.controllers;

import java.io.*;
import java.util.*;

import play.*;
import play.db.ebean.*;
import play.data.*;
import play.mvc.*;
import ix.core.NamedResource;
import ix.core.models.Edit;
import ix.core.models.Figure;

import com.fasterxml.jackson.databind.ObjectMapper;

@NamedResource(name="edits",
type=Edit.class,
description="Edit history of changes to entities")
public class EditFactory extends EntityFactory {
    public static final Model.Finder<UUID, Edit> finder = 
        new Model.Finder(UUID.class, Edit.class);

    public static Result get (String uuid) {
        try {
            UUID id = UUID.fromString(uuid);
            Edit edit = finder.byId(id);
            if (edit != null) {
                ObjectMapper mapper = getEntityMapper ();
                return ok (mapper.valueToTree(edit));
            }

            return notFound ("Bad request: "+request().uri());
        }
        catch (Exception ex) {
            Logger.trace("Can't retrieve edit "+uuid, ex);
        }
        
        return internalServerError
            ("Unable to fullfil request: "+request().uri());
    }

    public static Result field (String uuid, String field) {
        try {
            UUID id = UUID.fromString(uuid);
            Edit edit = finder.byId(id);
            if (edit != null) {
                return field (edit, field);
            }
            
            return notFound ("Bad request: "+request().uri());
        }
        catch (Exception ex) {
            Logger.trace("Can't retrieve edit "+uuid, ex);
        }

        return internalServerError
            ("Unable to fullfil request: "+request().uri());
    }
    
    public static Result page (int top, int skip, String filter) {
        return page (top, skip, filter, finder);
    }

}
