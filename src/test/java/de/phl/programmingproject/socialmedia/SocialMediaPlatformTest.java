package de.phl.programmingproject.socialmedia;

import de.phl.programmingproject.TestBase;
import de.phl.programmingproject.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the SocialMediaPlatform exercise {@link Main}.
 */
public class SocialMediaPlatformTest extends TestBase {


    private int userCount;
    private Object fixturePlatform;

    Class<?> userClass;

    static Class<?> getSocialMediaPlatformClass() {
        return TestUtils.getClassForName("SocialMediaPlatform",
                "de.phl.programmingproject.socialmedia");
    }

    static Class<?> getPostClass() {
        return TestUtils.getClassForName("Post",
                "de.phl.programmingproject.socialmedia");
    }

    Object createUserObject() {
        // Ausschließlich die vorgegebene Fabrik verwenden: Die Signatur des
        // User-Konstruktors ist frei. Eine Plattform pro Test vermeidet doppelte IDs.
        if (fixturePlatform == null) fixturePlatform = createSocialMediaPlatformObject();
        try {
            Object user = TestUtils.getMethod(getSocialMediaPlatformClass(), "createUser", String.class)
                    .invoke(fixturePlatform, "Testperson " + userCount++);
            assertNotNull(user, "Implementieren Sie createUser aus Aufgabe 7 als Testvoraussetzung.");
            return user;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Testperson konnte über createUser (Aufgabe 7) nicht erzeugt werden.", e);
        }
    }

    Object createSocialMediaPlatformObject() {
        try {
            return getSocialMediaPlatformClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("SocialMediaPlatform benötigt den vorgegebenen öffentlichen, parameterlosen Konstruktor.", e);
        }
    }

    @BeforeEach
    void setup() {
        userClass = TestUtils.getClassForName("User",
                "de.phl.programmingproject.socialmedia");
    }

    @Test
    public void task_1_User_class_with_properties_implemented() {

        Map<String, Class<?>> expectedFields = new LinkedHashMap() {
            {
                put("id", int.class);
                put("username", String.class);
                put("followers", Set.class);
                put("following", Set.class);
                put("posts", List.class);
            }
        };

        TestUtils.assertClassHasFieldsOfType(userClass, expectedFields);
    }

    @Test
    void task_2_Post_class_with_properties_implemented() {
        Class<?> postClass = getPostClass();

        assertNotNull(postClass, "The class 'Post' does not exist.");

        /**
         *     * `id` (int) - a unique identifier for each post. _Hint:_ create the ID in the constructor using an additional static variable to count.
         *     * `text` (String) - the text of the post
         *     * `author` (User) - the user who authored the post
         *     * `timestamp` (Date) - the time the post was created
         */
        Map<String, Class<?>> expectedFields = new LinkedHashMap() {
            {
                put("id", int.class);
                put("text", String.class);
                put("author", userClass);
                put("timestamp", Date.class);
            }
        };

        TestUtils.assertClassHasFieldsOfType(postClass, expectedFields);
    }

    @Test
    void task_3_User_class_implements_createPost() {
        TestUtils.assertClassHasMethod(userClass, "createPost", void.class, String.class);
        Method createPostMethod = TestUtils.getMethod(userClass, "createPost", String.class);


        Object userObject = createUserObject();

        // try to create a post and check the list of posts
        try {
            createPostMethod.invoke(userObject, "Post");

            Field postsField = TestUtils.getField(userClass, "posts");
            List<?> posts = (List<?>) postsField.get(userObject);
            assertEquals(1, posts.size(), "The 'createPost' method of the 'User' class does not add the post to the list of posts.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to create the post and add it to the list of posts. \n" + e);
        }
    }

    @Test
    void task_4_User_class_implements_follow() throws ReflectiveOperationException {
        TestUtils.assertClassHasMethod(userClass, "follow", void.class, userClass);
        Method follow = TestUtils.getMethod(userClass, "follow", userClass);
        Object user = createUserObject();
        Object other = createUserObject();
        Field following = TestUtils.getField(userClass, "following");
        Field followers = TestUtils.getField(userClass, "followers");

        follow.invoke(user, other);
        assertEquals(Set.of(other), following.get(user), "following muss die gefolgte Person enthalten.");
        assertEquals(Set.of(user), followers.get(other), "Die andere Person muss den Follower erhalten.");
        for (Object invalid : new Object[]{user, other, null}) {
            assertThrows(IllegalArgumentException.class, () -> invokeUnwrapped(follow, user, invalid),
                    "Selbstfolgen, erneutes Folgen und null müssen eine IllegalArgumentException auslösen.");
            assertEquals(Set.of(other), following.get(user), "Ein abgewiesener Aufruf darf following nicht verändern.");
            assertEquals(Set.of(user), followers.get(other), "Ein abgewiesener Aufruf darf followers nicht verändern.");
        }
    }

