package api.vacation;

import groovy.transform.Sealed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeVacationAdd {
    public String value;
    public String description;

    public TypeVacationAdd() {
        super();
    }

    public TypeVacationAdd(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
