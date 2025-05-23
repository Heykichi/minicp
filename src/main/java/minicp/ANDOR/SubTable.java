package minicp.ANDOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubTable {
    private List<Map<String, Integer>> variables;

    public SubTable() {
        this.variables = new ArrayList<>();
    }

    public void addSolution(Map<String, Integer> variables) {
        this.variables.add(variables);
    }

    public List<Map<String, Integer>> getSolutions() {
        return variables;
    }
}
