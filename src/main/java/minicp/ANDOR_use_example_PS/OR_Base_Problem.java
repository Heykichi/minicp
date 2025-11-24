package minicp.ANDOR_use_example_PS;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.Procedure;

import static minicp.ANDOR_use_example.AND_example1_1.printSum;

public class OR_Base_Problem {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);
        
        int index = 3;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 5);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 5);

        IntVar Y = Factory.makeIntVar(cp,5);

        //Y.fix(3);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

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
//            System.out.print("1) ");
//            printSum(X,Y);
//
//            System.out.print("2) ");
//            printSum(Z,Y);
//            System.out.println();
        });

        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 2000000);
        long fin = System.nanoTime();

        System.out.println("=======================================================================");
        System.out.format("Execution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
