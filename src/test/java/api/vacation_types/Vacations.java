package api.vacation_types;

public class Vacations {
    private Integer id;
    private String dateFrom;
    private String dateTo;
    private VacationType vacationType;
    private String vacationStatus;

    public Vacations() { super(); }

    public Vacations(Integer id, String dateFrom, String dateTo, VacationType vacationType, String vacationStatus) {
        this.id = id;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.vacationType = vacationType;
        this.vacationStatus = vacationStatus;
    }
}
