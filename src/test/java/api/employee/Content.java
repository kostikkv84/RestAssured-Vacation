package api.employee;

import api.vacation_types.Vacations;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Content {
    private Integer employeeId;
    private String name;
    private String surname;
    private String middleName;
    private Integer departmentId;
    private Integer positionId;
    private String email;
    private ArrayList<api.vacation_types.Vacations> vacations;
    private String employmentDate;

    public Content() {super();}

    public Content(Integer employeeId, String name, String surname, String middleName, Integer departmentId, Integer positionId, String email, ArrayList<Vacations> vacations, String employmentDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.email = email;
        this.vacations = vacations;
        this.employmentDate = employmentDate;
    }
}
