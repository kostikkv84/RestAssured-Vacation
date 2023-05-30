package api.vacation_types;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;

@Getter
@Setter
public class VacationTypeError {

    private String id;
    private String description;
    private Date timestamp;

    public VacationTypeError() {super();
    }

    public VacationTypeError(String id, String description, Date timestamp) {
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
    }

    public List<VacationTypeError> errorCreateVacationType(String url, String token, String typeValue, String typeDescription){
        TypeVacationAdd requestBody = new TypeVacationAdd(typeValue,typeDescription);
        List<VacationTypeError> errors = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(url + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        //System.out.println(errors.get(0).getDescription());
        return errors;
    }

    public VacationTypeError errorCreateVacationAdd(String url, String token, String typeValue, String typeDescription){
        TypeVacationAdd requestBody = new TypeVacationAdd(typeValue,typeDescription);
        VacationTypeError error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(url + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(error.getDescription());
        return error;
    }

}
