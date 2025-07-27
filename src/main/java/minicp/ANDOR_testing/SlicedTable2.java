package minicp.ANDOR_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlicedTable2 {
    private Map<Integer, Integer> pattern;
    private List<List<SlicedTable>> subSlicedTables;

    public SlicedTable2(Map<Integer, Integer> pattern, List<List<SlicedTable>> subSlicedTables) {
        this.pattern = (pattern != null) ? pattern : new HashMap<>();
        this.subSlicedTables = (subSlicedTables != null) ? subSlicedTables : new ArrayList<>();
    }

    public SlicedTable2(Map<Integer, Integer> pattern) {
        this(pattern, new ArrayList<>());
    }

    public SlicedTable2() {
        this(new HashMap<>(), new ArrayList<>());
    }

    public Map<Integer, Integer> getPattern() {
        return pattern;
    }

    public List<List<SlicedTable>> getSubSlicedTables() {
        return subSlicedTables;
    }

    public static List<Map<Integer, Integer>> computeSlicedTable(List<SlicedTable> slicedTables, int limit) {
        List<Map<Integer, Integer>> solutions = new ArrayList<>();
        if (slicedTables == null || slicedTables.isEmpty()) {
            return solutions;
        }

        for (SlicedTable st : slicedTables) {
            List<Map<Integer, Integer>> subs = computeSubTables(st, limit);
            if (!subs.isEmpty()) {
                solutions.addAll(subs);
            }
            if (limit > 0 && solutions.size() >= limit) {
                break;
            }
        }
        return solutions;
    }

    private static List<Map<Integer, Integer>> computeSubTables(SlicedTable slicedTable, int limit) {
        List<Map<Integer, Integer>> solutions = new ArrayList<>();

        // Always start with a copy of the pattern (or empty if null)
        Map<Integer, Integer> basePattern = (slicedTable.getPattern() != null)
                ? new HashMap<>(slicedTable.getPattern())
                : new HashMap<>();
        solutions.add(basePattern);

        // Base case: no sub-tables
        if (slicedTable.getSubSlicedTables() == null || slicedTable.getSubSlicedTables().isEmpty()) {
            List<Map<Integer, Integer>> one = new ArrayList<>(1);
            one.add(basePattern);
            return one;
        }

        // Combine all sub-table solutions progressively
        for (List<SlicedTable> subTables : slicedTable.getSubSlicedTables()) {
            List<Map<Integer, Integer>> subsolutions = new ArrayList<>();
            for (SlicedTable s : subTables) {
                subsolutions.addAll(computeSubTables(s, limit));
            }
            solutions = combine(solutions, subsolutions, limit);
            if (limit > 0 && solutions.size() >= limit) {
                break;
            }
        }
        return solutions;
    }

    private static List<Map<Integer, Integer>> combine(List<Map<Integer, Integer>> a, List<Map<Integer, Integer>> b, int limit) {
        List<Map<Integer, Integer>> resultat = new ArrayList<>();
        if (a.isEmpty()) {
            a.add(new HashMap<>()); // Avoid starting with an empty set
        }
        if (b.isEmpty()) {
            return a; // Nothing to combine, keep 'a' as is
        }

        for (Map<Integer, Integer> l1 : a) {
            for (Map<Integer, Integer> l2 : b) {
                Map<Integer, Integer> nouvelle = new HashMap<>(l1);
                nouvelle.putAll(l2); // Combine maps
                resultat.add(nouvelle);
                if (limit > 0 && resultat.size() >= limit) {
                    return resultat;
                }
            }
        }
        return resultat;
    }
}
