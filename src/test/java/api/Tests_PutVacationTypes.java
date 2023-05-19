package api;

import BaseClasses.ResponseModules;
import api.vacation.*;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class Tests_PutVacationTypes extends Specifications {

public static Integer vacationId;

    /**
     * Изменение записи, авторизован админ
     */
    @Test (priority = -1)
    public void changeValueAndDescription(){
    // создание записи для теста
    ResponseModules response = new ResponseModules();
    vacationId = response.createNewVacationType(token,"TypeValueForChange","TypeDiscriptionForChange");

    installSpecification(requestSpec(URL), specResponseOK200());
    TypeVacationAdd requestBody = new TypeVacationAdd("New value", "New description");
    VacationType resp = given()
            .header("Authorization", "Bearer "+token)
            .body(requestBody)
            .when()
            .put(URL + "/vacationType/" + vacationId)
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
                .put(URL + "/vacationType/" + vacationId)
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
                .put(URL + "/vacationType/" + vacationId)
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
                .put(URL + "/vacationType/" + vacationId)
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
                .put(URL + "/vacationType/" + vacationId)
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
                .put(URL + "/vacationType/" + vacationId)
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
        Assert.assertEquals(resp.ChangeVacationTypeValue(token,value,"description",vacationId),value);
    }

    /**
     * Изменение записи, description = 1000 символов
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeDescription1000Symbols(){
        installSpecification(requestSpec(URL), specResponseOK200());
        ResponseModules resp = new ResponseModules();
        String description = RandomString(1000);
        Assert.assertEquals(resp.ChangeVacationTypeDescription(token,"value",description,vacationId),description);
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
                .put(URL + "/vacationType/" + vacationId)
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
        TypeVacationAdd requestBody = new TypeVacationAdd("Новый тип", RandomString(1001));
        VacationTypeError resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + vacationId)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(resp.getDescription()
        );
        Assert.assertEquals(resp.getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Изменение записи, если ID не существует
     */
    @Test (dependsOnMethods={"changeValueAndDescription"})
    public void changeIfIdUnknown(){
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
        delete.deleteVacationType(token,vacationId);

        installSpecification(requestSpec(URL), specResponseError404());
        TypeVacationAdd requestBody = new TypeVacationAdd("Новый тип", "описание");
        VacationTypeError resp = given()
                .header("Authorization", "Bearer "+token)
                .body(requestBody)
                .when()
                .put(URL + "/vacationType/" + vacationId)
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(resp.getDescription());
        Assert.assertEquals(resp.getDescription(),"Тип отпуска не найден, id: " + vacationId);
    }



    //------------------------------------------------------------------------------------------------
    /**
     * Удаление лишних типов отпусков после прохождения тестов - Очистка
     */

    @AfterClass
    //@Test
    public void deleteVacationTypes() {
        // вычисляем количество записей
        Integer count = 0;
        installSpecification(requestSpec(URL), specResponseOK200());
        List<VacationType> list = given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType")
                .then()
                //.then().log().all()
                .extract().jsonPath().getList("",VacationType.class);

        List<Integer> idTypes = list.stream().map(VacationType::getId).collect(Collectors.toList());
        System.out.println(idTypes);

        //--- если типов отпусков больше 6 - то удалить лишние
        if (idTypes.size()>6) {
            for (int i=6;i<idTypes.size();i++){
                installSpecification(requestSpec(URL), specResponseOK204());
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer "+token)
                        .when()
                        .delete(URL+"/vacationType/" + idTypes.get(i))
                        .then()
                        .extract().response();
            }
        }

        // проверяем количество записей, что их 6
        installSpecification(requestSpec(URL), specResponseOK200());
        List<VacationType> listAfterDelete = given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType")
                .then()
                //.then().log().all()
                .extract().jsonPath().getList("",VacationType.class);
        List<Integer> idTypesAfterDelete = list.stream().map(VacationType::getId).collect(Collectors.toList());
        System.out.println("ID отпусков после удаления: " + idTypesAfterDelete);
        Assert.assertEquals(listAfterDelete.size(),6);

    }
}
