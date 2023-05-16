package api;

import api.vacation.TypeVacationAdd;
import api.vacation.VacationType;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import spec.Specifications;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;

public class TestsForExample extends Specifications {
    public static String token = "";
    public static String tokenUser = "";

    @BeforeTest
    @Ignore
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }

    /**
     * Получение токена Admin перед выполнением тестов
     * @throws JSONException
     */
    @BeforeTest
    @Ignore
    public void testOAuthWithAdmin() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "admin")
                        .formParam("password", "admin")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        token = accessToken;
    }
    @BeforeTest
    @Ignore
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
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Oauth Token with type " + tokenType + "   " + accessToken);
        tokenUser = accessToken;
    }

    /**
     * Удаление лишних типов отпусков после прохождения тестов - Очистка
     */
    //   @AfterTest
    @Test
    @Ignore
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
        System.out.println(idTypesAfterDelete);

        Assert.assertEquals(listAfterDelete.size(),6);

    }

    /**
     * Проверка схему -
     */
    @Test
    @Ignore
    public void vacationTypeCheckJsonSchema() {
        installSpecification(requestSpec(URL), specResponseOK200());
        RestAssured.given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType/5")
                .then().log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("VacationTypeSchema.json"));
        //    .extract().body().as(VacationType.class);
        //   System.out.println(vacationType.getValue());
        //  Assert.assertTrue(vacationType.getValue().contains("По уходу за ребенком"), " Значение типа отпуска 5 не совпадает с - По уходу за ребенком "); // проверка возвращаемого значения в Responce
    }

    /**
     * Простой JsonParser для примера - .then().using().defaultParser(Parser.JSON)
     */
    @Test
    @Ignore
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
                .body("error", Matchers.is("Not authorized"));
    }

    /**
     * Проверка данных в полях VacationType - версия с Hamcrest Matchers
     */
    @Test
    @Ignore
    public void vacationTypeCheckFieldsValues() {
        installSpecification(requestSpec(URL), specResponseOK200());
        RestAssured.given().header("Authorization", "Bearer "+token)
                .when()
                .get(URL + "/vacationType/5")
                .then().log().all()
                .assertThat()
                .body("id", is(5))
                .body("value", is("По уходу за ребенком"))
                .body("description", is("описание для По уходу за ребенком"));
        //    .extract().body().as(VacationType.class);
        //   System.out.println(vacationType.getValue());
        //  Assert.assertTrue(vacationType.getValue().contains("По уходу за ребенком"), " Значение типа отпуска 5 не совпадает с - По уходу за ребенком "); // проверка возвращаемого значения в Responce
    }
}
