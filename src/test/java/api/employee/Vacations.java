package api.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vacations {
    public int id;
    public String dateFrom;
    public String dateTo;
    public api.employee.VacationType vacationType;
    public String vacationStatus;

    public Vacations() {super();
    }

    public Vacations(int id, String dateFrom, String dateTo, VacationType vacationType, String vacationStatus) {
        this.id = id;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.vacationType = vacationType;
        this.vacationStatus = vacationStatus;
    }
}
