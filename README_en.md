# Exercise Sheet: Writing own Java classes
[Link to German Version](./README.md)

In this exercise sheet, you will learn to write your own Java classes (and programs) from scratch.

## Exercise: Social Media Platform

You are tasked with designing a social media platform where users can create profiles, post messages, and follow other users.
The classes should be implemented in the `de.phl.programmingproject.socialmedia` package.

### Tasks

1. Create a `User` class with the following properties:
    * `id` (int) - a unique identifier for each user
    * `username` (String) - the user's chosen username
    * `followers` (Set of User) - a set of users who follow this user
    * `following` (Set of User) - a set of users this user is following
    * `posts` (List of Post) - a list of posts

2. Create a `Post` class with the following properties:
    * `id` (int) - a unique identifier for each post. _Hint:_ create the ID in the constructor using an additional static variable to count.
    * `text` (String) - the text of the post
    * `author` (User) - the user who authored the post
    * `timestamp` (Date) - the time the post was created

3. Add an operation to the `User` class called `createPost(final String text)` that creates a new `Post` object and adds it to the list of posts for the user. Use the current date.

4. Add an operation to the `User` class called `follow(final User user)` that adds the specified user to the list of users being followed by this user. Keep in mind also to update the followed user by adding this user in the set of followers.

5. Add an operation to the `User` class called `getTimeline()` that returns a `List` of all the posts authored by users that this user is following.

6. Create a `SocialMediaPlatform` class with the following property:
    * `users` (`Set` of User) - a set of all the users on the platform

7. Add an operation to the `SocialMediaPlatform` class called `User createUser(final String username)` that creates and returns a new `User` object with the specified username, and adds it to the set of users on the platform. The created user is returned.

8. Add an operation to the `User` class called `getPostById(final int id)` that returns the `Post` object with the specified id, or throws a `NoSuchElementException` if no such post exists.

9. Add an operation to the `SocialMediaPlatform` class called `getUserById(final int id)` that returns the `User` object with the specified id, or throws a `NoSuchElementException` if no such user exists.

10. Add an operation to the `SocialMediaPlatform` class called `getMostFollowedUser()` that returns user with the most followers.

11. Add an operation to the `SocialMediaPlatform` class called `getMostActiveUser()` that returns the user with the most posts.

12. Implement the `main` operation located in the `Main` class, that:
    * creates a new `SocialMediaPlatform` object
    * creates at least 5 `User` objects and adds them to the platform
    * has each user create at least 3 posts
    * has each user follow at least 2 other users
    * calls the `getTimeline` operation for each user and prints out the result by iterating through the resulting posts and printing each post's user and text.
    * calls the `getMostFollowedUser` operation and prints out the user's name
    * calls the `getMostActiveUser` operation and prints out the user's name

## Exercise: Car Rental System

You are tasked with implementing a car rental system for a small car rental company. The system should allow customers to rent and return cars, as well as keep track of which cars are available for rent.

### Tasks

1. Define a `Car` class with the following attributes:

   - `make` (String): the make of the car (e.g., "Toyota", "Honda", "Ford")
   - `model` (String): the model of the car (e.g., "Camry", "Accord", "Focus")
   - `year` (int): the year the car was made
   - `rented` (boolean): whether the car is currently rented or not (initially set to `false`)

2. Define a `Customer` class with the following attributes:

   - `name` (String): the name of the customer
   - `rentedCar` (Optional\<Car\>): the car that the customer has currently rented (initially set to `Optional.empty()`)

3. Define a `CarRentalSystem` class with the following operations:

   - `addCar(final Car car)`: adds a new car to the system
   - `rentCar(final Customer customer, final Car car)`: rents the specified car to the specified customer (if the car is available for rent)
   - `returnCar(final Customer customer)`: returns the car rented by the given customer (i.e., sets the car's `rented` attribute to `false` and sets the customer's `rentedCar` attribute to `Optional.empty()`)
   - `getAvailableCars()`: returns a list of all available cars (i.e., cars with `rented` set to `false`)
   - `getRentedCars()`: returns a list of all rented cars (i.e., cars with `rented` set to `true`)

4. Implement a `String toString()` operation in the `Car` class which returns a String representation of a car, i.e., putting all attributes in one joint String and return it.

5. Implement the `main` operation located in the `Main` class:

   - Create a new `CarRentalSystem` object
   - Add some cars to the system
   - Create some customers and have them rent and return cars using the `CarRentalSystem` operations
   - Print out the available and rented cars after each rental and return to ensure that the system is working correctly.