package VacationTypesTests;

import BaseClasses.ResponseModules;
import api.vacation_types.TypeVacationAdd;
import api.vacation_types.TypeVacationAddIfNumber;
import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class Tests_PutVacationTypes extends Specifications {

public static Integer typeVacationId;

    /**
     * Изменение записи, авторизован админ
     */
    @Test
    public void changeValueAndDescription(){
    installSpecification(requestSpec(URL), specResponseOK200());
    TypeVacationAdd requestBody = new TypeVacationAdd("New value", "New description");
    VacationType resp = given()
            .header("Authorization", "Bearer "+token)
            .body(requestBody)
            .when()
            .put(URL + vacationTypeApi + typeVacationId)
            .then().log().all()
            .extract().body().as(VacationType.class);

    Assert.assertEquals(resp.getValue(),"New value");
    Assert.assertEquals(resp.getDescription(),"New description");
}

    /**
     * Изменение записи, авторизован User
     */
    @Test(dependsOnMethods={"changeValueAndDescription"})
    public void changeValueAndDescriptionIfUser(){
        installSpecification(requestSpec(URL), specResponseError403());
        TypeVacationAdd requestBody = new TypeVacationAdd("New value User", "New description User");
         RestAssured.given()
                .header("Authorization", "Bearer "+tokenUser)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all();
    }

    /**
     * Изменение записи, не авторизован
     */
    @Test(dependsOnMethods={"changeValueAndDescription"})
    public void changeValueAndDescriptionIfNotAuthorized(){
        installSpecification(requestSpec(URL), specResponseError401());
        TypeVacationAdd requestBody = new TypeVacationAdd("New value Users", "New description Users");
        RestAssured.given()
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .assertThat().body(containsString("Not authorized"));
    }

    /**
     * Изменение созданной записи, изменение: value пустое
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfValueIsNull(){
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("", "Новый тип");
        List<VacationTypeError> resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(resp.get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Изменение созданной записи, изменение: value пробел
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfValueIsWhitespace(){
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd(" ", "Новый тип");
        List<VacationTypeError> resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(resp.get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Изменение созданной записи, изменение: description пустое
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfDescriptionIsNull(){
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Новый тип отпуска", "");
        List<VacationTypeError> resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(resp.get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Изменение созданной записи, изменение: description пробел
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfDescriptionIsWhitespace(){
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Новый тип отпуска", " ");
        List<VacationTypeError> resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(resp.get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Изменение записи, данными с типом Integer
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeValueAndDescriptionInteger(){
        installSpecification(requestSpec(URL), specResponseOK200());
        TypeVacationAddIfNumber requestBody = new TypeVacationAddIfNumber(1000, 1000);
        VacationType resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + vacationTypeApi + typeVacationId)
                .then().log().all()
                .extract().body().as(VacationType.class);
        Assert.assertEquals(resp.getValue(),"1000");
        Assert.assertEquals(resp.getDescription(),"1000");
    }

    /**
     * Изменение записи, value = 255 символов
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeValue255Symbols(){
        installSpecification(requestSpec(URL), specResponseOK200());
        ResponseModules resp = new ResponseModules();
        String value = RandomString(255);
        Assert.assertEquals(resp.ChangeVacationTypeValue(token,value,"description",typeVacationId),value);
    }

    /**
     * Изменение записи, description = 1000 символов
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeDescription1000Symbols(){
        installSpecification(requestSpec(URL), specResponseOK200());
        ResponseModules resp = new ResponseModules();
        String description = RandomString(1000);
        Assert.assertEquals(resp.ChangeVacationTypeDescription(token,"value",description,typeVacationId),description);
    }

    /**
     * Изменение записи, value = 255+ символов
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeValue256symbols(){
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd(RandomString(256), "описание");
        VacationTypeError resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + typeVacationId)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(resp.getDescription()
        );
        Assert.assertEquals(resp.getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Изменение записи, description = 1000+ символов
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfDescription1001Symbols(){
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError resp = VacationTypeError.errorPutVacationType(URL, token, typeVacationId, "Новый тип", RandomString(1001));

        System.out.println(resp.getDescription());
        Assert.assertEquals(resp.getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Изменение записи, если ID не существует
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfIdUnknown()

    {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Тип", "описание");
        VacationTypeError resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + 7896514)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(resp.getDescription());
        Assert.assertEquals(resp.getDescription(),"Тип отпуска не найден, id: 7896514");
    }

    /**
     * Изменение записи, если ID записи удален
     */
    @Test (dependsOnMethods={"changeValueAndDescription"}, priority = 3)
    public void changeIfIdWasDeleted(){
        ResponseModules delete = new ResponseModules();
        delete.deleteVacationType(URL, token,typeVacationId);

        installSpecification(requestSpec(URL), specResponseError404());
        TypeVacationAdd requestBody = new TypeVacationAdd("Новый тип", "описание");
        VacationTypeError resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + typeVacationId)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(resp.getDescription());
        Assert.assertEquals(resp.getDescription(),"Тип отпуска не найден, id: " + typeVacationId);
    }

    /**
     * Изменение нового типа отпуска если Value, Description = Number. БАГ
     * в требованиях отправляются значения в формате String
     */
    @Test (description = "Баг", dependsOnMethods={"changeValueAndDescription"}, priority = 3)
    public void ChangeTypeVacationIfValueNumber() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAddIfNumber requestBody = new TypeVacationAddIfNumber(1,1);
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .put(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Ошибка в формате данных - предполагаемая. Ответ требует доработки");

    }

    //------------------------------------------------------------------------------------------------
    @BeforeClass
    public void createVacationTypeForTest(){
        // создание записи для теста
        ResponseModules req = new ResponseModules();
        typeVacationId = req.createNewVacationType(token,"TypeValueForChange","TypeDiscriptionForChange");
        System.out.println("Создан тип вакансии с ID: " + typeVacationId);
    }

    /**
     * Удаление лишних типов отпусков после прохождения тестов - Очистка
     */
    @AfterClass
    public void deleteVacationTypeAfterTest(){
        deleteVacationType(URL, token, typeVacationId);
        System.out.println("Удалена тестовая запись с типом вакансии, ID: " + typeVacationId);
    }

}
