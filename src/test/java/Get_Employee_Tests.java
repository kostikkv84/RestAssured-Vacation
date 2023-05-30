import api.employee.EmployeeList;
import api.employee.ErrorParams;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import spec.Specifications;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class Get_Employee_Tests extends Specifications {

    Integer id = 0;

    //@Test
    public void sum(){
        Assert.assertEquals(5+9,14);
    }

    //--------- Получение сотрудников без параметров ---------------------------

    /**
     * Получение всех сотрудников
     */
    @Test
    public void getAllEmployee() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList list = new EmployeeList();

        Assert.assertTrue(list.getEmployeeAll(URL, token).getTotal() > 0);
        Assert.assertTrue(list.getEmployeeAll(URL, token).getContent().size() > 1);
    }

    /**
     * Получение данных по сотруднику по ID
     */
    @Test
    public void getEmployeeOnID() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList list = new EmployeeList();
        Assert.assertEquals(list.getEmployeeList(URL, token, "id", "366").getContent().get(0).getEmployeeId(), 366);
    }

    /**
     * Получение данных по сотруднику по ID
     */
    @Test
    public void getEmployeeCheckEmploymentDate(){
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList list = new EmployeeList();
        Assert.assertFalse(list.getEmployeeList(URL, token, "id", "26").getContent().get(0).getEmploymentDate().isEmpty(), "Дата приема на работу у сотрудника пуста");
    }

    /**
     * Получение данных по сотруднику по ID
     */
    @Test
    public void getEmployeeOnIDifNotExist() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = given()
                .header("Authorization", "Bearer " + token)
                .param("id", 789)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract().body().as(EmployeeList.class);
        //  System.out.println(response.getContent().get(0).getEmployeeId());
        Assert.assertEquals(response.getTotal(), 0);
    }

    /**
     * Получение данных по сотруднику без авторизации
     */
    @Test
    public void getEmployeeIfNotAuthorized() {
        installSpecification(requestSpec(URL), specResponseError401());
        RestAssured.given()
                .param("id", 2)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .assertThat().body(containsString("Not authorized"));
    }

    //------------ Проверка параметра Size ---------------------------------------------

    /**
     * Error. Получение данных по сотрудникам Size = 0
     */
    @Test
    public void getEmployeeIfSize0() {
        installSpecification(requestSpec(URL), specResponseError400());
        ErrorParams response = new ErrorParams();
        Assert.assertEquals(response.getError(URL, token, "size", "0").get(0).getDescription(), "Поле size - Значение должно быть меньше 500 и больше 1");
    }

    /**
     * Получение данных по сотрудникам Size = 501
     */
    @Test
    public void getEmployeeIfSize501() {
        installSpecification(requestSpec(URL), specResponseError400());
        ErrorParams response = new ErrorParams();
        Assert.assertEquals(response.getError(URL, token, "size", "501").get(0).getDescription(), "Поле size - Значение должно быть меньше 500 и больше 1");
    }

    /**
     * Получение данных по сотрудникам Size = 1
     */
    @Test
    public void getEmployeeIfSize1() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = new EmployeeList();
        Assert.assertEquals(response.getEmployeeList(URL,token,"size", "1").getContent().size(), 1);
    }

    /**
     * Получение данных по сотрудникам Size = 500
     */
    @Test
    public void getEmployeeIfSize500() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = new EmployeeList();
        // System.out.println(response.get(0).getDescription());
        Assert.assertTrue(response.getEmployeeList(URL, token, "size", "500").getContent().size() > 10);
    }

    /**
     * Проверка параметризированного теста
     */
    @Test
    @Parameters("size")
    public void sizeParamsTest(String size) {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = given()
                .header("Authorization", "Bearer " + token)
                .param("size", size)
                .when()
                .get(URL + "/employee")
                .then()
                //.log().all()
                .extract().body().as(EmployeeList.class);
        System.out.println(response.getContent().size());
        Assert.assertEquals(response.getContent().size(), size);
    }

    /**
     * Тест проверяет, что параметр Size выводит заданное количество записей. Что работает.
     */
    @Test
    public void sizeParam_10_Test() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList employeeData = new EmployeeList();
        Assert.assertEquals(employeeData.getEmployeeList(URL,token,"size","10").getContent().size(),10);
  }


