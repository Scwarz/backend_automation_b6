package api.scripts.tg_application;

import api.pojo_classes.tg_application.AddAUser;
import api.pojo_classes.tg_application.UpdateAUser;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;

public class APIProject03 {
    Response response;

    RequestSpecification baseSpec;

    @BeforeMethod
    public void setAPI(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();
        DBUtil.createDBConnection();
    }

    @Test
    public void TechGlobalProject03CRUD() throws SQLException {
        /**
         * 1. Create a new user
         *
         * - Make a POST call for all TechGlobal students.
         * - Verify that a POST request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate first name, last name, email, and dob you sent in the request body is reflected on the
         * Database.
         */
        AddAUser createAStudent = AddAUser.builder()
                .firstName("Anatoli").lastName("Kamyshev").email("anatoli.kamyshev@gmail.com").dob("1995-01-15")
                .build();



        response = RestAssured.given()
                .spec(baseSpec)
                .body(createAStudent)
                .when().post("/students")
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(2000L))
                .extract().response();

        int newStudent_id = response.jsonPath().getInt("id");
        String sqlQuery = "SELECT * FROM student WHERE id = " + newStudent_id;
        List<List<Object>> SQLResultList = DBUtil.getQueryResultList(sqlQuery);
        List<Object> singleRowResult = SQLResultList.get(0); //this is the only one result with a unique ID after we created a query
        /**
         * Getting a BigDecimal and start casting it into an int
         */
        BigDecimal dbNewStudentId = (BigDecimal) singleRowResult.get(0); // I'm taking a value from the first row of the database which is "id" as a big decimal number here
        int dbNewStudentIdInt = dbNewStudentId.intValue(); //casting from big decimal into an int value
        List<Object> formattedDBResult = new ArrayList<>(singleRowResult); //Here I'm creating a new list taking info of the new student to switch from the old big decimal value into a regular int
        formattedDBResult.set(0, dbNewStudentIdInt); //here I'm setting a casted int value at the index of 0 which is value of "id" coulumn
        Assert.assertEquals(formattedDBResult, Arrays.asList(newStudent_id, createAStudent.getDob(), createAStudent.getEmail(), createAStudent.getFirstName(), createAStudent.getLastName()));

        /**
         * 2. Retrieve a specific user-created
         *
         * - Make a GET call for the specific user created.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Confirm that the user details retrieved from the specific GET API call match exactly with the user data
         * created and stored in the Database.
         */
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + dbNewStudentIdInt)
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(2000L))
                .body("id", equalTo(formattedDBResult.get(0))).body("firstName", equalTo(formattedDBResult.get(3))).body("lastName", equalTo(formattedDBResult.get(4)))
                .body("email", equalTo(formattedDBResult.get(2))).body("dob", equalTo(formattedDBResult.get(1)))
                .extract().response();

        /**
         * 3. Update an existing user
         *
         * - Make a PUT call to update ANY details of a created TechGlobal student you want to update.
         * - Verify that the PUT request status is 200 (success).
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Verify that the updates made through the PUT request are accurately reflected and match the
         * corresponding user data in the Database.
         */
        UpdateAUser updateAStudent = UpdateAUser.builder()
                .firstName("Barbie").lastName("Aqua").email("barbie.aqua@yahoo.com").dob("1985-02-20")
                .build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(updateAStudent)
                .when().put("/students/" + dbNewStudentIdInt)
                .then().log().body()
                .extract().response();
        SQLResultList = DBUtil.getQueryResultList(sqlQuery);
        singleRowResult = SQLResultList.get(0);
        dbNewStudentId = (BigDecimal) singleRowResult.get(0);
        dbNewStudentIdInt = dbNewStudentId.intValue();
        formattedDBResult = new ArrayList<>(singleRowResult);
        formattedDBResult.set(0, dbNewStudentIdInt);
        Assert.assertEquals(formattedDBResult, Arrays.asList(dbNewStudentIdInt, updateAStudent.getDob(), updateAStudent.getEmail(), updateAStudent.getFirstName(), updateAStudent.getLastName()));

        /**
         * 4. Retrieve a specific user created to confirm the update.
         *
         * - Make a GET call for the specific user created again.
         * - Verify that a GET request status is 200 successes.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Validate that the information in the response body of a specific user’s GET call is matching with the
         * values you updated, and it is reflected on the Database.
         */
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + dbNewStudentIdInt)
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(2000L))
                .body("id", equalTo(formattedDBResult.get(0))).body("firstName", equalTo(formattedDBResult.get(3))).body("lastName", equalTo(formattedDBResult.get(4)))
                .body("email", equalTo(formattedDBResult.get(2))).body("dob", equalTo(formattedDBResult.get(1)))
                .extract().response();

        //delete user
        /**
         * 5. Finally, delete the user that you created.
         *
         * - Verify that a DELETE request status is 200 success.
         * - Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * - Make sure the user created is also removed from the Database.
         */
        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + dbNewStudentIdInt)
                .then().log().body()
                .assertThat().statusCode(200)
                .extract().response();

    }

}
