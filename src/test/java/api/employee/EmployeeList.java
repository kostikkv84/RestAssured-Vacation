package api.employee;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EmployeeList {
    private ArrayList<Employee> content;
    private Integer total;

    public EmployeeList() {super();
    }

    public EmployeeList(ArrayList<Employee> content, Integer total) {
        this.content = content;
        this.total = total;
    }
}
