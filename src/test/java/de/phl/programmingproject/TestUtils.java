package de.phl.programmingproject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utility class for tests.
 */
public class TestUtils {

    /**
     * The user's working directory.
     */
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    /**
     * Returns true if the given file exists in the root directory or in the src directory.
     *
     * @param fileName
     * @return true if the given file exists in the root directory or in the src directory.
     */
    public static boolean fileExistsInRootOrSrcDirectory(String fileName) {
        return Files.exists(Paths.get(WORKING_DIRECTORY, fileName)) ||
                Files.exists(Paths.get(WORKING_DIRECTORY, "./src/" + fileName));
    }

    /**
     * Executes the given action and returns the output that was printed to the system out.
     *
     * @param actionThatPrintsToSystemOut
     * @return
     */
    public static String runActionAndGetSystemOut(Runnable actionThatPrintsToSystemOut) {
        // Save the original System.out
        PrintStream originalOut = System.out;

        // Create a stream to hold the output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8);

        // Set the new stream as the standard out
        System.setOut(newOut);

        // Call the action that prints to the system out
        try {
            actionThatPrintsToSystemOut.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString(java.nio.charset.StandardCharsets.UTF_8).replace("\r\n", "\n");
        return output;
    }

    /**
     * Gets the content of the given file from the root or src directory.
     *
     * @param fileName
     * @return
     */
    public static String getFileContentForFileInRootOrSrcDirectory(String fileName) {
        if (!fileExistsInRootOrSrcDirectory(fileName)) {
            fail(String.format("The file '%s' does not exist in the root (or './src') directory of the project.", fileName));
        }
        Path p1 = Paths.get(TestUtils.WORKING_DIRECTORY, fileName);
        Path p2 = Paths.get(TestUtils.WORKING_DIRECTORY, "./src/" + fileName);
        Path path = Files.exists(p1) ? p1 : p2;
        // load content of the file
        String txt;
        try {
            txt = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return txt;
    }

    /**
     * Returns the {@link Class<?>} with the given name in the given package. It fails if the class does not exist.
     *
     * @param className
     * @param packageName
     * @return the {@link Class<?>} with the given name in the given package, or fails the test if the class does not exist
     */
    public static Class<?> getClassForName(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            fail(String.format("Class '%s' does not exist in the package '%s'", className, packageName));
        }
        return null;
    }

    public static Class<?> getClassForName(String fqn) {
        String className = fqn.substring(fqn.lastIndexOf(".") + 1);
        String packageName = fqn.substring(0, fqn.lastIndexOf("."));
        return getClassForName(className, packageName);
    }

    /**
     * Returns the enum value with the given name. It fails if the enum does not contain the value.
     *
     * @param enumClazz
     * @param name
     * @return the enum value with the given name, or fails the test if the enum does not contain the value
     */
    public static Object getEnumValue(Class<?> enumClazz, String name) {
        try {
            return enumClazz.getDeclaredField(name).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(String.format("Enum '%s' does not contain the value '%s'", enumClazz.getSimpleName(), name));
        }
        return null;
    }

    /**
     * Asserts that the given class has the given field with the given type.
     *
     * @param clazz
     * @param fieldName
     * @param fieldType
     */
    public static void assertClassHasFieldOfType(Class<?> clazz, String fieldName, Class<?> fieldType) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                assertTrue(fieldType.isAssignableFrom(field.getType()),
                        String.format("The field '%s' of the class '%s' has the wrong type. The type '%s' was expected.", fieldName,
                                clazz.getSimpleName(), fieldType));
                return;
            }
        }
    }

    /**
     * Asserts that the given class has the given fields with the given types.
     *
     * @param clazz
     * @param expectedFieldsMap
     */
    public static void assertClassHasFieldsOfType(Class<?> clazz, Map<String, Class<?>> expectedFieldsMap) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Class<?>> fieldsMap = new HashMap<>();
        for (Field field : fields) {
            fieldsMap.put(field.getName().toLowerCase(), field.getType());
        }

        for (Map.Entry<String, Class<?>> expectedClassEntry : expectedFieldsMap.entrySet()) {
            assertTrue(fieldsMap.containsKey(expectedClassEntry.getKey().toLowerCase()),
                    String.format("The field '%s' of the class '%s' does not exist.", expectedClassEntry.getKey(),
                            clazz.getSimpleName()));

            Class<?> actualType = fieldsMap.get(expectedClassEntry.getKey().toLowerCase());
            assertTrue(expectedClassEntry.getValue().isAssignableFrom(actualType),
                    String.format("The field '%s' of the class '%s' has the wrong type. The type '%s' was expected, but it was '%s'.", expectedClassEntry.getKey(),
                            clazz.getSimpleName(), expectedClassEntry.getValue(), actualType.getSimpleName()));
        }
    }

    /**
     * Asserts that the given class has the given method with the given return type and parameter types.
     *
     * @param clazz
     * @param methodName
     * @param returnType
     * @param parameterTypes
     */
    public static void assertClassHasMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... parameterTypes) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(methodName)) {
                assertTrue(returnType.isAssignableFrom(declaredMethod.getReturnType()),
                        String.format("The return type of method '%s' is wrong. Expected '%s', but was '%s'.",
                                methodName, returnType, declaredMethod.getReturnType()));
                assertTrue(declaredMethod.getParameterCount() == parameterTypes.length,
                        String.format("The method '%s' has the wrong number of parameters. Expected '%s', but was '%s'.",
                                methodName,
                                declaredMethod.getParameterCount(), parameterTypes.length));

                Class<?>[] actualParameterTypes = declaredMethod.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    assertTrue(actualParameterTypes[i].equals(parameterTypes[i]),
                            String.format("The parameter type of parameter '%s' of method '%s' is wrong. Expected '%s', but was '%s'.",
                                    i, methodName, parameterTypes[i], actualParameterTypes[i]));
                }
                return;
            }
        }

        fail(String.format("The class '%s' does not contain a method '%s' with return type '%s' and parameters of type '%s'", clazz.getSimpleName(), methodName,
                returnType, Arrays.toString(parameterTypes)));
    }

    /**
     * Returns the method with the given name and parameter types. It fails if the method does not exist.
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            if (parameterTypes == null || parameterTypes.length == 0) {
                method = clazz.getDeclaredMethod(methodName);
            } else {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            }
        } catch (NoSuchMethodException e) {
            fail(String.format("The class '%s' does not contain a method '%s' with parameters of type '%s' (i.e., %s)", clazz.getSimpleName(), methodName,
                    Arrays.toString(parameterTypes),
                    String.format("'%s(%s)'", methodName, Arrays.toString(parameterTypes)).replace("[", "").replace("]", "")));
        }
        // make the method accessible (e.g., when it is private or protected)
        if(null != method){
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * Invokes the given method of the given object with the given arguments.
     * @param object
     * @param methodName
     * @param args
     * @return
     */
    public static Object invokeMethod(Object object, String methodName, Object... args){
        Class<?>[] params = new Class<?>[args.length];
        Class<?> clazz = object.getClass();

        for (int i = 0; i < args.length; i++) {
            params[i] = toPrimitiveType(args[i].getClass());
        }
        Method method = getMethod(object.getClass(), methodName, params);
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
            fail(String.format("Could not invoke method '%s' of class '%s' with arguments '%s'", methodName, clazz.getSimpleName(), Arrays.toString(args)));
        }
        return null;
    }

    /**
     * Returns the field with the given name. It fails if the field does not exist.
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            fail(String.format("The class '%s' does not contain a field '%s'", clazz.getSimpleName(), fieldName));
        }
        return field;
    }

    /**
     * Returns and asserts that the given class has the given constructor with the given parameter types.
     *
     * @param clazz
     * @param parameterTypes
     * @return
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail(String.format("The class '%s' does not contain a constructor with parameters of type '%s'", clazz.getSimpleName(),
                    Arrays.toString(parameterTypes)));
        }
        return constructor;
    }

    /**
     * Creates an instance of the given class with the given arguments.
     * The method asserts that the class has a constructor with the given parameter types.
     *
     * @param clazz
     * @param args
     * @return
     */
    public static Object createInstance(Class<?> clazz, Object... args) {
        Constructor<?> constructor = null;
        try {
            Class<?> params[] = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> argsClass = args[i].getClass();
                Class<?> type = toPrimitiveType(argsClass);

                params[i] = type;
            }
            constructor = getConstructor(clazz, params);
        } catch (Exception e) {
            System.err.println(e);
            fail(String.format("The class '%s' does not contain a constructor with parameters of type '%s'", clazz.getSimpleName(),
                    Arrays.toString(args)));
        }

        Object instance = null;
        try {
            instance = constructor.newInstance(args);
        } catch (Exception e) {
            System.err.println(e);
            fail("Could not create instance of class '" + clazz.getSimpleName() + "' with the given arguments " + Arrays.toString(args));
        }
        return instance;
    }

    /**
     * Converts the given class to its primitive type if it is a wrapper type.
     * @param clazz
     * @return
     */
    static Class<?> toPrimitiveType(Class<?> clazz) {
        if (clazz.equals(Integer.class)) {
            return int.class;
        } else if (clazz.equals(Double.class)) {
            return double.class;
        } else if (clazz.equals(Boolean.class)) {
            return boolean.class;
        }
        return clazz;
    }

    /**
     * Asserts that the system output after running the action matches the given regular expression.
     * This method invokes {@link #assertSystemOutMatchesRegex(Runnable, String, String, boolean)} with <code>printOutput = true</code>.
     * @param action
     * @param regex
     * @param failMessage
     */
    public static void assertSystemOutMatchesRegex(Runnable action, String regex, String failMessage) {
        assertSystemOutMatchesRegex(action, regex, failMessage, true);
    }

    /**
     * Asserts that the system output after running the action matches the given regular expression.
     * @param action The action to run that prints to the system out.
     * @param regex The regular expression that the output should match.
     * @param failMessage The message that is printed if the assertion fails.
     * @param printOutput Whether the output should be printed in the console if the assertion fails.
     */
    public static void assertSystemOutMatchesRegex(Runnable action, String regex, String failMessage, boolean printOutput) {
        String output = runActionAndGetSystemOut(action);
        assertTrue(Pattern.compile(regex).matcher(output).find(),
                printOutput? String.format("%s\n%s\n%s", failMessage,"The output was:", output) : failMessage);
    }

    /**
     * Asserts that the content of the file with the given name in the root or src directory matches the given regular expression.
     * This method invokes {@link #assertFileContentMatchesRegex(String, String, String, boolean)} with <code>printContent = true</code>.
     * @param fileName
     * @param regex
     * @param failMessage
     */
    public static void assertFileContentMatchesRegex(String fileName, String regex, String failMessage) {
      assertFileContentMatchesRegex(fileName, regex, failMessage, true);
    }

    /**
     * Asserts that the content of the file with the given name in the root or src directory matches the given regular expression.
     * @param fileName The name of the file.
     * @param regex The regular expression that the content of the file should match.
     * @param failMessage The message that is printed if the assertion fails.
     * @param printContent Whether the content of the file should be printed in the console if the assertion fails.
     */
    public static void assertFileContentMatchesRegex(String fileName, String regex, String failMessage, boolean printContent) {
        String fileContent = getFileContentForFileInRootOrSrcDirectory(fileName);
        assertTrue(Pattern.compile(regex).matcher(fileContent).find(),
                printContent? String.format("%s\n%s\n%s", failMessage, "The content of the file was:", fileContent) : failMessage);
    }
    /**
     * Asserts that the file with the given name exists in the root or src directory.
     * @param fileName
     */
    public static void assertFileExistsInRootOurSrcDirectory(String fileName){
        assertTrue(fileExistsInRootOrSrcDirectory(fileName),
                String.format("The file '%s' does not exist in the root (or './src') directory of the project.", fileName));
    }
}