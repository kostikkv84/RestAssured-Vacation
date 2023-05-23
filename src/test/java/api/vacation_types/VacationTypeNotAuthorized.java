package api.vacation_types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacationTypeNotAuthorized {
    private String error;

    public VacationTypeNotAuthorized() {
        super();
    }

    public VacationTypeNotAuthorized(String error) {
        this.error = error;
    }
}
