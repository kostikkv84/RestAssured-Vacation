package api.vacation_types;

import io.restassured.RestAssured;
import lombok.Getter;
import lombok.Setter;
import spec.Specifications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Getter
@Setter
public class VacationType extends Specifications {
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

    public static VacationType createVacationStr(String URL, String token, String typeValue, String typeDescription) {
        TypeVacationAdd requestBody = new TypeVacationAdd(typeValue, typeDescription);
        VacationType resp = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + vacationTypeApi)
                .then().log().all()
                .extract().body().as(VacationType.class);
        System.out.println("СОздан новый тип отпуска с ID - " + resp.getId());
        return resp;
    }

    public static VacationType createVacationInt(String URL, String token, Integer typeValue, Integer typeDescription) {
        TypeVacationAddIfNumber requestBody = new TypeVacationAddIfNumber(typeValue, typeDescription);
        VacationType resp = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + vacationTypeApi)
                .then().log().all()
                .extract().body().as(VacationType.class);
        System.out.println("СОздан новый тип отпуска с ID - " + resp.getId());
        return resp;
    }

    public Boolean getCreatedVacationSuccess(String url, String token, Integer vacationTypeID, String value, String description) {
        RestAssured.given().header("Authorization", "Bearer "+token)
                .when()
                .get(url + vacationTypeApi + vacationTypeID)
                .then().log().all()
                .assertThat()
                .body("id", is(vacationTypeID))
                .body("value", is(value))
                .body("description", is(description));
        return true;
    }

}