    private static Object invokeUnwrapped(Method method, Object target, Object... arguments) throws Throwable {
        try {
            return method.invoke(target, arguments);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    void task_5_User_class_implements_getTimeline() throws ReflectiveOperationException {
        TestUtils.assertClassHasMethod(userClass, "getTimeline", List.class);
        Method timeline = TestUtils.getMethod(userClass, "getTimeline");
        Method follow = TestUtils.getMethod(userClass, "follow", userClass);
        Method createPost = TestUtils.getMethod(userClass, "createPost", String.class);
        Field posts = TestUtils.getField(userClass, "posts");
        Object user = createUserObject();
        Object first = createUserObject();
        Object second = createUserObject();
        Object outsider = createUserObject();
        createPost.invoke(user, "Eigener Beitrag gehört nicht in die Timeline");
        createPost.invoke(outsider, "Nicht gefolgter Person gehört nicht in die Timeline");
        assertTrue(((List<?>) timeline.invoke(user)).isEmpty(), "Ohne gefolgte Personen ist die Timeline leer.");
        follow.invoke(user, first);
        follow.invoke(user, second);
        createPost.invoke(first, "Erster Beitrag");
        createPost.invoke(first, "Zweiter Beitrag");
        createPost.invoke(second, "Dritter Beitrag");
        List<Object> expected = new ArrayList<>((List<?>) posts.get(first));
        expected.addAll((List<?>) posts.get(second));
        List<?> actual = (List<?>) timeline.invoke(user);
        assertEquals(expected.size(), actual.size(), "Die Timeline enthält genau die Beiträge der gefolgten Personen.");
        for (Object post : expected) {
            assertEquals(1L, actual.stream().filter(candidate -> candidate == post).count(),
                    "Jeder Beitrag der gefolgten Personen muss genau einmal vorkommen; die Reihenfolge ist frei.");
        }
    }

    @Test
    void task_6_SocialMediaPlatform_class_with_properties_implemented() {
        Class<?> socialMediaPlatformClass = getSocialMediaPlatformClass();

        assertNotNull(socialMediaPlatformClass, "The class 'SocialMediaPlatform' does not exist.");

        TestUtils.assertClassHasFieldOfType(socialMediaPlatformClass, "users", Set.class);
    }

    @Test
    void task_7_SocialMediaPlatform_implements_createUser() {
        Class<?> socialMediaPlatformClass = getSocialMediaPlatformClass();

        TestUtils.assertClassHasMethod(socialMediaPlatformClass, "createUser", userClass, String.class);

        Method createUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "createUser", String.class);
        Field usersField = TestUtils.getField(socialMediaPlatformClass, "users");

        try {
            Object socialMediaPlatform = createSocialMediaPlatformObject();
            Object user = createUserMethod.invoke(socialMediaPlatform, "Test");
            assertNotNull(user, "The 'createUser' method of the 'SocialMediaPlatform' class does not return a user object.");
            assertTrue(userClass.isInstance(user), "The 'createUser' method of the 'SocialMediaPlatform' class does not return a user object.");
            Field nameField = TestUtils.getField(userClass, "username");
            assertEquals("Test", nameField.get(user), "The 'createUser' method of the 'SocialMediaPlatform' class does not set the username of the user correctly.");
            assertEquals(1, ((Set<?>) usersField.get(socialMediaPlatform)).size(), "The 'createUser' method of the 'SocialMediaPlatform' class does not add the user to the set of users.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to create a user in the 'SocialMediaPlatform' class. \n" + e);
        }
    }

    @Test
    void task_8_User_implements_getPostById() {
        Class<?> postClass = getPostClass();
        TestUtils.assertClassHasMethod(userClass, "getPostById", postClass, int.class);

        Method getPostByIdMethod = TestUtils.getMethod(userClass, "getPostById", int.class);
        Object user = createUserObject();
        try {
            Method createPostMethod = TestUtils.getMethod(userClass, "createPost", String.class);
            createPostMethod.invoke(user, "Post 1");
            createPostMethod.invoke(user, "Post 2");
            // get the second post
            Field idField = TestUtils.getField(postClass, "id");
            Field postsField = TestUtils.getField(userClass, "posts");
            List<?> posts = (List<?>) postsField.get(user);
            Object expectedPost = posts.get(1);
            int expectedId = (int) idField.get(posts.get(1));
            // create a third post
            createPostMethod.invoke(user, "Post 3");
            // get the post by id for the second post
            Object post = getPostByIdMethod.invoke(user, expectedId);
            assertNotNull(post, "The 'getPostById' method of the 'User' class does not return a post object.");
            assertTrue(postClass.isInstance(post), "The 'getPostById' method of the 'User' class does not return a post object.");
            Field textField = TestUtils.getField(postClass, "text");
            assertEquals(expectedPost, post, "The 'getPostById' method of the 'User' class does not return the correct post.");
            int missingPostId = unusedId(posts, postClass);
            assertThrows(NoSuchElementException.class, () -> invokeUnwrapped(getPostByIdMethod, user, missingPostId),
                    "The 'getPostById' method of the 'User' class does not throw an exception when the post does not exist.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get a post in the 'User' class. \n" + e);
        }
    }

    @Test
    void task_9_SocialMediaPlatform_implements_getUserById() {
        Class<?> socialMediaPlatformClass = getSocialMediaPlatformClass();
        TestUtils.assertClassHasMethod(socialMediaPlatformClass, "getUserById", userClass, int.class);

        Method getUserByIdMethod = TestUtils.getMethod(socialMediaPlatformClass, "getUserById", int.class);
        try {
            Method createUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "createUser", String.class);
            Object socialMediaPlatform = createSocialMediaPlatformObject();
            createUserMethod.invoke(socialMediaPlatform, "User 1");
            createUserMethod.invoke(socialMediaPlatform, "User 2");
            Object expectedUser = createUserMethod.invoke(socialMediaPlatform, "User 3");
            int expectedId = (int) TestUtils.getField(userClass, "id").get(expectedUser);
            Object userObject = getUserByIdMethod.invoke(socialMediaPlatform, expectedId);
            assertSame(expectedUser, userObject);
            assertNotNull(userObject, "The 'getUserById' method of the 'SocialMediaPlatform' class does not return a user object.");
            assertTrue(userClass.isInstance(userObject), "The 'getUserById' method of the 'SocialMediaPlatform' class does not return a user object.");
            Field nameField = TestUtils.getField(userClass, "username");
            assertEquals("User 3", nameField.get(userObject), "The 'getUserById' method of the 'SocialMediaPlatform' class does not return the correct user.");
            Set<?> registered = (Set<?>) TestUtils.getField(socialMediaPlatformClass, "users").get(socialMediaPlatform);
            int missingUserId = unusedId(registered, userClass);
            assertThrows(NoSuchElementException.class, () -> invokeUnwrapped(getUserByIdMethod, socialMediaPlatform, missingUserId),
                    "The 'getUserById' method of the 'SocialMediaPlatform' class does not throw an exception when the user does not exist.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get the correct user in the 'SocialMediaPlatform' class. \n" + e);
        }
    }

