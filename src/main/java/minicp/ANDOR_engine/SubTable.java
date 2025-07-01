package minicp.ANDOR_engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubTable {
    private List<Map<Integer, Integer>> variables;

    public SubTable() {
        this.variables = new ArrayList<>();
    }

    public void addSolution(Map<Integer, Integer> variables) {this.variables.add(variables);}

    public List<Map<Integer, Integer>> getSolutions() {return variables;}
}
