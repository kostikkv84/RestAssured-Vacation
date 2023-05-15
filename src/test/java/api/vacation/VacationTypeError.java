package api.vacation;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VacationTypeError {

    private String id;
    private String description;
    private Date timestamp;

    public VacationTypeError() {super();
    }

    public VacationTypeError(String id, String description, Date timestamp) {
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
    }
}
