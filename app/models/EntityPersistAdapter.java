package models;

import play.Logger;
import java.util.Date;
import java.lang.reflect.*;
import com.avaje.ebean.event.*;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.core.*;
import utils.Global;

public class EntityPersistAdapter extends BeanPersistAdapter {

    public EntityPersistAdapter () {
    }

    public boolean isRegisterFor (Class<?> cls) {
        return cls.isAnnotationPresent(Entity.class);
    }

    
    @Override
    public boolean preInsert (BeanPersistRequest<?> request) {
        Object bean = request.getBean();
        if (ETag.class.isAssignableFrom(bean.getClass())) {
            try {
                Method m = bean.getClass()
                    .getMethod("setModified", Date.class);
                m.invoke(bean, new Date ());
                Logger.debug("Updating "+((ETag)bean).etag);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Global g = Global.getInstance();
        //Logger.debug("## indexing bean "+bean+"; global="+g);
        try {
            g.getTextIndexer().add(bean);
        }
        catch (java.io.IOException ex) {
            Logger.warn("Can't index bean "+bean, ex);
        }

        return true;
    }

    @Override
    public void postInsert (BeanPersistRequest<?> request) {
    }

    @Override
    public boolean preUpdate (BeanPersistRequest<?> request) {
        Object bean = request.getBean();
        return true;
    }

    @Override
    public void postUpdate (BeanPersistRequest<?> request) {
        ObjectMapper mapper = new ObjectMapper ();
        Logger.debug(">> Old: "+mapper.valueToTree(request.getOldValues())
                     +"\n>> New: "+mapper.valueToTree(request.getBean()));
        
    }
}
