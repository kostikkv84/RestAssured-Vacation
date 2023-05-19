package BaseClasses;

import api.vacation.TypeVacationAdd;
import api.vacation.TypeVacationAddIfNumber;
import api.vacation.VacationType;
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

    /**
     * Удаление типа отпуска
     * @param token
     * @param idVacationType
     */
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

    /**
     * СОздание нового типа отпуска, возвращает Id из ответа
     * @param token
     * @return
     */
    public Integer createNewVacationType (String token, String value, String description) {
        installSpecification(requestSpec(URL), specResponseOK201());
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
        Integer id = response.getId();
        System.out.println("СОздан новый тип отпуска с ID: " + id);
        return id;
    }    /**
     * СОздание нового типа отпуска, возвращает Id из ответа
     * @param token
     * @return
     */


    /**
     * Изменение нового типа отпуска, возвращает Id из ответа
     * @param token
     * @return
     */
    public Integer ChangeVacationTypeID (String token, String value, String description, Integer id) {
        installSpecification(requestSpec(URL), specResponseOK201());
        TypeVacationAdd requestBody = new TypeVacationAdd(value,description);
        VacationType resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + id)
                .then().log().all()
                .extract().body().as(VacationType.class);
        Integer vacationID = resp.getId();
        System.out.println("Изменен тип отпуска с ID: " + vacationID);
        return vacationID;
    }

    /**
     * Изменение нового типа отпуска, возвращает Value из ответа
     * @param token
     * @return
     */
    public String ChangeVacationTypeValue (String token, String value, String description, Integer id) {
        installSpecification(requestSpec(URL), specResponseOK200());
        TypeVacationAdd requestBody = new TypeVacationAdd(value,description);
        VacationType resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + id)
                .then().log().all()
                .extract().body().as(VacationType.class);
        String vacationValue = resp.getValue();
        System.out.println("Изменен тип отпуска с Value: " + vacationValue);
        return vacationValue;
    }

    /**
     * Изменение нового типа отпуска, возвращает Description из ответа
     * @param token
     * @return
     */
    public String ChangeVacationTypeDescription (String token, String value, String description, Integer id) {
        installSpecification(requestSpec(URL), specResponseOK200());
        TypeVacationAdd requestBody = new TypeVacationAdd(value,description);
        VacationType resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + id)
                .then().log().all()
                .extract().body().as(VacationType.class);
        String vacationDescription = resp.getDescription();
        System.out.println("Изменен тип отпуска с Description: " + vacationDescription);
        return vacationDescription;
    }




}
