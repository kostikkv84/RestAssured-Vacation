package api.vacation_types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeVacationAddIfNumber {
    public Integer value;
    public Integer description;

    public TypeVacationAddIfNumber() {
        super();
    }

    public TypeVacationAddIfNumber(Integer value, Integer description) {
        this.value = value;
        this.description = description;
    }
}
