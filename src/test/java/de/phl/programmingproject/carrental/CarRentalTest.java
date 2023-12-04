package de.phl.programmingproject.carrental;

import de.phl.programmingproject.TestUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Car Rental exercise.
 */
public class CarRentalTest {


    @Test
    void task_1_Car_class_with_properties_implemented() {
        /*
        1. Define a `Car` class with the following attributes:

- `make` (String): the make of the car (e.g., "Toyota", "Honda", "Ford")
- `model` (String): the model of the car (e.g., "Camry", "Accord", "Focus")
- `year` (int): the year the car was made
- `rented` (boolean): whether the car is currently rented or not (initially set to `false`)
         */

        Class carClass = getCarClass();

        Map<String, Class> expectedProperties = new HashMap<String, Class>() {{
            put("make", String.class);
            put("model", String.class);
            put("year", int.class);
            put("rented", boolean.class);
        }};

        TestUtils.assertClassHasFieldsOfType(carClass, expectedProperties);
    }

    @Test
    void task_2_Customer_class_with_properties_implemented() {

        Class customerClass = TestUtils.getClassForName("Customer", "de.phl.programmingproject.carrental");
        Map<String, Class> expectedProperties = new HashMap<String, Class>() {{
            put("name", String.class);
            put("rentedCar", Optional.class);
        }};
        TestUtils.assertClassHasFieldsOfType(customerClass, expectedProperties);
    }

    /**
     * 3. Define a `CarRentalSystem` class with the following operations:
     * <p>
     * - `addCar(final Car car)`: adds a new car to the system
     * - `rentCar(final Car car, final Customer customer)`: rents the specified car to the specified customer (if the car is available for rent)
     * - `returnCar(final Customer customer)`: returns the car rented by the given customer (i.e., sets the car's `rented` attribute to `false` and sets the customer's `rentedCar` attribute to `Optional.empty()`)
     * - `getAvailableCars()`: returns a list of all available cars (i.e., cars with `rented` set to `false`)
     * - `getRentedCars()`: returns a list of all rented cars (i.e., cars with `rented` set to `true`)
     */

    @Test
    void task_3_CarRentalSystem_implements_addCar() {
        Class carRentalSystemClass = getCarRentalSystemClass();
        Class carClass = getCarClass();
        TestUtils.assertClassHasMethod(carRentalSystemClass, "addCar", void.class, carClass);

        Object carRentalSystem = createCarRentalSystem();

        Method addCarMethod = TestUtils.getMethod(carRentalSystemClass, "addCar", carClass);
        Field carsField = TestUtils.getField(carRentalSystemClass, "cars");

        Object car = createCar();
        try {
            addCarMethod.invoke(carRentalSystem, car);
            assertEquals(1, ((Collection) carsField.get(carRentalSystem)).size());
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test addCar method in CarRentalSystem");
        }
    }

    @Test
    void task_3_CarRentalSystem_implements_rentCar() {
        Class carRentalSystemClass = getCarRentalSystemClass();
        Class carClass = getCarClass();
        Class customerClass = getCustomerClass();
        TestUtils.assertClassHasMethod(carRentalSystemClass, "rentCar", void.class, customerClass, carClass);

        Object carRentalSystem = createCarRentalSystem();

        Method rentCarMethod = TestUtils.getMethod(carRentalSystemClass, "rentCar", customerClass, carClass);
        Field rentedField = TestUtils.getField(carClass, "rented");
        Field rentedCarField = TestUtils.getField(customerClass, "rentedCar");

        Object car = createCar();
        Object customer = createCustomer("John Doe");
        try {
            rentCarMethod.invoke(carRentalSystem, customer, car);
            assertTrue((boolean) rentedField.get(car), "The car was not rented.");
            assertEquals(car, ((Optional) rentedCarField.get(customer)).get(), "The customer did not rent the car.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test rentCar method in CarRentalSystem");
        }
    }

    @Test
    void task_3_CarRentalSystem_implements_returnCar() {
        Class carRentalSystemClass = getCarRentalSystemClass();
        Class customerClass = getCustomerClass();
        TestUtils.assertClassHasMethod(carRentalSystemClass, "returnCar", void.class, customerClass);

        Object carRentalSystem = createCarRentalSystem();

        Method rentCarMethod = TestUtils.getMethod(carRentalSystemClass, "rentCar", customerClass, getCarClass());
        Method returnCarMethod = TestUtils.getMethod(carRentalSystemClass, "returnCar", customerClass);
        Field rentedCarField = TestUtils.getField(customerClass, "rentedCar");
        Field rentedField = TestUtils.getField(getCarClass(), "rented");

        Object customer = createCustomer("John Doe");
        Object car = createCar();
        try {
            rentCarMethod.invoke(carRentalSystem, customer, car);
            assertTrue((boolean) rentedField.get(car), "The car was not rented.");
            returnCarMethod.invoke(carRentalSystem, customer);
            assertFalse((boolean) rentedField.get(car), "The car was not returned.");
            assertEquals(Optional.empty(), rentedCarField.get(customer), "The customer did not return the car.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test returnCar method in CarRentalSystem");
        }
    }

