package de.phl.programmingproject.socialmedia;

import de.phl.programmingproject.TestBase;
import de.phl.programmingproject.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the SocialMediaPlatform exercise {@link Main}.
 */
public class SocialMediaPlatformTest extends TestBase {

    Class userClass;

    static Class getSocialMediaPlatformClass() {
        return TestUtils.getClassForName("SocialMediaPlatform",
                "de.phl.programmingproject.socialmedia");
    }

    static Class getPostClass() {
        return TestUtils.getClassForName("Post",
                "de.phl.programmingproject.socialmedia");
    }

    Object createUserObject() {
        Object userObject = null;
        for (Constructor constructor : userClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                try {
                    userObject = constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (constructor.getParameterCount() == 1 &&
                    constructor.getParameterTypes()[0] == String.class) {
                try {
                    userObject = constructor.newInstance("Test User");
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

    @AfterEach
    void tearDown() {
        // reset static fields of User and Post class
        if (userClass != null) {
            resetStaticFields(userClass, 0);
        }

        try {
            Class postClass = Class.forName("de.phl.programmingproject.socialmedia.Post");
            resetStaticFields(postClass, 0);
        } catch (ClassNotFoundException e) {
            System.out.println("Post class not yet implemented!");
        }
    }

    private static void resetStaticFields(Class clazz, Object defaultValue) {
        for (Field field : clazz.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    field.set(null, defaultValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Test
    public void task_1_User_class_with_properties_implemented() {

        Map<String, Class> expectedFields = new LinkedHashMap() {
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
        Class postClass = getPostClass();

        assertNotNull(postClass, "The class 'Post' does not exist.");

        /**
         *     * `id` (int) - a unique identifier for each post. _Hint:_ create the ID in the constructor using an additional static variable to count.
         *     * `text` (String) - the text of the post
         *     * `author` (User) - the user who authored the post
         *     * `timestamp` (Date) - the time the post was created
         */
        Map<String, Class> expectedFields = new LinkedHashMap() {
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
    void task_4_User_class_implements_follow() {
        TestUtils.assertClassHasMethod(userClass, "follow", void.class, userClass);

        Method followMethod = TestUtils.getMethod(userClass, "follow", userClass);

        Object userObject = createUserObject();
        Object otherUser = createUserObject();

        Field followingField = TestUtils.getField(userClass, "following");
        Field followersField = TestUtils.getField(userClass, "followers");
        try {
            Set<?> following = (Set<?>) followingField.get(userObject);
            assertEquals(0, following.size());
            followMethod.invoke(userObject, otherUser);
            assertEquals(1, following.size(), "The 'follow' method of the 'User' class does not add the user to the list of following users.");
            assertEquals(1, ((Set<?>) followersField.get(otherUser)).size(), "The 'follow' method of the 'User' class does not add the user to the list of followers of the other user.");

            assertThrows(Exception.class, () -> followMethod.invoke(userObject, userObject),
                    "The 'follow' method of the 'User' class does not throw an exception when the user tries to follow itself.");

            assertThrows(Exception.class, () -> followMethod.invoke(userObject, otherUser),
                    "The 'follow' method of the 'User' class does not throw an exception when the user tries to follow a user that he/she is already following.");

        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to follow another user. \n" + e);
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
        Class socialMediaPlatformClass = getSocialMediaPlatformClass();

        assertNotNull(socialMediaPlatformClass, "The class 'SocialMediaPlatform' does not exist.");

        TestUtils.assertClassHasFieldOfType(socialMediaPlatformClass, "users", Set.class);
    }

    @Test
    void task_7_SocialMediaPlatform_implements_createUser() {
        Class socialMediaPlatformClass = getSocialMediaPlatformClass();

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
        Class postClass = getPostClass();
        TestUtils.assertClassHasMethod(userClass, "getPostById", postClass, int.class);

        Method getPostByIdMethod = TestUtils.getMethod(userClass, "getPostById", int.class);
        Object user = createUserObject();
        try {
            Method createPostMethod = TestUtils.getMethod(userClass, "createPost", String.class);
            createPostMethod.invoke(user, "Post 1");
            createPostMethod.invoke(user, "Post 2");
            createPostMethod.invoke(user, "Post 3");
            Object post = getPostByIdMethod.invoke(user, 2);
            assertNotNull(post, "The 'getPostById' method of the 'User' class does not return a post object.");
            assertTrue(post.getClass().isAssignableFrom(postClass), "The 'getPostById' method of the 'User' class does not return a post object.");
            Field textField = TestUtils.getField(postClass, "text");
            assertEquals("Post 3", textField.get(post), "The 'getPostById' method of the 'User' class does not return the correct post.");
            assertThrows(Exception.class, () -> getPostByIdMethod.invoke(user, 4),
                    "The 'getPostById' method of the 'User' class does not throw an exception when the post does not exist.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get a post in the 'User' class. \n" + e);
        }
    }

    @Test
    void task_9_SocialMediaPlatform_implements_getUserById() {
        Class socialMediaPlatformClass = getSocialMediaPlatformClass();
        TestUtils.assertClassHasMethod(socialMediaPlatformClass, "getUserById", userClass, int.class);

        Method getUserByIdMethod = TestUtils.getMethod(socialMediaPlatformClass, "getUserById", int.class);
        try {
            Method createUserMethod = TestUtils.getMethod(socialMediaPlatformClass, "createUser", String.class);
            Object socialMediaPlatform = createSocialMediaPlatformObject();
            createUserMethod.invoke(socialMediaPlatform, "User 1");
            createUserMethod.invoke(socialMediaPlatform, "User 2");
            createUserMethod.invoke(socialMediaPlatform, "User 3");
            Object userObject = getUserByIdMethod.invoke(socialMediaPlatform, 2);
            assertNotNull(userObject, "The 'getUserById' method of the 'SocialMediaPlatform' class does not return a user object.");
            assertTrue(userObject.getClass().isAssignableFrom(userClass), "The 'getUserById' method of the 'SocialMediaPlatform' class does not return a user object.");
            Field nameField = TestUtils.getField(userClass, "username");
            assertEquals("User 3", nameField.get(userObject), "The 'getUserById' method of the 'SocialMediaPlatform' class does not return the correct user.");
            assertThrows(Exception.class, () -> getUserByIdMethod.invoke(socialMediaPlatform, 4),
                    "The 'getUserById' method of the 'SocialMediaPlatform' class does not throw an exception when the user does not exist.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to get the correct user in the 'SocialMediaPlatform' class. \n" + e);
        }
    }

    @Test
    void task_10_SocialMediaPlatform_implements_getMostFollowedUser() {
        Class socialMediaPlatformClass = getSocialMediaPlatformClass();

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
        Class socialMediaPlatformClass = getSocialMediaPlatformClass();

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
    void task_12_main_method_implemented() {

        // TODO: use Powermock to spy on the SocialMediaPlatform and User object and verify that the methods are called

        /*
         * creates a new `SocialMediaPlatform` object
         * creates at least 5 `User` objects and adds them to the platform
         * has each user create at least 3 posts
         * has each user follow at least 2 other users
         * calls the `getTimeline` operation for each user and prints out the result by iterating through the resulting posts and printing each post's user and text.
         * calls the `getMostFollowedUser` operation and prints out the user's name
         * calls the `getMostActiveUser` operation and prints out the user's name
         */
        String content = TestUtils.getFileContentForFileInRootOrSrcDirectory("main/java/de/phl/programmingproject/socialmedia/Main.java");

        // assert that at least 5x createUser is called
        int createUserCnt = 0;
        int createPostCnt = 0;
        int followCnt = 0;
        int getTimelineCnt = 0;
        int getMostFollowedUserCnt = 0;
        int getMostActiveUserCnt = 0;
        for (String s : content.split("\n")) {
            if (s.contains("createUser("))
                createUserCnt++;
            else if (s.contains("createPost("))
                createPostCnt++;
            else if (s.contains("follow("))
                followCnt++;
            else if (s.contains("getTimeline("))
                getTimelineCnt++;
            else if (s.contains("getMostFollowedUser("))
                getMostFollowedUserCnt++;
            else if (s.contains("getMostActiveUser("))
                getMostActiveUserCnt++;
        }

        assertEquals(5, createUserCnt, String.format("The 'createUser' method in the 'main' method of the 'Main' file is expected to be called 15 times, but was called %d times.", createUserCnt));

        assertTrue(createPostCnt >= 3 * 5 || createPostCnt == 1, String.format("The 'createPost' method in the 'main' method of the 'Main' file is expected to be called at least 15 times (at least 3 posts for each user), but was called %d times instead.", createPostCnt, 3 * 5));

        assertTrue(followCnt >= 2 * 5, String.format("The 'follow' method in the 'main' method of the 'Main' file is expected to be called at least 10 times (at least 2 followers for each user), but was called %d times instead.", followCnt, 2 * 5));

        assertEquals(1, getTimelineCnt, String.format("The 'getTimeline' method in the 'main' method of the 'Main' file is expected to be called once, but is called %d times", getTimelineCnt));

        assertEquals(1, getMostFollowedUserCnt, String.format("The 'getMostFollowedUser' method in the 'main' method of the 'Main' file is expected to be called once, but is called %d times", getMostFollowedUserCnt));

        assertEquals(1, getMostActiveUserCnt, String.format("The 'getMostActiveUser' method in the 'main' method of the 'Main' file is expected to be called once, but is called %d times", getMostActiveUserCnt));
    }


}
