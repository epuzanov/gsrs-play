package ix.core.java8Util;

import ix.core.EntityProcessor;
import ix.core.adapters.EntityPersistAdapter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class was created so Ebean enchanced classes
 * using Play 2.3 can use Java 8 language features.
 * We have to factor out all the java 8 new language features
 * into not only a separate class but a different package
 * so they aren't "enhanced" by ebean and it's java 7
 * bytecode parser.
 * Not doing this will cause silent errors and make persisting to the database not work.
 *
 * Created by katzelda on 6/28/16.
 */
public class Java8ForOldEbeanHelper {


    private  static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * Register an EntityProcessor's  {@link EntityProcessor#prePersist(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPrePersistEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "prePersist", ep::prePersist);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#postPersist(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPostPersistEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "postPersist", ep::postPersist);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#preUpdate(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPreUpdateEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "preUpdate", ep::preUpdate);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#postUpdate(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPostUpdateEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "postUpdate", ep::postUpdate);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#preRemove(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPreRemoveEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "preRemove", ep::preRemove);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#postRemove(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPostRemoveEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "postRemove", ep::postRemove);
    }
    /**
     * Register an EntityProcessor's  {@link EntityProcessor#postLoad(Object)} method.
     * @param cls the class type of this processor
     * @param registry the registry mapping of hooks.
     * @param ep the EntityProcessor's instance.
     */
    public static void addPostLoadEntityProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, EntityProcessor ep) {
        registerProcessor(cls, registry, "postLoad", ep::postLoad);
    }


    private static void registerProcessor(Class cls, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry, String name, EntityHookMethod
            method){

        registry.computeIfAbsent(cls, k-> new ArrayList<>())
                .add(new EntityProcessorHook(method, name));

    }

    /**
     * Register the given method as an EntityPersistAdapter Hook
     * if it is annotated with the given annotation.  this method will not
     * do anything if the method does not have the given annotation.
     *
     * @param annotation the annotation to look for.
     * @param cls the class to insepct.
     * @param m the method to inspect.
     * @param registry the registry mapping of hooks.
     */
    public static void register (Class annotation,
                   Class cls, Method m, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry) {
        if (m.isAnnotationPresent(annotation)) {
//            Logger.info("Method \""+m.getName()+"\"["+cls.getName()
//                    +"] is registered for "+annotation.getName());
            convertToMethodHandle(cls, m, registry);

        }
    }

    private static void convertToMethodHandle(Class cls, Method m, Map<Class<?>, List<EntityPersistAdapter.Hook>> registry) {
        try{
            MethodHandle mh = LOOKUP.unreflect(m);

            registry.computeIfAbsent(cls, k-> new ArrayList<>())
                    .add(new MethodHandleHook(m.getName(), mh));
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @FunctionalInterface
    interface EntityHookMethod{
        void apply(Object o) throws EntityProcessor.FailProcessingException;
    }
    private static class EntityProcessorHook implements EntityPersistAdapter.Hook {
        private final EntityHookMethod delegate;
        private final String name;

        public EntityProcessorHook(EntityHookMethod method, String name){
            this.name = name;
            this.delegate = method;
        }


        @Override
        public void invoke(Object o) throws Exception {
            delegate.apply(o);
        }

        @Override
        public String getName() {
            return name;
        }

    }


    private static class MethodHandleHook implements EntityPersistAdapter.Hook {

        private final String name;
        private final MethodHandle methodHandle;

        public MethodHandleHook(String name, MethodHandle methodHandle) {
            this.name = name;
            this.methodHandle = methodHandle;
        }

        @Override
        public void invoke(Object o) throws Exception {
            try{
                methodHandle.invoke(o);
            }catch(Throwable t){
                throw new Exception(t.getMessage(), t);
            }
        }

        @Override
        public String getName() {
            return name;
        }
    }
}