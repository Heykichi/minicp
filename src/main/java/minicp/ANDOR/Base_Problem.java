package minicp.ANDOR;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.Procedure;

import java.util.Arrays;

public class Base_Problem {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);
        IntVar[] V = Factory.makeIntVarArray(cp, 4, 4);
        IntVar[] H = Factory.makeIntVarArray(cp, 4, 4);

        cp.post(Factory.equal(V[2], H[2]));
        for (int i = 0; i < 4; i++){
            for (int j = i + 1; j < 4; j++) {
                cp.post(Factory.notEqual(V[i], V[j]));
                cp.post(Factory.notEqual(H[i], H[j]));
            }
        }

        DFSearch search = Factory.makeDfs(cp, () -> {
            int idx = -1; // index of the first variable that is not fixed
            boolean axe = true;
            for (int k = 0; k < H.length; k++)
                if (H[k].size() > 1) {
                    idx = k;
                    break;
                }
            if (idx == -1)
                for (int k = 0; k < H.length; k++)
                    if (V[k].size() > 1) {
                        idx = k;
                        axe = false;
                        break;
                    }
            if (idx == -1)
                return new Procedure[0];
            else {
                IntVar qi = axe ? H[idx] : V[idx];
                int v = qi.min();
                Procedure left = () -> cp.post(Factory.equal(qi, v));
                Procedure right = () -> cp.post(Factory.notEqual(qi, v));
                return new Procedure[]{left, right};
            }
        });

        search.onSolution(() ->
                System.out.println("solution:" + Arrays.toString(H) + " - " + Arrays.toString(V))
        );
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);

        //search.showTree("NQUEENS");

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
