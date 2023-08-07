package VacationTypesTests;

import groovyjarjarpicocli.CommandLine;
import org.testng.annotations.AfterClass;
import spec.Specifications;

import java.util.ArrayList;
import java.util.List;

public class PostDataProviders extends Specifications {

    List listToDelete = new ArrayList();
    public Integer vacationTypeID = 0;





// -------- Dataproviders -----------------

    /**
     * Набор параметров для проверки деления на 2
     * @return
     */
    @org.testng.annotations.DataProvider(name="data-provider")
    public Object[][] evenNumbers() {
        return new Object[][]{{1, false}, {2, true}, {3, false}, {4, true}};
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

}
