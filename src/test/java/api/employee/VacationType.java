package api.employee;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;

@Getter
@Setter
public class VacationType {
    private Integer id;
    private String value;
    private String description;

    public VacationType() {super();
    }

    public VacationType(Integer id, String value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }
}
