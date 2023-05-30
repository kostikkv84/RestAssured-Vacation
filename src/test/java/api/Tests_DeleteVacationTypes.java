package api;

import BaseClasses.ResponseModules;
import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
import api.vacation_types.VacationTypeNotAuthorized;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class Tests_DeleteVacationTypes extends Specifications {
private Integer typeId;

    /**
     * Удаление типа отпуска - admin
     */
    @Test
    public void deleteIfAuth() {
    //создаем тип отпуска
    ResponseModules response = new ResponseModules();
    typeId = response.createNewVacationType(token, "new value", "new description");
    //удаляем
    response.deleteVacationType(token, typeId);
    // проверяем, что удален
    Assert.assertTrue(response.getVacationTypeOnIDError(token, typeId));
}

    /**
     * Удаление типа отпуска - User
     */
    @Test
    public void deleteIfAuthUser() {
        //создаем тип отпуска
        ResponseModules response = new ResponseModules();
        typeId = response.createNewVacationType(token, "new value for del", "new description for del");
        //удаляем
       Assert.assertTrue(response.deleteVacationType403(URL, tokenUser, typeId));
    }

    /**
     * Удаление типа отпуска - не авторизованный пользователь
     */
    @Test
    public void deleteIfNotAuth() {
        //создаем тип отпуска
        ResponseModules response = new ResponseModules();
        Integer id = response.createNewVacationType(token, "new value123", "new description123");
        //удаляем
        response.deleteVacationTypeNotAuth(URL, typeId);
        // проверяем, что удален
        VacationTypeNotAuthorized error = new VacationTypeNotAuthorized();
        // Проверяем, что проверка на ошибку возвращает Истину
        Assert.assertTrue(error.notAuthError(URL, token, "value", "description"), "Ответ не содержит ошибку");
    }

    /**
     * Удаление ранее уделенного типа отпуска
     */
    @Test (dependsOnMethods={"deleteIfAuth"})
    public void deleteIfTypeWasDeleted() {
        installSpecification(requestSpec(URL), specResponseError404());
        VacationTypeError error = new VacationTypeError();
        // Проверяем, что проверка на ошибку возвращает Истину
        Assert.assertEquals(error.deleteError(URL, token, typeId).getDescription(), "Тип отпуска не найден, id: " + typeId, "Ответ не содержит ошибку");
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
