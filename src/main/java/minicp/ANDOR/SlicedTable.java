package minicp.ANDOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlicedTable {
    private Map<String, Integer> pattern;
    private List<SubTable> subtables;

    public SlicedTable(Map<String, Integer> pattern, int N) {
        this.pattern = pattern;
        this.subtables = new ArrayList<SubTable>();
    }

    public void addSubTable(SubTable subTable) {this.subtables.add(subTable);}

    public Map<String, Integer> getPattern() {return pattern;}

    public List<SubTable> getSubTables() {return subtables;}
}
// TO DO
// peut contenir des sliced tables au lieu des solutions
// fonction pour transformer une sliced tables en liste de solutions


