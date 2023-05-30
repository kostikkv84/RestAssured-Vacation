package api.employee;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

@Getter
@Setter
public class EmployeeList {
    private ArrayList<Content> content;
    private Integer total;

    public EmployeeList() {super();
    }

    public EmployeeList(ArrayList<Content> content, Integer total) {
        this.content = content;
        this.total = total;
    }

        public EmployeeList getEmployeeAll(String url, String token){
            EmployeeList response = given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get(url + "/employee")
                    .then().log().all()
                    .extract().body().as(EmployeeList.class);
            return response;
        }



        public EmployeeList getEmployeeList(String url, String token, String paramName, String paramValue){
            EmployeeList response = given()
                    .header("Authorization", "Bearer " + token)
                    .param(paramName,paramValue)
                    .when()
                    .get(url + "/employee")
                    .then().log().all()
                    .extract().body().as(EmployeeList.class);
         //   System.out.println(response.getContent().size());
            return response;
        }
}
