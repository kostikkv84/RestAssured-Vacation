package BaseClasses;

import api.employee.EmployeeList;
import api.vacation_types.TypeVacationAdd;
import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
import org.testng.Assert;
import spec.Specifications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ResponseModules extends Specifications {

    /**
     * Получение типа отпуска
     */
    public String getVacationTypeOnID(String token, String vacationID){
        installSpecification(requestSpec(URL), specResponseOK200());
        VacationType response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType/" + vacationID)
                .then().log().all()
                .extract().body().as(VacationType.class);
        String value = response.getValue();
        return value;
    }

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
     * @param tokenUser
     * @param idVacationType
     */
    public Boolean deleteVacationType403(String url, String tokenUser, Integer idVacationType){
        installSpecification(requestSpec(url), specResponseError403());
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+tokenUser)
                .when()
                .delete(url+"/vacationType/" + idVacationType)
                .then()
                .extract().response();
        return true;
    }

    /**
     * Удаление типа отпуска
     * NOt AUTH
     */
    public void deleteVacationTypeNotAuth(String url, Integer idVacationType){
        installSpecification(requestSpec(url), specResponseError401());
        given()
                .header("Content-type", "application/json")
                .when()
                .delete(url+"/vacationType/" + idVacationType)
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
                .post(URL + vacationTypeApi)
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
                .put(URL + vacationTypeApi + id)
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
                .put(URL + vacationTypeApi + id)
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
                .put(URL + vacationTypeApi + id)
                .then().log().all()
                .extract().body().as(VacationType.class);
        String vacationDescription = resp.getDescription();
        System.out.println("Изменен тип отпуска с Description: " + vacationDescription);
        return vacationDescription;
    }

    /**
     * Получение ID сотрудника.
     * @return
     */
    public Integer getEmployeeID(){
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = given()
                .header("Authorization", "Bearer "+token)
                .param("id",366)
                .when()
                .get(URL +"/employee")
                .then().log().all()
                .extract().body().as(EmployeeList.class);

        return response.getContent().get(0).getEmployeeId();
    }


}
