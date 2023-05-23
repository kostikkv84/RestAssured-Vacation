package api.vacation_types;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VacationTypeError404 {
    private Date timestamp;
    private int status;
    private String error;
    private String path;

    public VacationTypeError404() {super();
    }

    public VacationTypeError404(Date timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }
}
