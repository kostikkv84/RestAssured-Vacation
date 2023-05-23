package api.vacation_types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacationType {
    private Integer id;
    private String value;
    private String description;

    public VacationType() {
        super();
    }

    public VacationType(Integer id, String value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }

}
