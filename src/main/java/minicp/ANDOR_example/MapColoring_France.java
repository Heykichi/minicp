package minicp.ANDOR_example;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.io.InputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static minicp.cp.BranchingScheme.firstFail;

public class MapColoring_France {
    public static void main(String[] args) {
        InputReader reader1 = new InputReader("data/graph_coloring/france/departments.txt");

        Map<String, String> names = new HashMap<>();
        try {
            while (true) {
                String name = reader1.getString();
                String code = reader1.getString();
                names.put(code, name);
            }
        } catch (RuntimeException e) {}
        List<String> index = new ArrayList<>(names.keySet());

        Solver cp = Factory.makeANDSolver(false);
        IntVar[] vars = Factory.makeIntVarArray(cp, names.size(), 4);

        InputReader reader2 = new InputReader("data/graph_coloring/france/neighbors.txt");
        System.out.println(index.size());
        try {
            while (true) {
                String input = reader2.getString();
                String[] parts = input.split(":");
                String[] neighbors = parts[1].split(",");
                for (String neighbor : neighbors) {
                    cp.post(Factory.allDifferent(new IntVar[]{vars[index.indexOf(parts[0])], vars[index.indexOf(neighbor)]}));
                }
            }
        } catch (RuntimeException e) {}

        DFSearch search = Factory.makeDfs(cp, firstFail(vars));

        search.onSolution(() -> {
//            for (int k = 0; k < vars.length; k++) {
//                int n = vars[k].min()+1;
//                if (vars[k].isFixed()){
//                    System.out.println(names.get(index.get(k))+ " " + n);
//                }
//            }
        });
        // https://paintmaps.com/map-charts/76/France-Detailed-map-chart
        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1);
        long fin = System.nanoTime();

        System.out.format("\nExecution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
