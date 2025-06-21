package minicp.ANDOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlicedTable {
    private Map<Integer, Integer> pattern= null;
    private List<SubTable> subtables;
    private List<SlicedTable> subSlicedTables;

    public SlicedTable(Map<Integer, Integer> pattern) {
        this.pattern = pattern;
        this.subtables = new ArrayList<SubTable>();
        this.subSlicedTables = new ArrayList<SlicedTable>();
    }

    public SlicedTable() {
        this.subtables = new ArrayList<SubTable>();
        this.subSlicedTables = new ArrayList<SlicedTable>();
    }

    public void addSubTable(SubTable subTable) {this.subtables.add(subTable);}

    public void addSubSlicedTable(SlicedTable subSlicedTable) {this.subSlicedTables.add(subSlicedTable);}

    public Map<Integer, Integer> getPattern() {return pattern;}

    public List<SubTable> getSubTables() {return subtables;}

    public List<SlicedTable> getSubSlicedTables() {return subSlicedTables;}
}
// TODO fonction pour transformer une sliced tables en liste de solutions


