package api.vacation;

import spec.Specifications;

import static api.Tests.token;
import static io.restassured.RestAssured.given;

public class VacationTypeSpec extends Specifications {


    public void vacationTypePost(String value, String description){
        TypeVacationAdd requestBody = new TypeVacationAdd(value,description);
        VacationType response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationType.class);

        }
}