//------- Тесты на параметр Page -----------------------------------------

    /**
     * Получение данных по сотрудникам Page = -1
     */
    @Test
    public void getEmployeeIfPageMinusOne() {
        installSpecification(requestSpec(URL), specResponseError400());
        ErrorParams response = new ErrorParams();
        Assert.assertEquals(response.getError(URL, token, "page", "-1").get(0).getDescription(), "Поле page - must be greater than or equal to 0");
    }

    //------- Тесты на параметр Page -----------------------------------------

    /**
     * Получение данных по сотрудникам Page = 0
     */
    @Test
    public void getEmployeeIfPage0() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = given()
                .header("Authorization", "Bearer " + token)
                .param("size", 3)
                .param("page", 0)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract().body().as(EmployeeList.class);
        id = response.getContent().get(0).getEmployeeId();
        System.out.println("Получен ID первой записи равен: " + id);
        Assert.assertEquals(response.getContent().size(), 3);
    }

    /**
     * Получение данных по сотрудникам Page = 1
     */
    @Test(dependsOnMethods = {"getEmployeeIfPage0"})
    public void getEmployeeIfPage1() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = given()
                .header("Authorization", "Bearer " + token)
                .param("size", 3)
                .param("page", 1)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract().body().as(EmployeeList.class);
        Assert.assertNotEquals(response.getContent().get(0).getEmployeeId(), id);
    }

    //------------Тесты по параметру DateFrom ---------------------------

    /**
     * Получение отпусков после 01.01.2023 - проверка через парсинг строки с датой. Легкий способ
     * Заведен БАГ
     */
    @Test
    public void getEmployeeDateFrom2023() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "01.01.2023")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body()
                .as(EmployeeList.class);

        for (int i = 0; i < response.getContent().size(); i++) {
            for (int a = 0; a < response.getContent().get(i).getVacations().size(); a++) {
                String date = response.getContent().get(i).getVacations().get(a).getDateFrom(); // получаем дату
                String year = date.substring(date.length() - 4); // вырезаем 4 последних символа с годом
                System.out.println(year + " год ранее запрашиваемого 2023");
                Assert.assertTrue(Integer.parseInt(year) >= 2023, "Ошибка, запись ранее 2023"); // сравниваем
            }
        }

    }

    /**
     * Получение отпусков в диапазоне с 01.01.2023 по 31.12.2023
     * Заведен БАГ
     */
    @Test
    public void getEmployeeVacationsDateOnly2023() {
        installSpecification(requestSpec(URL), specResponseOK200());

        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "01.01.2023")
                .param("dateTo", "31.12.2023")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class);

        for (int i = 0; i < response.getContent().size(); i++) {
            for (int a = 0; a < response.getContent().get(i).getVacations().size(); a++) {
                String date = response.getContent().get(i).getVacations().get(a).getDateFrom(); // получаем дату
                String year = date.substring(date.length() - 4); // вырезаем 4 последних символа с годом
                System.out.println(year + " не входит в диапазон отпусков за 2023 год");
                Assert.assertEquals(Integer.parseInt(year), 2023, "Запись не содержит 2023 год");
            }
        }

    }

    /**
     * Error: Получение отпусков в диапазоне с 31.12.2023 по 01.01.2023
     * Заведен БАГ - негативный ТК
     */
    @Test
    public void getEmployeeDateFrom2024DateTo2023() {
        installSpecification(requestSpec(URL), specResponseOK200());
        ErrorParams response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "31.12.2023")
                .param("dateTo", "01.01.2023")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                 .body().as(ErrorParams.class);
        Assert.assertEquals(response.getDescription(), "Error", "Заведен баг http://jira.lan:8080/browse/IC-379");
    }

    /**
     * DateFrom = число
     */
    @Test
    public void getEmployeeDateFromNumber() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", 6)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [6]"), "Ошибка, в поле datуFrom число.");
    }

    /**
     * DateFrom = letters
     */
    @Test
    public void getEmployeeDateFromLetters() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "text")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [text]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }

    /**
     * DateFrom = spec simbols
     */
    @Test
    public void getEmployeeDateFromSpecSimbols() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "%&(#$")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [%&(#$]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }

    /**
     * DateFrom = emogi
     */
    @Test
    public void getEmployeeDateFromEmogi() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "♣☺♂")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [♣☺♂]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }

    //------------------------------------------------------------------------------------

    /**
     * Получение отпусков до 31.12.2022. Проверка через парсинг строки в дату - сложная проверка.
     * Заведен БАГ
     */
    @Test
    public void getEmployeeDateTo2023() throws ParseException {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateTo", "31.12.2022")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body()
                .as(EmployeeList.class);

        for (int i = 0; i < response.getContent().size(); i++) {
            for (int a = 0; a < response.getContent().get(i).getVacations().size(); a++) {

                String dateStr = response.getContent().get(i).getVacations().get(a).getDateTo();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                LocalDate dateBefore = LocalDate.parse("31.12.2022", formatter);
                System.out.println(localDate);
                Assert.assertTrue(localDate.isBefore(dateBefore), "Ошибка, запись позже 31-12-2022 - " + localDate);
            }
        }

    }


    /**
     * DateTo = число
     */
    @Test
    public void getEmployeeDateToNumber() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateTo", 156)
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [156]"), "Ошибка, в поле datуFrom число.");
    }

    /**
     * DateTo = letters
     */
    @Test
    public void getEmployeeDateToLetters() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "text1")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [text1]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }

    /**
     * DateTo = spec simbols
     */
    @Test
    public void getEmployeeDateToSpecSimbols() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "$#**/")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [$#**/]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }

    /**
     * DateTo = emogi
     */
    @Test
    public void getEmployeeDateToEmogi() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("dateFrom", "☺♂♣☺♂")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);
        Assert.assertTrue(response.get(0).getDescription().contains("failed for value [☺♂♣☺♂]"), "Ошибка, в поле datуFrom текст, а не дата.");
    }


    // --------- Тест на параметр PositionID --------------

    /**
     * PositionID - все сотрудники, отбор по ID должности
     */
    @Test
    public void getEmployeePositionIdOK() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = new EmployeeList();
        EmployeeList resp = response.getEmployeeList(URL, token, "positionId", "1");

                /*RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("positionId", "1")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class); */
        resp.getContent().stream().forEach(x -> Assert.assertEquals(x.getPositionId(),1));
  /*      for (int i = 0; i < response.getContent().size(); i++) { // пробегаемся по всем объектам.
            //        System.out.println(response.getContent().get(i).getPositionId());
            Assert.assertEquals(response.getContent().get(i).getPositionId(), 1, "Id должности отлично от 1");
  }*/
    }

    /**
     * PositionID - по не существующему ID. Записис не должны быть отобраны
     */
    @Test
    public void getEmployeePositionIdUnknown() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("positionId", "145")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class);

        Assert.assertEquals(response.getTotal(), 0, "Сотрудники с указанным ID должности найдены");
    }

    /**
     * PositionID - проверка на отпарвку текста
     */
    @Test
    public void getEmployeePositionIdText() {
        installSpecification(requestSpec(URL), specResponseError400());
        List<ErrorParams> response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("positionId", "text")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .jsonPath().getList("", ErrorParams.class);

        Assert.assertTrue(response.get(0).getDescription().contains("java.lang.NumberFormatException:"), "Сотрудники с указанным ID должности найдены");
    }

    // --------- Тест на параметр DepartmentID --------------

    /**
     * DepartmentID - по существующему ID.
     */
    @Test
    public void getEmployeeDepartmentId() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("departmentId", "2")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class);
        for (int i = 0; i < response.getContent().size(); i++) { // пробегаемся по всем объектам.
            //        System.out.println(response.getContent().get(i).getPositionId());
            Assert.assertEquals(response.getContent().get(i).getDepartmentId(), 2, "Id должности отлично от 1");
        }
    }

    /**
     * DepartmentID - по НЕ существующему ID.
     */
    @Test
    public void getEmployeeDepartmentIdUnknown() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("departmentId", "226")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class);
       Assert.assertEquals(response.getTotal(), 0, "Количество больше 0");

    }

    //------ тест на FullName -----------------
    /**
     * employeeFullName - по существующей фамилии.
     */
    @Test
    public void getEmployeeOnFullName() {
        installSpecification(requestSpec(URL), specResponseOK200());
        EmployeeList response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .param("departmentId", "2")
                .when()
                .get(URL + "/employee")
                .then().log().all()
                .extract()
                .body().as(EmployeeList.class);
            Assert.assertEquals(response.getContent().get(0).getSurname(), "Довженко", "Фамилия не найдена");

    }








    //-------------------------------------------------
    @Test
    @Ignore
    public void getEmployeeOnIDMatchers(){
        installSpecification(requestSpec(URL), specResponseOK200());
        RestAssured.given()
                .header("Authorization", "Bearer "+token)
                .param("id",26)
                .when()
                .get(URL +"/employee/")
                .then().log().all()
                .assertThat()
                .body("id", is(26))
                .body("surname", is("Довженко"));
        System.out.println("Данные по сотружнику найдены. ");
    }


}
