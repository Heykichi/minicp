package minicp.ANDOR_use_example_PS;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.Procedure;

public class OR_Base_Problem2 {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);

        int index = 3;
        IntVar[] X = Factory.makeIntVarArray(cp, index, index);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, index);

        cp.post(Factory.allDifferent(X));
        cp.post(Factory.allDifferent(Z));

        cp.post(Factory.equal(X[0], Z[0]));

        DFSearch search = Factory.makeDfs(cp, () -> {
            int idx = -1; // index of the first variable that is not fixed
            boolean axe = true;
            for (int k = 0; k < X.length; k++)
                if (X[k].size() > 1) {
                    idx = k;
                    break;
                }
            if (idx == -1)
                for (int k = 0; k < Z.length; k++)
                    if (Z[k].size() > 1) {
                        idx = k;
                        axe = false;
                        break;
                    }
            if (idx == -1)
                return new Procedure[0];
            else {
                IntVar qi = axe ? X[idx] : Z[idx];
                int v = qi.min();
                Procedure left = () -> cp.post(Factory.equal(qi, v));
                Procedure right = () -> cp.post(Factory.notEqual(qi, v));
                return new Procedure[]{left, right};
            }
        });

        search.onSolution(() -> {
            System.out.println(" "+ X[0] + " - " + X[1] + " - " + X[2] );
            System.out.println("  | ");
            System.out.println(" "+ Z[0] + " - " + Z[1] + " - " + Z[2] + "\n");
        });
        
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
