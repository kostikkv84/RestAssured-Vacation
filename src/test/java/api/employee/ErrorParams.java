package api.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorParams {
    private String id;
    private String description;
    private String timestamp;

    public ErrorParams() {super();
    }

    public ErrorParams(String id, String description, String timestamp) {
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
    }
}
