package api;

import BaseClasses.ResponseModules;
import api.employee.EmployeeList;
import api.vacation_types.TypeVacationAdd;
import api.vacation_types.TypeVacationAddIfNumber;
import api.vacation_types.VacationType;
import api.vacation_types.VacationTypeError;
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

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class Tests_PostVacationType extends Specifications {

    public Integer vacationTypeID = 0;

  /*  @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }
    @BeforeClass
    public void testOAuthWithAdmin() throws JSONException {
        installSpecification(requestSpec(URL_TOKEN),specResponseOK200());
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("grant_type", "password")
                .formParam("username", "admin")
                .formParam("password", "admin")
                .when()
                .post(URL_TOKEN);

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        token = accessToken;
    }
    @BeforeClass
    public void testOAuthWithUser() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "konstantin.kostylev@irlix.ru")
                        .formParam("password", "P@ssw0rd4323")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        tokenUser = accessToken;
    }  */
//---------------------------------------------------------------------------

    /**
     * Создание нового типа отпуска. Авторизован - Admin
     */
    @Test (priority = -2)
    public void createNewTypeOfVacation() {
        installSpecification(requestSpec(URL), specResponseOK201()); // проверка статуса ответа
        // создается тип отпуска
        TypeVacationAdd requestBody = new TypeVacationAdd("TestType", "Test description");
        VacationType resp = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationType.class);
        System.out.println("СОздан новый тип отпуска с ID - " + resp.getId());
        Assert.assertEquals(resp.getValue(),"TestType"); // проверяется, что тип отпуска создан

        vacationTypeID = resp.getId();
      ResponseModules response = new ResponseModules();
   //   response.deleteVacationType(token,vacationTypeID); // удаляем созданный тип отпуска

    //  Assert.assertTrue(response.getVacationTypeOnIDError(token,vacationTypeID)); // проверяем успешное удаление
    }

    /**
     * Проверка созданного, на предидущем шаге типа отпуска с последующим удалением
     */
    @Test (dependsOnMethods={"createNewTypeOfVacation"}, priority = -1)
    public void getVacationCreatedTypeOnID(){
        installSpecification(requestSpec(URL), specResponseOK200());
            RestAssured.given().header("Authorization", "Bearer "+token)
                    .when()
                .get(URL + "/vacationType/"+vacationTypeID)
                .then().log().all()
                .assertThat()
                .body("id", is(vacationTypeID))
                .body("value", is("TestType"))
                .body("description", is("Test description"));

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
    //    System.out.println(vacationTypeID);
      //  Assert.assertEquals(response.getValue(),"TestType");
    }

    /**
     * Создание нового типа отпуска. Не авторизован!
     */
    @Test
    public void createNewTypeOfVacationNotAuthorized() {
        installSpecification(requestSpec(URL), specResponseError401());
        TypeVacationAdd requestBody = new TypeVacationAdd("TestType1","TestType Descriptions1");
        RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().using().defaultParser(Parser.JSON).log().all()
                .assertThat()
                .body("error", is("Not authorized"));
    }

    /**
     * Создание нового типа отпуска если значение (Value) отпуска уже существует.
     */
    @Test
    public void createNewTypeOfVacationIfValueExist() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Основной оплачиваемый","Some description");
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Создание нового типа отпуска если значение (Description) отпуска уже существует.
     */
    @Test
    public void createNewTypeOfVacationIfDescriptionExist() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Some value","описание для Дополнительный оплачиваемый");
        VacationTypeError response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationTypeError.class);
        Assert.assertEquals(response.getDescription(),"Ошибка добавления или обновления записи в бд");
    }

    /**
     * Создание нового типа отпуска если значение (Value) не указано - "".
     */
    @Test
    public void createNewTypeVacationIfValueIsEmpty() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("","Unique description of vacation type");
        List<VacationTypeError> error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(error.get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Value) = Null (" ").
     */
    @Test
    public void createNewTypeVacationIfValueIsNull() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("","Unique description of vacation type");
        List<VacationTypeError> error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(error.get(0).getDescription(),"Поле value: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Description) не указано - "".
     */
    @Test
    public void createNewTypeVacationIfDescriptionIsEmpty() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Unique_value","");
        List<VacationTypeError> error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(error.get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Description) = Null (" ").
     */
    @Test
    public void createNewTypeVacationIfDescriptionIsNull() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd("Unique type vacation value","  ");
        List<VacationTypeError> error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertEquals(error.get(0).getDescription(),"Поле description: поле не должно быть null и не должно быть пустым");
    }

    /**
     * Создание нового типа отпуска если значение (Value) = 255 length.
     */
    @Test
    public void createNewTypeVacationIfValue_255_Symbols() {
        installSpecification(requestSpec(URL), specResponseOK201());
        String text = RandomString(255);
        TypeVacationAdd requestBody = new TypeVacationAdd(text,RandomString(10));
        VacationType response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationType.class);
        vacationTypeID = response.getId();
        Assert.assertEquals(requestBody.getValue(),text,"Значение в Value не совпадает со сгенерированной строкой на 255 символов");

       ResponseModules delete = new ResponseModules();
       delete.deleteVacationType(token, response.getId());
    }

    /**
     * Создание нового типа отпуска если значение (Value) = 256 length.
     */
    @Test
    public void createNewTypeVacationIfValue_256_Symbols() {
        installSpecification(requestSpec(URL), specResponseError400());
        TypeVacationAdd requestBody = new TypeVacationAdd(RandomString(256),RandomString(10));
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().using().defaultParser(Parser.JSON).log().all()
                .assertThat()
                .body("description", is("Ошибка добавления или обновления записи в бд"));
       // Assert.assertEquals(response.getDescription(), "Ошибка добавления или обновления записи в бд", "Ожидаемое сообщение об ошибке записи в БД не получено .");
    }

    /**
     * ПОпытка получения удаленного типа отпуска, после удаления
     */
    @Test (dependsOnMethods={"createNewTypeVacationIfValue_255_Symbols"})
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
        TypeVacationAdd requestBody = new TypeVacationAdd(RandomString(10),text);
        VacationType response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().body().as(VacationType.class);
        Assert.assertEquals(response.getDescription(),text,"Значение в Value не совпадает со сгенерированной строкой на 255 символов");

        ResponseModules delete = new ResponseModules();
        delete.deleteVacationType(token,response.getId());
    }

    /**
     * Создание нового типа отпуска если значение (Description) = 1001 length.
     */
    @Test
    public void createNewTypeVacationIfDescription_1001_Symbols() {
        installSpecification(requestSpec(URL), specResponseError400());
        String text = RandomString(1001);
        TypeVacationAdd requestBody = new TypeVacationAdd(RandomString(10),text);
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body(requestBody)
                .when()
                .post(URL + "/vacationType")
                .then().using().defaultParser(Parser.JSON).log().all()
                .assertThat()
                .body("description", is("Ошибка добавления или обновления записи в бд"));
        System.out.println("Длинна строки отправленная в Description - " + text.length() + " символов");
        // Assert.assertEquals(response.getDescription(), "Ошибка добавления или обновления записи в бд", "Ожидаемое сообщение об ошибке записи в БД не получено .");
    }


    /**
     * Создание нового типа отпуска если Value и Description не указаны - пустой Body.
     */
    @Test
    public void createNewTypeVacationIfValueAndDescriptionIsEmpty() {
        installSpecification(requestSpec(URL), specResponseError400());
       // TypeVacationAdd requestBody = new TypeVacationAdd("Unique_value","");
        List<VacationTypeError> error = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .and()
                .body("{}")
                .when()
                .post(URL + "/vacationType")
                .then().log().all()
                .extract().jsonPath().getList("", VacationTypeError.class);
        Assert.assertTrue(error.get(0).getDescription().contains("поле не должно быть null и не должно быть пустым"));
        Assert.assertTrue(error.get(1).getDescription().contains("поле не должно быть null и не должно быть пустым"));
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


    @Test
    public void sizeParam_1_Test() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList employeeData = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .param("size","1")
                .when()
                .get(URL+"/employee")
                .then().log().all()
                .extract().body().as(EmployeeList.class);
        System.out.println("Количество отображаемых сотрудников = " + employeeData.getContent().size() + " - PASS");
        Assert.assertEquals(employeeData.getContent().size(),1);
    }

    /**
     * Тест проверяет, что параметр Size выводит заданное количество записей. Что работает.
     */
    @Test
    public void sizeParam_10_Test() {
        installSpecification(requestSpec(URL), specResponseOK200());
        List<EmployeeList> employeeData = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .param("size","10")
                .when()
                .get(URL+"/employee")
                .then().log().all()
                .extract().body().path("content");
        System.out.println("Количество отображаемых записей = " + employeeData.size());
        System.out.println(tokenUser + " + " + tokenUser);
        Assert.assertEquals(employeeData.size(),10);

     //   System.out.println("Количество отображаемых сотрудников = " + employeeData.getContent().size() + " - PASS");
     //   Assert.assertEquals(employeeData.getContent().size(),1);
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


