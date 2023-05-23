import BaseClasses.ResponseModules;
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

}
