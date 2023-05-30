package api;

import BaseClasses.ResponseModules;
import api.vacation_types.VacationTypeNotAuthorized;
import org.testng.Assert;
import org.testng.annotations.Test;
import spec.Specifications;

import static io.restassured.RestAssured.given;

public class Tests_DeleteVacationTypes extends Specifications {

@Test
    public void deleteIfAuth() {
    //создаем тип отпуска
    ResponseModules response = new ResponseModules();
    Integer id = response.createNewVacationType(token, "new value", "new description");
    //удаляем
    response.deleteVacationType(token, id);
    // проверяем, что удален
    response.getVacationTypeOnIDError(token, id);
}

    @Test
    public void deleteIfNotAuth() {
        //создаем тип отпуска
        ResponseModules response = new ResponseModules();
        Integer id = response.createNewVacationType(token, "new value123", "new description123");
        //удаляем
        response.deleteVacationTypeNotAuth(URL, id);
        // проверяем, что удален
        VacationTypeNotAuthorized error = new VacationTypeNotAuthorized();
        // Проверяем, что проверка на ошибку возвращает Истину
        Assert.assertTrue(error.notAuthError(URL, token, "value", "descroption"), "Ответ не содержит ошибку");
    }

}
