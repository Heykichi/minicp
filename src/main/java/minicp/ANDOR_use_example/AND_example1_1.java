package minicp.ANDOR_use_example;

import minicp.ANDOR_engine.DFSearch_And_CS;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;
import static minicp.ANDOR_engine.AND_Scheme.*;


public class AND_example1_1 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 4;
        int domain = 4;
        IntVar[] X = Factory.makeIntVarArray(cp, index, domain);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, domain);
        IntVar Y = Factory.makeIntVar(cp, domain);
        System.out.println(Y);
        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        DFSearch_And_CS search = Factory.makeAND_Dfs(cp, naiveTreeBuilding(cp,1,2),firstFail());

        search.setBranching(firstFail());

        search.onSolution(() -> {
            printSum(X,Y);
            printSum(Z,Y);
            System.out.println();
        });

        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(true);
        long fin = System.nanoTime();
        System.out.println("=======================================================================");
        System.out.format("Execution time : %s ms\n", (fin - debut) / 1_000_000);
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }

    public static void printSum(IntVar[] vars, IntVar sum){
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < vars.length -1; i += 1) {
            expression.append(vars[i]).append(" + ");
        }
        expression.append(vars[vars.length-1]);
        System.out.println(expression.toString() + " = " + sum);
    }
}
