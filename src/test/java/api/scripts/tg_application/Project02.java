package api.scripts.tg_application;

import api.pojo_classes.tg_application.AddAUser;
import api.pojo_classes.tg_application.PatchUserInfo;
import api.pojo_classes.tg_application.UpdateAUser;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import org.hamcrest.Matchers;


import static org.hamcrest.Matchers.*;

public class  Project02 {
    Response response;

    RequestSpecification baseSpec;

    @BeforeMethod
    public void setAPI(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void TechGlobalCRUD(){
        //1. Retrieve a list of all users.
        /** 1. Retrieve a list of all users.
         * - Make a GET call for all TechGlobal students.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate the number of students when you get all students are more than equal to 2
         * - Validate second default users name is “John”, and the last name is “Doe”
         */
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().body().assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("$", hasSize(greaterThan(1))).body("[1].firstName", equalTo("John")).body("[1].lastName", equalTo("Doe"))
                .extract().response();

        //2. Create a new user
        /** 2. Create a new user
         * - Make a POST call for all TechGlobal students.
         * - Verify that a POST request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate first name, last name, email, and dob you set is matching with the response.
         */

        AddAUser createUser = AddAUser.builder()
                .firstName("Anatoli").lastName("Kamyshev").email("anatoli.kamyshev@gmail.com").dob("1995-01-15")
                .build();

         response = RestAssured.given()
                .spec(baseSpec)
                .body(createUser)
                .when().post("/students")
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("firstName", equalTo("Anatoli")).body("lastName", equalTo("Kamyshev")).body("dob", equalTo("1995-01-15"))
                .extract().response();
        int newUser_id = response.jsonPath().getInt("id");
        String firstNameOfNewUser = response.jsonPath().getString("firstName");
        String lastNameOfNewUser = response.jsonPath().getString("lastName");

        //3. Retrieve a specific user-created
        /** 3. Retrieve a specific user-created
         * Make a GET call for the specific user created.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate response of a specific user’s GET call is matching with the user you created.
         */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + newUser_id)
                .then().log().body().assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("firstName", equalTo(firstNameOfNewUser)).body("lastName", equalTo(lastNameOfNewUser))
                .extract().response();

        //4. Update an existing user
        /** 4. Update an existing user
         * - Make a PUT call to update ALL details of a created TechGlobal student.
         * - Verify that the PUT request status is 200 (success).
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Retrieve the updated student's details and validate that the updated details match the changes made.
         */
        UpdateAUser updateAUser = UpdateAUser.builder()
                .firstName("Caroline").lastName("Kamysheva").email("caroline.kamysheva@gmail.com").dob("1990-06-10")
                .build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(updateAUser)
                .when().put("/students/" + newUser_id)
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("firstName", equalTo(updateAUser.getFirstName())).body("lastName", equalTo(updateAUser.getLastName()))
                .body("email", equalTo(updateAUser.getEmail())).body("dob", equalTo(updateAUser.getDob()))
                .extract().response();

        //5. Partially update an existing User
        /** 5. Partially update an existing User
         * - Make a PATCH call and update the email and dob of a user you created.
         * - Verify that the PATCH request status is 200 (success).
         * - Retrieve the updated student's details and validate that the updated details match the changes made
         * and untouched fields remain the same.
         */

        PatchUserInfo patchUpdateUser = PatchUserInfo.builder()
                .email("newcaroline.kamysheva@yahoo.com").dob("1985-02-11")
                .build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(patchUpdateUser)
                .when().patch("/students/" + newUser_id)
                .then().log().body()
                .assertThat().statusCode(200)
                .body("firstName", equalTo(updateAUser.getFirstName())).body("lastName", equalTo(updateAUser.getLastName()))
                .body("email", equalTo(patchUpdateUser.getEmail())).body("dob", equalTo(patchUpdateUser.getDob()))
                .extract().response();

        //6. Retrieve a list of all users again
        /** 6. Retrieve a list of all users again
         * - Make a GET call for all TechGlobal students.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate the number of students when you get all students are more than equal to 3
         */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().body().assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("$", hasSize(greaterThan(2)))
                .extract().response();

        //7. Retrieve a specific user created to confirm the update.
        /** 7. Retrieve a specific user created to confirm the update.
         * - Make a GET call for the specific user created again.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate that the information in the response body of a specific user’s GET call is matching with the
         * user you updated.
         */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + newUser_id)
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .body("firstName", equalTo(updateAUser.getFirstName())).body("lastName", equalTo(updateAUser.getLastName()))
                .body("email", equalTo(patchUpdateUser.getEmail())).body("dob", equalTo(patchUpdateUser.getDob()))
                .extract().response();

        //8. Finally, delete the user that you created.
        /** 8. Finally, delete the user that you created.
         * - Verify that a DELETE request status is 200 success.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         */

        response = RestAssured.given()
             .spec(baseSpec)
             .when().delete("/students/" + newUser_id)
             .then().log().all()
                .assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .extract().response(); /**/
    }

}
