package spec;

import api.vacation_types.VacationType;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;

public class Specifications {

    public static String token = "";
    public static String tokenUser = "";
    public static String tokenKadry = "";
    public static String tokenBuhgalter = "";
    public static String tokenSales = "";
    public static String tokenRN = "";
    public static String tokenAccount = "";
    public static String tokenTop = "";

    public Integer vacationTypeID = 0;

    public static String vacationTypeApi = "vacation-type/";

    public final String URL = "http://vacation-dev.lan/api/";
    // Запрос спецификация
    public static RequestSpecification requestSpec(String url) {
        RestAssured.authentication = basic("username", "password");
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .setContentType(ContentType.JSON)
                .build();
    }

    // Ответ спецификация на 200 код
    public static ResponseSpecification specResponseOK200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    // Ответ спецификация на 201 код
    public static ResponseSpecification specResponseOK201() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .build();
    }

    // Ответ спецификация на 204 код
    public static ResponseSpecification specResponseOK204() {
        return new ResponseSpecBuilder()
                .expectStatusCode(204)
                .build();
    }
    // Ответ спецификация на 400 код
    public static ResponseSpecification specResponseError400() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .build();
    }
    public static ResponseSpecification specResponseError401() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .build();
    }
    public static ResponseSpecification specResponseError403() {
        return new ResponseSpecBuilder()
                .expectStatusCode(403)
                .build();
    }
    // Ответ спецификация на 404 код
    public static ResponseSpecification specResponseError404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .build();
    }

    // Ответ спецификация на  код
    public static ResponseSpecification specResponseUnique(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .build();
    }

    public static void installSpecification(RequestSpecification request, ResponseSpecification response) {
        RestAssured.requestSpecification = request;
        RestAssured.responseSpecification = response;
    }

    //----------------------------------------------------------------------------------------

    /**
     * Генерация случайной строки
     * @param n
     * @return
     */
    public String RandomString(int n) {

        int length = n;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        return generatedString;
    }


    /**
     * Генерация случайного числа
     * @param n
     * @return
     */
    public Integer randomNumber(int n) {

        int length = n;
        boolean useLetters = false;
        boolean useNumbers = true;
        Integer generatedInteger = Integer.parseInt(RandomStringUtils.random(length, useLetters, useNumbers));
        return generatedInteger;
    }

    @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }
    /**
     * Получение токена Admin перед выполнением тестов
     * @throws JSONException
     */
    @BeforeSuite
    public void AuthWithAdmin() throws JSONException {
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
    @BeforeSuite
    public void AuthWithUser() throws JSONException {
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
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenUser = accessToken;
    }

  //  @BeforeSuite
    public void AuthWithKadry() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "kadry@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenKadry = accessToken;
    }

 //   @BeforeSuite
    public void AuthWithBuhgalter() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "buhgalter@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenBuhgalter = accessToken;
    }

 //   @BeforeSuite
    public void AuthWithSales() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "sales@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenSales = accessToken;
    }

 //   @BeforeSuite
    public void AuthWithRN() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "rn@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenRN = accessToken;
    }

 //   @BeforeSuite
    public void AuthWithAccount() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "accountt@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token")
                        .then().log().all();
        System.out.println(response);

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenAccount = accessToken;
    }

 //   @BeforeSuite
    public void AuthWithTop() throws JSONException {
        Response response =
                (Response) given()
                        .auth().preemptive().basic("core", "d11e83a3-95cc-460c-9289-511d36d3e3fb")
                        .contentType("application/x-www-form-urlencoded").log().all()
                        .formParam("grant_type", "password")
                        .formParam("username", "top@irlix.ru")
                        .formParam("password", "12345678")
                        .when()
                        .post("http://keycloak-dev.lan/auth/realms/freeipa-realm/protocol/openid-connect/token");
             /*           .then().log().all();
        System.out.println(response);*/

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessToken = jsonObject.get("access_token").toString();
        String tokenType = jsonObject.get("token_type").toString();
        System.out.println("Auth Token with type " + tokenType + "   " + accessToken);
        tokenTop = accessToken;
    }

    //-------- Delete after tests
    public static void deleteAllExtraVacationTypes(String url, String token, List list){
        for (int i=0;i<list.size();i++){
            installSpecification(requestSpec(url), specResponseOK204());
            given()
                    .header("Content-type", "application/json")
                    .header("Authorization", "Bearer "+token)
                    .when()
                    .delete(url+ vacationTypeApi + list.get(i))
                    .then()
                    .extract().response();
            System.out.println("Удален тип отпуска с ID: " + list.get(i));
        }
    }

    /**
     * Удаление типа отпуска
     * @param token
     * @param idVacationType
     */
    public void deleteVacationType(String url, String token, Integer idVacationType){
        installSpecification(requestSpec(URL), specResponseOK204());
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .delete(url+ vacationTypeApi + idVacationType)
                .then()
                .extract().response();
        System.out.println("Тип отпуска с id: " + idVacationType +  " был удален.");
    }
    public static void deleteVacationTypes(String url) {
        // вычисляем количество записей
        Integer count = 0;
        installSpecification(requestSpec(url), specResponseOK200());
        List<VacationType> list = given().header("Authorization", "Bearer "+token)
                .when()
                .get(url + vacationTypeApi)
                .then()
                //.then().log().all()
                .extract().jsonPath().getList("",VacationType.class);

        List<Integer> idTypes = list.stream().map(VacationType::getId).collect(Collectors.toList());
        System.out.println(idTypes);

        //--- если типов отпусков больше 6 - то удалить лишние
        if (idTypes.size()>6) {
            for (int i=6;i<idTypes.size();i++){
                installSpecification(requestSpec(url), specResponseOK204());
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer "+token)
                        .when()
                        .delete(url + vacationTypeApi + idTypes.get(i))
                        .then()
                        .extract().response();
            }
        }

        // проверяем количество записей, что их 6
        installSpecification(requestSpec(url), specResponseOK200());
        List<VacationType> listAfterDelete = given().header("Authorization", "Bearer "+token)
                .when()
                .get(url + vacationTypeApi)
                .then()
                //.then().log().all()
                .extract().jsonPath().getList("",VacationType.class);
        List<Integer> idTypesAfterDelete = list.stream().map(VacationType::getId).collect(Collectors.toList());
        System.out.println("ID отпусков после удаления: " + idTypesAfterDelete);
        Assert.assertEquals(listAfterDelete.size(),6);

    }
}

