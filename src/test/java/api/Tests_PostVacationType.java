package api;

import BaseClasses.ResponseModules;
import api.employee.EmployeeList;
import api.vacation_types.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import spec.Specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class Tests_PostVacationType extends Specifications {

    public Integer vacationTypeID = 0;
    private String value = "";
    private String description = "";

    /**
     * Создание нового типа отпуска. Авторизован - Admin
     */

    @Test (priority = -2)
    public void createNewTypeOfVacation(){
        installSpecification(requestSpec(URL), specResponseOK201()); // проверка статуса ответа
        VacationType request = new VacationType();
        VacationType req = request.createVacation(URL,token,"NewType", "New description");

            vacationTypeID = req.getId();
            value = req.getValue();
            description = req.getDescription();
        System.out.print("ID - " + vacationTypeID + " value " + value + " description " + description) ;
        Assert.assertEquals(req.getValue(), "NewType");
    }

    /**
     * Проверка созданного, на предидущем шаге типа отпуска с последующим удалением
     */
    @Test (dependsOnMethods={"createNewTypeOfVacation"}, priority = -1)
    public void getVacationCreatedTypeOnID(){
        installSpecification(requestSpec(URL), specResponseOK200());
        VacationType response = new VacationType();
                // Проверка созданного типа отпуска
        Assert.assertTrue(response.getCreatedVacationSuccess(URL, token, vacationTypeID, value, description));
                // удаление, очистка от ненужных тестовых данных
        ResponseModules delete = new ResponseModules();
        delete.deleteVacationType(token,vacationTypeID);
        }


    /**
     * Создание нового типа отпуска. Авторизован - User
     */
    @Test
    public void createNewTypeOfVacationIfUser() {
        installSpecification(requestSpec(URL), specResponseError403());
        TypeVacationAdd requestBody = new TypeVacationAdd("TestType","TestType Descriptions");
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + tokenUser)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all();
    }

    /**
     * Создание нового типа отпуска. Не авторизован!
     */
    @Test
    public void notAuth()
    {
        installSpecification(requestSpec(URL), specResponseError401());
        VacationTypeNotAuthorized error = new VacationTypeNotAuthorized();
        // Проверяем, что проверка на ошибку возвращает Истину
        Assert.assertTrue(error.notAuthError(URL, token, "value", "descroption"), "Ответ не содержит ошибку");
    }

    /**
     * Создание нового типа отпуска если значение (Value) отпуска уже существует.
     * Value - уникально.
     */
    @Test
    public void createNewTypeOfVacationIfValueExist() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationAdd(URL,token, "Основной оплачиваемый", "Some descr").getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Создание нового типа отпуска если значение (Description) отпуска уже существует - УСПЕХ - ошибки быть не должно.
     */
    @Test
    public void createNewTypeOfVacationIfDescriptionExist() {
        installSpecification(requestSpec(URL), specResponseOK201());
        VacationType success  = new VacationType();
        Assert.assertEquals(success.createVacation(URL,token, "Some value1", "описание для Без сохранения ЗП").getDescription(),"описание для Без сохранения ЗП");
    }

    /**
     * Создание нового типа отпуска если значение (Value) = null.
     */
    @Test
    public void createNewTypeVacationIfValueIsNull() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationType(URL,token, null, "Some descripton" ).get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Value) = Empty (" ").
     */
    @Test
    public void createNewTypeVacationIfValueIsEmpty() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationType(URL,token, " ", "Some descripton" ).get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Description) не указано - " ".
     */
    @Test
    public void createNewTypeVacationIfDescriptionIsEmpty() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationType(URL,token, "Some value", " " ).get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Description) = Null.
     */
    @Test
    public void createNewTypeVacationIfDescriptionIsNull() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationType(URL,token, "Some value", null).get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Value) = 255 length.
     */
    @Test
    public void createNewTypeVacationIfValue_255_Symbols() {
        installSpecification(requestSpec(URL), specResponseOK201());
        String text = RandomString(255);
        VacationType resp = new VacationType();
        // Проверяем, что создан тип отпуска с Value = 255 символов
        Assert.assertEquals(resp.createVacation(URL,token, text,RandomString(10)).getValue(), text, "Содержимое value не совпадает.");

    }

    /**
     * Создание нового типа отпуска если значение (Value) = 256 length.
     */
    @Test
    public void createNewTypeVacationIfValue_256_Symbols(){
        installSpecification(requestSpec(URL), specResponseError400());
        String text = RandomString(256);
        VacationTypeError resp = new VacationTypeError();
        // Проверяем, что создан тип отпуска с Value = 256 символов
        Assert.assertEquals(resp.errorCreateVacationAdd(URL,token, text,RandomString(10)).getDescription(), "Ошибка добавления или обновления записи в бд", "Содержимое value не совпадает.");

    }

    /**
     * ПОпытка получения удаленного типа отпуска, после удаления
     */
    @Test (dependsOnMethods={"getVacationCreatedTypeOnID"})
    @Ignore
    public void checkDeletedTypeID(){
        // удаление типа отпуска
        ResponseModules delete = new ResponseModules();
        delete.deleteVacationType(token, 7);
        // попытка получить удаленный тип отпуска
        ResponseModules response = new ResponseModules();
        Assert.assertTrue(response.getVacationTypeOnIDError(token,7));
    }

    /**
     * Создание нового типа отпуска если значение (Description) = 1000 length.
     */
    @Test
    public void createNewTypeVacationIfDescription_1000_Symbols() {
        installSpecification(requestSpec(URL), specResponseOK201());
        String text = RandomString(1000);
        VacationType resp = new VacationType();
        // Проверяем, что создан тип отпуска с Description = 1000 символов
        Assert.assertEquals(resp.createVacation(URL,token, RandomString(10),text).getDescription(), text, "Содержимое description не совпадает.");
    }

    /**
     * Создание нового типа отпуска если значение (Description) = 1001 length.
     */
    @Test
    public void createNewTypeVacationIfDescription_1001_Symbols() {
        installSpecification(requestSpec(URL), specResponseError400());
        String text = RandomString(1001);
        VacationTypeError resp = new VacationTypeError();
        // Проверяем, что создан тип отпуска с Value = 256 символов
        Assert.assertEquals(resp.errorCreateVacationAdd(URL,token, RandomString(10),text).getDescription(), "Ошибка добавления или обновления записи в бд", "Содержимое value не совпадает.");
    }

    /**
     * Создание нового типа отпуска если Value и Description не указаны - пустой Body.
     */
    @Test
    public void createNewTypeVacationIfValueAndDescriptionIsEmpty() {
       installSpecification(requestSpec(URL), specResponseError400());
       VacationTypeError errors = new VacationTypeError();
        // вывод содержимого полей с description
        errors.errorCreateVacationType(URL,token,null,null).stream().forEach(x -> System.out.println(x.getDescription()));
        // проверка содержимого полей с description
        errors.errorCreateVacationType(URL,token,null,null).stream().forEach(x -> Assert.assertTrue(x.getDescription().contains("поле не должно быть null и не должно быть пустым")));
       }

    /**
     * Создание нового типа отпуска если Value, Description = Number. БАГ
     * в требованиях отправляются значения в формате String
     */
    @Test (description = "Баг")
    public void createNewTypeVacationIfValueNumber() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAddIfNumber requestBody = new TypeVacationAddIfNumber(1,1);
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Ошибка в формате данных - предполагаемая. Ответ требует доработки");

    }

    /**
     * Создание нового типа отпуска. При указании emogi - БАГ заведен в джира
     */
    @Test
    public void createNewTypeOfVacationIfSendEmogi() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("♣☺♂♣☺♣☺","♣☺♂♣♣☺♂♣♣☺♂♣");
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        System.out.println(response.getDescription());

        Assert.assertEquals(response.getDescription(),"Ошибка добавления или обновления записи в бд");
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
//---------------------------------------------------------------------------------







}


