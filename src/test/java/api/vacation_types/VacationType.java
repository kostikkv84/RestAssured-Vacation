package api.vacation_types;

import io.restassured.RestAssured;
import lombok.Getter;
import lombok.Setter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Getter
@Setter
public class VacationType {
    private Integer id;
    private String value;
    private String description;

    public VacationType() {
        super();
    }

    public VacationType(Integer id, String value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }

    public VacationType createVacation(String URL, String token, String typeValue, String typeDescription) {
        TypeVacationAdd requestBody = new TypeVacationAdd(typeValue, typeDescription);
        VacationType resp = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationType.class);
        System.out.println("СОздан новый тип отпуска с ID - " + resp.getId());
        return resp;
    }

    public Boolean getCreatedVacationSuccess(String url, String token, Integer vacationTypeID, String value, String description) {
        RestAssured.given().header("Authorization", "Bearer "+token)
                .when()
                .get(url + "/vacationType/"+vacationTypeID)
                .then().log().all()
                .assertThat()
                .body("id", is(vacationTypeID))
                .body("value", is(value))
                .body("description", is(description));
        return true;
    }

}
