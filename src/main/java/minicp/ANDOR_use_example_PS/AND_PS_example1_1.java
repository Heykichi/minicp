package minicp.ANDOR_use_example_PS;

import minicp.ANDOR_engine.AND_DFSearch_partial_solution;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import static minicp.ANDOR_engine.AND_Scheme.naiveTreeBuilding;
import static minicp.ANDOR_engine.AND_Scheme.firstFail;


public class AND_PS_example1_1 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 3;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 5);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 5);
        IntVar Y = Factory.makeIntVar(cp, 6);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        //System.out.println(cp.getGraph().toString());

        // Branch(IntVar[] variables, Branch[] branches, boolean rebranching)
        // we fix variables, then branches. If rebranching == true, we call rebranching (in series for an OR branch or in parallel for an AND branch)
        // only one rebranching is possible to avoid searching for a node multiple times

        // the branching must return a branch.
        // In the case of AND branches, variables assigned to subbranches must be removed (graph.removeNode(Intvar v) or graph.removeNode(Intvar[] v)).
        //
        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, naiveTreeBuilding(cp, 1,5));

        search.setBranching(firstFail());

        search.onSolution(() -> {});

        long debut = System.nanoTime();
        SearchStatistics stats = search.solve(2000000, true);
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
