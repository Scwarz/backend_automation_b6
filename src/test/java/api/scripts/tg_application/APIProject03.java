package api.scripts.tg_application;

import api.pojo_classes.tg_application.AddAUser;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
        /* AddAUser createAStudent = AddAUser.builder()
                .firstName("Anatoli").lastName("Kamyshev").email("anatoli.kamyshev@gmail.com").dob("1995-01-15")
                .build();
        int newStudent_id = response.jsonPath().getInt("id"); */
        Connection conn = DBUtil.createDBConnection();
        Statement stmt = conn.createStatement();
        String sqlQuery = "SELECT * FROM students WHERE id = 0";
        List<List<Object>> SQLResult = DBUtil.getQueryResultList(sqlQuery);
        for (List<Object> row : SQLResult) {
            String firstNameSQL = row.get(0).toString();
            String lastNameSQL = row.get(1).toString();
            String emailSQL = row.get(2).toString();
            String dobSQL = row.get(3).toString();
        }
        System.out.println(SQLResult);


        /* response = RestAssured.given()
                .spec(baseSpec)
                .body(createAStudent)
                .when().post("/students")
                .then().log().body()
                .assertThat().statusCode(200).time(Matchers.lessThan(2000L))
                .body("firstName", equalTo(String.valueOf((DBUtil.getColumnNames(sqlQuery).get(0)))).body("lastName", equalTo(DBUtil.getColumnNames(sqlQuery).get(1))).body("email", equalTo(DBUtil.getColumnNames(sqlQuery).get(2))).body("dob", equalTo(DBUtil.getColumnNames(sqlQuery).get(3)))
                .extract().response(); */





    }

}
