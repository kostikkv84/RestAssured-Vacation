package api.vacation_types;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.Getter;
import lombok.Setter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static spec.Specifications.vacationTypeApi;

@Getter
@Setter
public class VacationTypeNotAuthorized {
    private String error;

    public VacationTypeNotAuthorized() {
        super();
    }

    public VacationTypeNotAuthorized(String error) {
        this.error = error;
    }

    public Boolean notAuthError(String url, String token, String value, String descr){
        TypeVacationAdd requestBody = new TypeVacationAdd(value,descr);
        RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post(url + vacationTypeApi)
                .then().using().defaultParser(Parser.JSON).log().all()
                .assertThat()
                .body("error", is("Not authorized"));
        return true;
    }
}
