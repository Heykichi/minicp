package minicp.ANDOR_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlicedTable {
    private Map<Integer, Integer> pattern = null;
    private List<List<SlicedTable>> subSlicedTables = null;

    public SlicedTable(Map<Integer, Integer> pattern, List<List<SlicedTable>> subSlicedTables) {
        this.pattern = pattern;
        this.subSlicedTables = subSlicedTables;
    }

    public SlicedTable(Map<Integer, Integer> pattern) {
        this.pattern = pattern;
        this.subSlicedTables = new ArrayList<List<SlicedTable>>();
    }

    public SlicedTable() {
        this.subSlicedTables = new ArrayList<List<SlicedTable>>();
    }

    public Map<Integer, Integer> getPattern() {return pattern;}


    public List<List<SlicedTable>> getSubSlicedTables() {return subSlicedTables;}


    public static List<Map<Integer, Integer>> computeSlicedTable(List<SlicedTable> SlicedTables, int limit){
        List<Map<Integer, Integer>> solutions = new ArrayList<>();
        for (SlicedTable st : SlicedTables ){
            solutions.addAll(computeSubTables(st,limit));
            if (solutions.size() >= limit) {
                break;
            }
        }
        return solutions;
    }

    private static List<Map<Integer, Integer>> computeSubTables(SlicedTable slicedTable, int limit){
        List<Map<Integer, Integer>> solutions = new ArrayList<>();
        solutions.add(slicedTable.getPattern());

        if (slicedTable.getSubSlicedTables() == null || slicedTable.getSubSlicedTables().isEmpty()){
            Map<Integer, Integer> p = slicedTable.getPattern();
            List<Map<Integer, Integer>> one = new ArrayList<>(1);
            if (p != null) {
                one.add(new HashMap<>(p));
            }  else {
                throw new IllegalStateException("Null pattern ");
                //return null;
            }
            return one;
        }

        for (List<SlicedTable> subTables : slicedTable.getSubSlicedTables()){

            List<Map<Integer, Integer>> subsolutions = new ArrayList<>();
            for (SlicedTable s : subTables) {
                if (subsolutions.size() >= limit) break;
                subsolutions.addAll(computeSubTables(s,limit));
            }
            solutions = combine(solutions, subsolutions,limit);
        }
        return solutions;
    }
    private static List<Map<Integer, Integer>> combine(List<Map<Integer, Integer>> a, List<Map<Integer, Integer>> b,int limit) {
        List<Map<Integer, Integer>> resultat = new ArrayList<>();
        for (Map<Integer, Integer> l1 : a) {
            for (Map<Integer, Integer> l2 : b) {
                Map<Integer, Integer> nouvelle = new HashMap<>();
                if (l1 != null) nouvelle.putAll(l1);
                nouvelle.putAll(l2);
                resultat.add(nouvelle);
            }
        }
        return resultat;
    }

}

// TODO fonction pour transformer une sliced tables en liste de solutions


