package api.pojo_classes.tg_application;

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

public class DeleteUser {

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
    public void deleteAUser(){
        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + 1483)
                .then().log().all()
                .assertThat().statusCode(200).time(Matchers.lessThan(7000L))
                .extract().response();
    }

}
