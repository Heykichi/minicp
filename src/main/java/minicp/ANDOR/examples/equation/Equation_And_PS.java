package minicp.ANDOR.examples.equation;

import minicp.ANDOR.DFSearch_And_PS;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import static minicp.ANDOR.Scheme.firstFail;
import static minicp.ANDOR.Scheme.naiveTreeBuilding;
import static minicp.ANDOR.examples.equation.Equation_And_CS.printSum;


public class Equation_And_PS {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 4;
        int domain = 4;
        IntVar[] X = Factory.makeIntVarArray(cp, index, domain);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, domain);
        IntVar Y = Factory.makeIntVar(cp, domain);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        DFSearch_And_PS search = Factory.makeAND_Dfs_PS(cp, naiveTreeBuilding(cp,1,4),firstFail());

        search.setBranching(firstFail());

        search.onSolution(() -> {
            printSum(X,Y);
            printSum(Z,Y);
            System.out.println();
        });

        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(100,true);
        long fin = System.nanoTime();
        System.out.println("=======================================================================");
        System.out.format("Execution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
