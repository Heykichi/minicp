package minicp.ANDOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlicedTable {
    private Map<String, Integer> pattern;
    private List<AND_DFSearch.SubTable> subtables;

    public SlicedTable(Map<String, Integer> pattern, int N) {
        this.pattern = pattern;
        this.subtables = new ArrayList<>();
    }

    public void addSubTable(AND_DFSearch.SubTable subTable) {
        this.subtables.add(subTable);
    }

    public Map<String, Integer> getPattern() {
        return pattern;
    }

    public List<AND_DFSearch.SubTable> getSubTables() {
        return subtables;
    }

    static class SubTable {
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
}


