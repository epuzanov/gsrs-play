package ix.test.ix.test;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * JUnit Runner that can find all Test classes on the classpath
 * and execute them.  Use the {@link #Config}
 * annotation to specify filtering options.
 *
 * Example:
 *
 * <pre>
 *     @RunWith(FindAllTestsRunner.class)
 @FindAllTestsRunner.Config(fullExcludePattern = {"ix\\.ntd\\..+"})
 public class PrototypeSuite {


 }
 *
 * </pre>
 *
 *
 * Created by katzelda on 5/20/16.
 */
public class FindAllTestsRunner extends Suite{
    /**
     * Annotation to add to provide configuration details.
     */
    @Retention(value= RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Config {
        /**
         * Should jar files in the class path be included.
         * @return {@code true} if jars should be included; {@code false} otherwise.
         */
        boolean includeJars() default false;

        /**
         * String Patterns of class names that should be included
         * in this suite.
         * @return an array of Strings that can be converted into Pattern objects.
         */
        String[] includePattern() default {".*Test$"};
        /**
         * String Patterns of class names that should NOT be included
         * in this suite.
         * @return an array of Strings that can be converted into Pattern objects.
         */
        String[] excludePattern() default {};
        /**
         * String Patterns of fully qualified class names, including all packages separated by "\\." that should be included
         * in this suite.
         * @return an array of Strings that can be conveted into Pattern objects.
         */
        String[] fullExcludePattern() default {};
        /**
         * String Patterns of fully qualified class names, including all packages separated by "\\." that should NOT be included
         * in this suite.
         * @return an array of Strings that can be conveted into Pattern objects.
         */
        String[] fullIncludePattern() default {".*"};
    }
    @Config
    private static class DefaultConfig {}


    private static DefaultConfig defaultConfigInstance = new DefaultConfig();


    public FindAllTestsRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        this(builder, null, getTestClasses(klass));
    }

    /**
     * Called by this class and subclasses once the classes making up the suite have been determined
     *
     * @param builder builds runners for classes in the suite
     * @param klass the root of the suite
     * @param suiteClasses the classes in the suite
     * @throws InitializationError
     */
    protected FindAllTestsRunner(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(klass, builder.runners(klass, suiteClasses));
    }


    public static Class<?>[] getTestClasses(Class<?> parentSuite) {

        Config config = parentSuite.getAnnotation(Config.class);
        if(config ==null){
            config = defaultConfigInstance.getClass().getAnnotation(Config.class);
        }
        ClassFilter filter = new ConfigClassFilter(config);

        return ClassFinder.getTestClasses(parentSuite, config.includeJars(), filter);
    }



    private static class ConfigClassFilter implements ClassFilter {
        private final  List<Pattern> includePatterns, excludePatterns, fullIncludePatterns,fullExcludePatterns;

        ConfigClassFilter(Config config) {
            includePatterns = compilePatterns(config.includePattern());
            excludePatterns = compilePatterns(config.excludePattern());

            fullIncludePatterns = compilePatterns(config.fullIncludePattern());
            fullExcludePatterns = compilePatterns(config.fullExcludePattern());
        }


        @Override
        public Optional<Class<?>>  test(String className, File root, File classFile){
            //for now don't let subclasses through...
            if(className.contains("$")){
                return Optional.empty();
            }
            if(matches(className, includePatterns) && !matches(className, excludePatterns)) {
                String fullClassName = computeFullClassName(root, classFile);
                //System.out.println("matched class checking full patterns for " + fullClassName);
                if(matches(fullClassName, fullIncludePatterns) && !matches(fullClassName, fullExcludePatterns)){
                    try {
                        Class<?> c = Class.forName(fullClassName);
                        int modifiers = c.getModifiers();
                        if( !c.isEnum() && !Modifier.isAbstract(modifiers)){
                            System.out.println("./activator -Dtestconfig=conf/ginas.conf  \"ginas/testOnly "+ fullClassName +"\"");
                           return Optional.of(c);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return Optional.empty();
        }

        private boolean matches(String input, List<Pattern> patterns){
            for(Pattern p : patterns){
                if(p.matcher(input).matches()){
                    return true;
                }
            }
            return false;
        }
    }
    private static List<Pattern> compilePatterns( String[] array) {
        if(array !=null){
            List<Pattern> includePatterns = new ArrayList<>(array.length);

            for(String pattern : array){
                includePatterns.add(Pattern.compile(pattern));
            }
            return includePatterns;
        }
        return Collections.emptyList();
    }





    private static String computeFullClassName(File root, File currentFile) {
        String name = currentFile.getName();
        String className = name.substring(0, name.lastIndexOf(".class"));

        File p = currentFile.getParentFile();

        StringBuilder builder = new StringBuilder();
        builder.append(className);
        while( p!=null && !root.equals(p)){
            builder.insert(0, '.').insert(0, p.getName());
            p = p.getParentFile();
        }

        return builder.toString();
    }


}
