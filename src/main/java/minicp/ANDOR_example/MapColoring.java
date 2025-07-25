package minicp.ANDOR_example;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.io.InputReader;

import java.util.HashMap;
import java.util.Map;

import static minicp.cp.BranchingScheme.firstFail;

public class MapColoring {
    public static void main(String[] args) {

        InputReader reader1 = new InputReader("data/world/countries.txt");

        Map<Integer, String> countriesNames = new HashMap<>();
        try {
            int i = 0;
            while (true) {
                i++;
                String name = reader1.getString();
                int code = reader1.getInt();
                countriesNames.put(code, name);
            }
        } catch (RuntimeException e) {}

        Solver cp = Factory.makeANDSolver(false);
        IntVar[] countriesVars = Factory.makeIntVarArray(cp, countriesNames.size(), 4);

        InputReader reader2 = new InputReader("data/world/countries_neighbor.txt");

        try {
            while (true) {
                Integer[] neighbor = reader2.getIntLine();
                for (int k = 1; k < neighbor.length; k++) {
                    cp.post(Factory.allDifferent(new IntVar[]{countriesVars[neighbor[0]], countriesVars[neighbor[k]]}));
                }
            }
        } catch (RuntimeException e) {}

        System.out.println("start");

        DFSearch search = Factory.makeDfs(cp, firstFail(countriesVars));

        search.onSolution(() -> {
            for (int k = 0; k < countriesVars.length; k++) {
                int n = countriesVars[k].min()+1;
                System.out.println(countriesNames.get(k) + " " + n);
            }
        });
        // https://paintmaps.com/map-charts/293/World-map-chart
        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1);
        long fin = System.nanoTime();

        System.out.format("\nExecution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