    @Test
    void task_3_CarRentalSystem_implements_getAvailableCars() {
        Class carRentalSystemClass = getCarRentalSystemClass();
        TestUtils.assertClassHasMethod(carRentalSystemClass, "getAvailableCars", Collection.class);

        Object carRentalSystem = createCarRentalSystem();

        Method addCarMethod = TestUtils.getMethod(carRentalSystemClass, "addCar", getCarClass());
        Method getAvailableCarsMethod = TestUtils.getMethod(carRentalSystemClass, "getAvailableCars");
        Object car = createCar();
        try {
            assertEquals(0, ((Collection) getAvailableCarsMethod.invoke(carRentalSystem)).size());
            addCarMethod.invoke(carRentalSystem, car);
            assertEquals(1, ((Collection) getAvailableCarsMethod.invoke(carRentalSystem)).size(), "The 'getAvailableCars' method does not return the correct number of cars.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test getAvailableCars method in CarRentalSystem");
        }
    }
    @Test
    void task_3_CarRentalSystem_implements_getRentedCars(){
        Class carRentalSystemClass = getCarRentalSystemClass();
        TestUtils.assertClassHasMethod(carRentalSystemClass, "getRentedCars", Collection.class);

        Object carRentalSystem = createCarRentalSystem();

        Method addCarMethod = TestUtils.getMethod(carRentalSystemClass, "addCar", getCarClass());
        Method rentCarMethod = TestUtils.getMethod(carRentalSystemClass, "rentCar", getCustomerClass(), getCarClass());
        Method getRentedCarsMethod = TestUtils.getMethod(carRentalSystemClass, "getRentedCars");
        Object car = createCar();
        Object customer = createCustomer("John Doe");
        try {
            assertEquals(0, ((Collection) getRentedCarsMethod.invoke(carRentalSystem)).size());
            addCarMethod.invoke(carRentalSystem, car);
            rentCarMethod.invoke(carRentalSystem, customer, car);
            assertEquals(1, ((Collection) getRentedCarsMethod.invoke(carRentalSystem)).size(), "The 'getRentedCars' method does not return the correct number of cars.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test getRentedCars method in CarRentalSystem");
        }
    }

    @Test
    void task_4_Car_implements_toString() {
        Class carClass = getCarClass();
        TestUtils.assertClassHasMethod(carClass, "toString", String.class);

        Object car = createCar();
        try {
            String result = car.toString();
            assertTrue(result.contains("Disney"), "The 'toString' method does not contain the make of the car.");
            assertTrue(result.contains("Lightning McQueen"), "The 'toString' method does not contain the model of the car.");
            assertTrue(result.contains("2006"), "The 'toString' method does not contain the year of the car.");
        } catch (Exception e) {
            System.err.println(e);
            fail("Failed to test toString method in Car");
        }
    }

    @Test
    void task_5_main_method_implemented() {
        /*   - Create a new `CarRentalSystem` object
   - Add some cars to the system
   - Create some customers and have them rent and return cars using the `CarRentalSystem` operations
   - Print out the available and rented cars after each rental and return to ensure that the system is working correctly.
   */
        String content = TestUtils.getFileContentForFileInRootOrSrcDirectory("main/java/de/phl/programmingproject/carrental/Main.java");
        assertTrue(content.contains("CarRentalSystem"), "The 'main' method does not create a 'CarRentalSystem' object.");
        assertTrue(content.contains("new Car"), "The 'main' method does not create any 'Car' objects.");
        assertTrue(content.contains("new Customer"), "The 'main' method does not create any 'Customer' objects.");
        assertTrue(content.contains("addCar"), "The 'main' method does not call the 'addCar' method.");
        assertTrue(content.contains("rentCar"), "The 'main' method does not call the 'rentCar' method.");
        assertTrue(content.contains("returnCar"), "The 'main' method does not call the 'returnCar' method.");
        assertTrue(content.contains("getAvailableCars"), "The 'main' method does not call the 'getAvailableCars' method.");
        assertTrue(content.contains("getRentedCars"), "The 'main' method does not call the 'getRentedCars' method.");

    }

    static Class getCarClass() {
        return TestUtils.getClassForName("Car", "de.phl.programmingproject.carrental");
    }

    static Class getCustomerClass() {
        return TestUtils.getClassForName("Customer", "de.phl.programmingproject.carrental");
    }

    static Class getCarRentalSystemClass() {
        return TestUtils.getClassForName("CarRentalSystem", "de.phl.programmingproject.carrental");
    }

    static Object createCarRentalSystem() {
        Class carRentalSystemClass = TestUtils.getClassForName("CarRentalSystem", "de.phl.programmingproject.carrental");
        Object carRentalSystem = null;
        try {
            carRentalSystem = carRentalSystemClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println(e);
            fail("Could not create CarRentalSystem");
        }
        return carRentalSystem;
    }

    static Object createCar() {
        Class carClass = TestUtils.getClassForName("Car", "de.phl.programmingproject.carrental");
        Object car = null;
        try {
            for (Constructor constructor : carClass.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 0) {
                    try {
                        car = constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (constructor.getParameterCount() == 3 &&
                        constructor.getParameterTypes()[0] == String.class &&
                        constructor.getParameterTypes()[1] == String.class && constructor.getParameterTypes()[2] == int.class) {
                    try {
                        car = constructor.newInstance("Disney", "Lightning McQueen", 2006);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            fail("Could not create Car");
        }
        return car;
    }

    static Object createCustomer(String name) {
        Class customerClass = getCustomerClass();
        Object customer = null;
        try {
            for (Constructor constructor : customerClass.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 0) {
                    try {
                        customer = constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (constructor.getParameterCount() == 1 &&
                        constructor.getParameterTypes()[0] == String.class) {
                    try {
                        customer = constructor.newInstance(name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            fail("Could not create Customer");
        }
        return customer;
    }
}