    @Test
    void task_10_SocialMediaPlatform_implements_getMostFollowedUser() {
        Class<?> socialMediaPlatformClass = getSocialMediaPlatformClass();

        TestUtils.assertClassHasMethod(socialMediaPlatformClass, "getMostFollowedUser", userClass);

        Method getMostFollowedUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "getMostFollowedUser");
        Method followMethod = TestUtils.getMethod(userClass, "follow", userClass);

        Object socialMediaPlatform = createSocialMediaPlatformObject();
        try {
            Method createUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "createUser", String.class);
            Object user1 = createUserMethod.invoke(socialMediaPlatform, "Test");
            Object user2 = createUserMethod.invoke(socialMediaPlatform, "Test2");
            Object user3 = createUserMethod.invoke(socialMediaPlatform, "Test3");

            followMethod.invoke(user1, user2);
            followMethod.invoke(user1, user3);
            followMethod.invoke(user2, user3);

            Object mostFollowedUser = getMostFollowedUserMethod.invoke(socialMediaPlatform);
            assertEquals(user3, mostFollowedUser, "The 'getMostFollowedUser' method of the 'SocialMediaPlatform' class does not return the correct user.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get the most followed user in the 'SocialMediaPlatform' class. \n" + e);
        }

    }

    @Test
    void task_11_SocialMediaPlatform_implements_getMostActiveUser() {
        Class<?> socialMediaPlatformClass = getSocialMediaPlatformClass();

        TestUtils.assertClassHasMethod(socialMediaPlatformClass, "getMostActiveUser", userClass);

        Method getMostActiveUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "getMostActiveUser");
        Method createPostMethod = TestUtils.getMethod(userClass, "createPost", String.class);
        Method createUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "createUser", String.class);
        Object socialMediaPlatform = createSocialMediaPlatformObject();

        try {
            Object user1 = createUserMethod.invoke(socialMediaPlatform, "Test");
            Object user2 = createUserMethod.invoke(socialMediaPlatform, "Test2");
            Object user3 = createUserMethod.invoke(socialMediaPlatform, "Test3");

            createPostMethod.invoke(user1, "Post 1.1");
            createPostMethod.invoke(user1, "Post 1.2");
            createPostMethod.invoke(user1, "Post 1.3");
            createPostMethod.invoke(user2, "Post 2.1");
            createPostMethod.invoke(user2, "Post 2.2");
            createPostMethod.invoke(user3, "Post 3.1");

            Object mostActiveUser = getMostActiveUserMethod.invoke(socialMediaPlatform);
            assertEquals(user1, mostActiveUser, "The 'getMostActiveUser' method of the 'SocialMediaPlatform' class does not return the user with the most posts.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get the most active user in the 'SocialMediaPlatform' class. \n" + e);
        }
    }

    @Test
    void task_12_main_method_implemented() throws ReflectiveOperationException {
        Class<?> platformClass = getSocialMediaPlatformClass();
        assertNotNull(userClass, "Implementieren Sie zuerst User.");
        assertNotNull(platformClass, "Implementieren Sie zuerst SocialMediaPlatform.");

        // Echte Konstruktoren initialisieren den Zustand. Methoden laufen auf dem
        // beobachteten Objekt real weiter; IDs, Collections und Rückgaben bleiben erhalten.
        OutputProbe probe = new OutputProbe();
        try (MockedConstruction<?> users = observeConstruction(userClass, probe.answer());
             MockedConstruction<?> platforms = observeConstruction(platformClass, probe.answer())) {
            String output = TestUtils.runActionAndGetSystemOut(() -> Main.main(new String[0]));
            assertEquals(1, platforms.constructed().size(), "Erzeugen Sie eine Plattform.");
            Object platform = platforms.constructed().getFirst();
            Set<?> registered = (Set<?>) TestUtils.getField(platformClass, "users").get(platform);
            assertTrue(registered.size() >= 5, "Registrieren Sie mindestens fünf Nutzerinnen oder Nutzer.");
            for (Object user : registered) {
                assertTrue(Mockito.mockingDetails(user).isMock(), "Erzeugen Sie die Nutzer innerhalb von main.");
                List<?> posts = (List<?>) TestUtils.getField(userClass, "posts").get(user);
                assertTrue(posts.size() >= 3, "Jede Person benötigt mindestens drei Beiträge.");
                Set<?> following = (Set<?>) TestUtils.getField(userClass, "following").get(user);
                assertTrue(following.size() >= 2, "Jede Person muss mindestens zwei verschiedenen Personen folgen.");
                assertFalse(following.contains(user), "Eine Person darf sich nicht selbst folgen.");
                assertTrue(registered.containsAll(following), "Folgen Sie Personen derselben Plattform.");
                assertCalled(user, "getTimeline");
            }
            assertCalled(platform, "getMostFollowedUser");
            assertCalled(platform, "getMostActiveUser");
            probe.assertPrinted(output);
        }
    }

    private static void assertCalled(Object object, String method) {
        assertTrue(Mockito.mockingDetails(object).getInvocations().stream()
                .anyMatch(invocation -> invocation.getMethod().getName().equals(method)),
                "Rufen Sie " + method + " für das erforderliche Objekt auf.");
    }

    /**
     * Aufgabe 12 prüft den Umgang mit Rückgaben, unabhängig vom Ausgabeformat.
     * Kontrollierte Kopien tragen eindeutige Texte/Namen: Die bloße Ausgabe der
     * eigenen Beiträge oder ein ungenutzter Statistikaufruf kann so nicht bestehen.
     * Originalzustand und Konstruktoren bleiben erhalten. Fachliche Rückgaben der
     * Methoden selbst werden separat in Aufgaben 5, 10 und 11 geprüft.
     */
    private static final class OutputProbe {
        private final Map<Object, Map<Object, Object>> timelineCopies = new IdentityHashMap<>();
        private final Map<String, Map<Object, Object>> statisticCopies = new HashMap<>();
        private final Set<String> requiredOutput = new LinkedHashSet<>();
        private int sequence;

        private String marker(String kind, Object original) {
            String token = "[INF520-" + kind + "-" + sequence++ + "]";
            requiredOutput.add(token);
            return token + " " + original;
        }

        Answer<Object> answer() {
            return invocation -> {
                Object result = invocation.callRealMethod();
                String name = invocation.getMethod().getName();
                if (name.equals("getTimeline") && invocation.getArguments().length == 0) {
                    assertInstanceOf(List.class, result, "getTimeline muss eine Liste zurückgeben.");
                    Map<Object, Object> copies = timelineCopies.computeIfAbsent(invocation.getMock(), key -> new IdentityHashMap<>());
                    List<Object> controlled = new ArrayList<>();
                    for (Object post : (List<?>) result) {
                        if (!copies.containsKey(post)) {
                            Object copy = copyObject(post);
                            Field text = TestUtils.getField(getPostClass(), "text");
                            text.set(copy, marker("Beitrag", text.get(post)));
                            Field author = TestUtils.getField(getPostClass(), "author");
                            Object authorCopy = copyObject(author.get(post));
                            Field username = TestUtils.getField(authorCopy.getClass(), "username");
                            username.set(authorCopy, marker("Verfasser", username.get(authorCopy)));
                            author.set(copy, authorCopy);
                            copies.put(post, copy);
                        }
                        controlled.add(copies.get(post));
                    }
                    return controlled;
                }
                if ((name.equals("getMostFollowedUser") || name.equals("getMostActiveUser"))
                        && invocation.getArguments().length == 0) {
                    assertNotNull(result, "Die Statistik muss eine Person zurückgeben.");
                    Map<Object, Object> copies = statisticCopies.computeIfAbsent(name, key -> new IdentityHashMap<>());
                    if (!copies.containsKey(result)) {
                        Object copy = copyObject(result);
                        Field username = TestUtils.getField(copy.getClass(), "username");
                        username.set(copy, marker(name, username.get(copy)));
                        copies.put(result, copy);
                    }
                    return copies.get(result);
                }
                return result;
            };
        }

        void assertPrinted(String output) {
            assertFalse(requiredOutput.isEmpty(), "Geben Sie die Timeline und die Statistiken aus.");
            for (String token : requiredOutput) {
                assertTrue(output.contains(token), "Geben Sie die Texte und Namen aus den Rückgaben von getTimeline, "
                        + "getMostFollowedUser und getMostActiveUser aus. Es fehlt: " + token);
            }
        }
    }

    private static Object copyObject(Object original) throws IllegalAccessException {
        assertNotNull(original, "Beiträge und ihre Verfassenden dürfen nicht null sein.");
        // Mockito instanziiert ohne zusätzlichen Konstruktoraufruf; private/finale
        // Felder werden wie beim beobachteten Konstruktor aus dem Original übernommen.
        Object copy = Mockito.mock(original.getClass(), Mockito.CALLS_REAL_METHODS);
        copyFields(original, copy);
        return copy;
    }

    private static void copyFields(Object original, Object target) throws IllegalAccessException {
        for (Class<?> current = original.getClass(); current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    field.set(target, field.get(original));
                }
            }
        }
    }

    private static <T> MockedConstruction<T> observeConstruction(Class<T> type, Answer<Object> answer) {
        return Mockito.mockConstruction(type, Mockito.withSettings().defaultAnswer(answer),
                (mock, context) -> {
                    context.constructor().setAccessible(true);
                    Object initialized = context.constructor().newInstance(context.arguments().toArray());
                    copyFields(initialized, mock);
                });
    }

    // Auch eine zulässige ID-Folge mit anderem Startwert darf bestehen.
    private static int unusedId(Collection<?> objects, Class<?> type) throws IllegalAccessException {
        Set<Integer> ids = new HashSet<>();
        Field field = TestUtils.getField(type, "id");
        for (Object object : objects) ids.add(field.getInt(object));
        int candidate = 0;
        while (ids.contains(candidate)) candidate++;
        return candidate;
    }
}
