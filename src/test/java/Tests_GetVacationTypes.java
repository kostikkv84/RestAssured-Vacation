import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class Tests_GetVacationTypes extends Specifications {

    /**
     * Получение типа отпуска
     */
    @Test
    public void getVacationTypeOnID(){
        installSpecification(requestSpec(URL), specResponseOK200());
        VacationType response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType/3")
                .then().log().all()
                .extract().body().as(VacationType.class);
        Assert.assertEquals(response.getValue(),"Без сохранения ЗП");

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
                .get(URL + "/vacationType/146")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Тип отпуска не найден, id: 146");

    }

    /**
     * Получение всех типов отпуска admin
     */
    @Test
    public void getAllVacationTypes(){
        installSpecification(requestSpec(URL), specResponseOK200());
        List<VacationType> response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("",VacationType.class);
        Assert.assertEquals(response.get(1).getValue(),"Дополнительный оплачиваемый");
    }

    /**
     * Получение всех типов отпуска не авторизован
     */
    @Test
    public void getAllVacationTypesNotAuth(){
        installSpecification(requestSpec(URL), specResponseError401());
        RestAssured.given()
                .when()
                .get(URL + "/vacationType")
                .then().log().all()
                .assertThat()
                .body(containsString("Not authorized"));
    }

}
