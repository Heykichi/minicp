package minicp.ANDOR.examples.equation;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;
import minicp.util.Procedure;

import static minicp.ANDOR.examples.equation.Equation_And_CS.printSum;

public class Equation_Or {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);

        int index = 4;
        int domain = 4;
        IntVar[] X = Factory.makeIntVarArray(cp, index, domain);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, domain);
        IntVar Y = Factory.makeIntVar(cp, domain);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        DFSearch search = Factory.makeDfs(cp, () -> {
            int idx = -1; // index of the first variable that is not fixed
            boolean axe = true;
            if (!Y.isFixed()){
                int v = Y.min();
                Procedure left = () -> cp.post(Factory.equal(Y, v));
                Procedure right = () -> cp.post(Factory.notEqual(Y, v));
                return new Procedure[]{left, right};
            }
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
            printSum(X,Y);
            printSum(Z,Y);
            System.out.println();
        });

        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 100);
        long fin = System.nanoTime();
        System.out.println("=======================================================================");
        System.out.format("Execution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
