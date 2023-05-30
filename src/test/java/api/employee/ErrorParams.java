package api.employee;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static io.restassured.RestAssured.given;

@Getter
@Setter
public class ErrorParams {
    private String id;
    private String description;
    private String timestamp;

    public ErrorParams() {super();
    }

    public ErrorParams(String id, String description, String timestamp) {
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
    }

    public List<ErrorParams> getError(String url, String token, String param, String value){
        List<ErrorParams> response = given()
                .header("Authorization", "Bearer " + token)
                .param(param, value)
                .when()
                .get(url + "/employee")
                .then().log().all()
                .extract().jsonPath().getList("", ErrorParams.class);
        return response;
    }
}
