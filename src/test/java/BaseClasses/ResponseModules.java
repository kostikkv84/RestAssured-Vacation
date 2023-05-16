package BaseClasses;

import api.vacation.VacationTypeError;
import io.restassured.RestAssured;
import org.testng.Assert;
import spec.Specifications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ResponseModules extends Specifications {

    /**
     * Попытка получения удаленного типа отпуска.
     * @param token
     * @param vacationTypeID
     * @return
     */
    public boolean getVacationTypeOnIDError(String token, Integer vacationTypeID){
        installSpecification(requestSpec(URL), specResponseError404());
        VacationTypeError res = given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType/"+vacationTypeID)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);

            Assert.assertTrue(res.getDescription().contains("Тип отпуска не найден, id: " + vacationTypeID));
        return true;
    }

    public void deleteVacationType(String token, Integer idVacationType){
        installSpecification(requestSpec(URL), specResponseOK204());
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .delete(URL+"/vacationType/" + idVacationType)
                .then()
                .extract().response();
        System.out.println("Тип отпуска с id: " + idVacationType +  " был удален.");
    }

}
