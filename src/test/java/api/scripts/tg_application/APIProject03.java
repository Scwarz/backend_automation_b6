package api.scripts.tg_application;

import api.pojo_classes.tg_application.AddAUser;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        System.out.println(singleRowResult);
        /**
         * Getting a BigDecimal and start casting it into an int
         */
        BigDecimal dbNewStudentId = (BigDecimal) singleRowResult.get(0); // I'm taking a value from the first row of the database which is "id" as a big decimal number here
        int dbNewStudentIdInt = dbNewStudentId.intValue(); //casting from big decimal into an int value
        List<Object> formattedDBResult = new ArrayList<>(singleRowResult);
        formattedDBResult.set(0, dbNewStudentIdInt); //here I'm setting a casted int value at the index of 0 which is value of "id" coulumn
        Assert.assertEquals(formattedDBResult, Arrays.asList(newStudent_id, createAStudent.getDob(), createAStudent.getEmail(), createAStudent.getFirstName(), createAStudent.getLastName()));
        //delete user
        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + newStudent_id)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

    }

}
