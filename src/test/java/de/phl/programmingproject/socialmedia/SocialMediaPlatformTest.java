package de.phl.programmingproject.socialmedia;

import de.phl.programmingproject.TestBase;
import de.phl.programmingproject.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.Constructor;
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


    private static int USER_CNT;

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
        Object userObject = null;
        for (Constructor constructor : userClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                try {
                    return constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (constructor.getParameterCount() == 1 &&
                    constructor.getParameterTypes()[0] == String.class) {
                try {
                    return constructor.newInstance("Test User " + USER_CNT++);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (constructor.getParameterCount() == 2 &&
                    constructor.getParameterTypes()[0] == int.class &&
                    constructor.getParameterTypes()[1] == String.class) {
                try {
                    return constructor.newInstance(USER_CNT++, "Test User " + USER_CNT);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return userObject;
    }

    Object createSocialMediaPlatformObject() {
        Object socialMediaPlatformObject = null;
        for (Constructor constructor : getSocialMediaPlatformClass().getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                try {
                    socialMediaPlatformObject = constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return socialMediaPlatformObject;
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
    void task_5_User_class_implements_getTimeline() {
        TestUtils.assertClassHasMethod(userClass, "getTimeline", List.class);

        // create two users, user1 follows user2 and user2 creates a post.
        Method getTimelineMethod = TestUtils.getMethod(userClass, "getTimeline");
        Method followMethod = TestUtils.getMethod(userClass, "follow", userClass);
        Method createPostMethod = TestUtils.getMethod(userClass, "createPost", String.class);

        Object user = createUserObject();
        Object otherUser = createUserObject();

        try {
            followMethod.invoke(user, otherUser);
            createPostMethod.invoke(otherUser, "Post 1");
            List<?> posts = (List<?>) getTimelineMethod.invoke(user);
            assertEquals(1, posts.size(), "The 'getTimeline' method of the 'User' class does not return the correct number of posts.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to create the post and add it to the list of posts. \n" + e);
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
            assertTrue(user.getClass().isAssignableFrom(userClass), "The 'createUser' method of the 'SocialMediaPlatform' class does not return a user object.");
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
            assertTrue(post.getClass().isAssignableFrom(postClass), "The 'getPostById' method of the 'User' class does not return a post object.");
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
            assertTrue(userObject.getClass().isAssignableFrom(userClass), "The 'getUserById' method of the 'SocialMediaPlatform' class does not return a user object.");
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
        try (MockedConstruction<?> users = observeConstruction(userClass);
             MockedConstruction<?> platforms = observeConstruction(platformClass)) {
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
                for (Object followed : following) {
                    List<?> timelinePosts = (List<?>) TestUtils.getField(userClass, "posts").get(followed);
                    String name = (String) TestUtils.getField(userClass, "username").get(followed);
                    assertTrue(output.contains(name), "Geben Sie die Namen der Verfassenden in der Timeline aus.");
                    for (Object post : timelinePosts) {
                        String text = (String) TestUtils.getField(getPostClass(), "text").get(post);
                        assertTrue(output.contains(text), "Geben Sie die Texte der Timeline-Beiträge aus.");
                    }
                }
            }
            assertCalled(platform, "getMostFollowedUser");
            assertCalled(platform, "getMostActiveUser");
        }
    }

    private static void assertCalled(Object object, String method) {
        assertTrue(Mockito.mockingDetails(object).getInvocations().stream()
                .anyMatch(invocation -> invocation.getMethod().getName().equals(method)),
                "Rufen Sie " + method + " für das erforderliche Objekt auf.");
    }

    private static <T> MockedConstruction<T> observeConstruction(Class<T> type) {
        return Mockito.mockConstruction(type, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS),
                (mock, context) -> {
                    // Während der Initialisierung wird dieser verschachtelte Konstruktor
                    // nicht erneut gemockt. Dadurch bleiben auch eigene Konstruktorvarianten nutzbar.
                    context.constructor().setAccessible(true);
                    Object initialized = context.constructor().newInstance(context.arguments().toArray());
                    for (Class<?> current = type; current != Object.class; current = current.getSuperclass()) {
                        for (Field field : current.getDeclaredFields()) {
                            if (!Modifier.isStatic(field.getModifiers())) {
                                field.setAccessible(true);
                                field.set(mock, field.get(initialized));
                            }
                        }
                    }
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
