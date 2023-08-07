package VacationTypesTests;

import BaseClasses.ResponseModules;
import api.vacation_types.*;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;


public class Tests_PostVacationType extends Specifications {
    List listToDelete = new ArrayList();
    public Integer vacationTypeID = 0;
    private String value = "";
    private String description = "";

    /**
     * Создание нового типа отпуска. Авторизован - Admin
     */

    @Test (priority = -2)
    public void createNewTypeOfVacation(){
        installSpecification(requestSpec(URL), specResponseOK201()); // проверка статуса ответа
        VacationType response = VacationType.createVacationStr(URL,token,"NewType", "New description");

        vacationTypeID = response.getId();
        value = response.getValue();
        description = response.getDescription();

        listToDelete.add(response.getId());
        System.out.println(value);
        System.out.println(listToDelete);
        Assert.assertEquals(response.getValue(), "NewType");

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

     //   deleteVacationType(URL, token,vacationTypeID);
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
                .post(URL + vacationTypeApi)
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
        Assert.assertTrue(error.notAuthError(URL, token, "value", "description"), "Ответ не содержит ошибку");
    }

    /**
     * Создание нового типа отпуска если значение (Value) отпуска уже существует.
     * Value - уникально.
     */
    @Test
    public void createNewTypeOfVacationIfValueExist() {
        installSpecification(requestSpec(URL), specResponseError400());
        VacationTypeError error = new VacationTypeError();
        Assert.assertEquals(error.errorCreateVacationAdd(URL,token, value, "Some descr").getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Создание нового типа отпуска если значение (Description) отпуска уже существует - УСПЕХ - ошибки быть не должно.
     */
    @Test
    public void createNewTypeOfVacationIfDescriptionExist() {
        installSpecification(requestSpec(URL), specResponseOK201());
        VacationType response  = VacationType.createVacationStr(URL,token, "Some value1", "описание для Без сохранения ЗП");
        listToDelete.add(response.getId());
        System.out.println(listToDelete);
        Assert.assertEquals(response.getDescription(),"описание для Без сохранения ЗП");
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
     * Создание нового типа отпуска если значение (Value) = Empty (" ").
     */
    @Test
    public void createNewTypeVacationIfValueIsBoolean() {
        installSpecification(requestSpec(URL), specResponseError400());

        Assert.assertEquals(VacationTypeError.errorCreateVacationType(URL,token, " ", "Some descripton" ).get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
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
        VacationType response = VacationType.createVacationStr(URL,token, text,RandomString(10));
        // Проверяем, что создан тип отпуска с Value = 255 символов
        listToDelete.add(response.getId());
        System.out.println(listToDelete);
        Assert.assertEquals(response.getValue(), text, "Содержимое value не совпадает.");

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
     * Попытка получения удаленного типа отпуска, после удаления
     */
    @Test (dependsOnMethods={"getVacationCreatedTypeOnID"})
    @Ignore
    public void checkDeletedTypeID(){
        // удаление типа отпуска
        ResponseModules delete = new ResponseModules();
        delete.deleteVacationType(URL, token, vacationTypeID);
        // попытка получить удаленный тип отпуска
        ResponseModules response = new ResponseModules();
        Assert.assertTrue(response.getVacationTypeOnIDError(token,vacationTypeID));
    }

    /**
     * Создание нового типа отпуска если значение (Description) = 1000 length.
     */
    @Test
    public void createNewTypeVacationIfDescription_1000_Symbols() {
        installSpecification(requestSpec(URL), specResponseOK201());
        String text = RandomString(1000);
        VacationType response = VacationType.createVacationStr(URL,token, RandomString(10),text);
        // Проверяем, что создан тип отпуска с Value = 255 символов
        listToDelete.add(response.getId());
        System.out.println(listToDelete);
        // Проверяем, что создан тип отпуска с Description = 1000 символов
        Assert.assertEquals(response.getDescription(), text, "Содержимое description не совпадает.");
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
     * Создание нового типа отпуска если Value, Description = Number.
     */
    @Test
    public void createNewTypeVacationIfValueNumber() {
        installSpecification(requestSpec(URL), specResponseOK201());
        Integer value = randomNumber(4);
        VacationType response = VacationType.createVacationInt(URL,token, value,randomNumber(3));

        listToDelete.add(response.getId());
        System.out.println(listToDelete);
        // Проверяем, что создан тип отпуска с value = число
        Assert.assertEquals(Integer.parseInt(response.getValue()), value, "Содержимое value не совпадает.");
    }

    /**
     * Создание нового типа отпуска. При указании emogi - БАГ заведен в джира
     */
    @Test (description = "Тест на эмоджи в теле запроса на создание записи")
    public void createNewTypeOfVacationIfSendEmogi() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("♣☺♂♣☺♣☺","♣☺♂♣♣☺♂♣♣☺♂♣");
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + vacationTypeApi)
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
    public void deleteVacationAfterTests(){
        //deleteVacationTypes(URL);
        System.out.println(listToDelete);
        deleteAllExtraVacationTypes(URL, token,listToDelete);
    }
//---------------------------------------------------------------------------------







}


