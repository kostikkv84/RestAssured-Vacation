package VacationTypesTests;

import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class Tests_GetVacationTypes extends Specifications {

    /**
     * Получение одного типа отпуска - версия TestNG
     */
    @Test (priority = -2)
    public void getVacationOnId_Exist(){
        installSpecification(requestSpec(URL), specResponseOK200());
        VacationType vacationType = (VacationType) given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + vacationTypeApi + 5)
                .then().log().all()
                .extract().body().as(VacationType.class);
        System.out.println(vacationType.getValue());
        Assert.assertTrue(vacationType.getValue().contains("По уходу за ребенком"), " Значение типа отпуска 5 не совпадает с - По уходу за ребенком "); // проверка возвращаемого значения в Responce
    }

    /**
     * Получение не существующего типа отпуска
     */
    @Test
    public void getDeletedVacationTypeOnID(){
        installSpecification(requestSpec(URL), specResponseError404());
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .get(URL + vacationTypeApi + 146)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Тип отпуска не найден, id: 146");

    }

    /**
     * Получение всех типов отпусков
     */
    @Test (description = "Получение всех типов отпусков", priority = -3)
    public void getVacationTypeList() {
        installSpecification(requestSpec(URL), specResponseOK200());
        List<VacationType> list = given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + vacationTypeApi)
                .then().log().all()
                .extract().jsonPath().getList("",VacationType.class);
        Assert.assertEquals(list.size(),6);
    }

    /**
     * Проверка схемы VacationType - полученного запросом Get
     */
    @Test
    public void vacationTypeCheckJsonSchema() {
        installSpecification(requestSpec(URL), specResponseOK200());
        RestAssured.given().header("Authorization", "Bearer " + token)
                .when()
                .get(URL + vacationTypeApi + 5)
                .then().log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("VacationTypeSchema.json"));
    }

    /**
     * Получение всех типов отпуска не авторизован
     */
    @Test
    public void getAllVacationTypesNotAuth(){
        installSpecification(requestSpec(URL), specResponseError401());
        RestAssured.given()
                .when()
                .get(URL + vacationTypeApi)
                .then().log().all()
                .assertThat()
                .body(containsString("Not authorized"));
    }

    //---------------- Запуск удаления лишних типов вакансий на всякий случай

    @AfterClass
    public void cleanExtraVacationTypes() {
        deleteVacationTypes(URL);
    }

}
