package minicp.ANDOR_use_example;

import minicp.ANDOR_engine.AND_DFSearch_partial_solution;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import static minicp.ANDOR_engine.AND_BranchingScheme.BasicTreeBuilding;
import static minicp.ANDOR_engine.AND_BranchingScheme.firstFail;


public class AND_PS_example2 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 3;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);

        cp.post(Factory.allDifferent(X));
        cp.post(Factory.allDifferent(Z));

        cp.post(Factory.equal(X[0], Z[0]));

        //System.out.println(cp.getGraph().toString());

        // Branch(IntVar[] variables, Branch[] branches, boolean rebranching)
        // we fix variables, then branches. If rebranching == true, we call rebranching (in series for an OR branch or in parallel for an AND branch)
        // only one rebranching is possible to avoid searching for a node multiple times

        // the branching must return a branch.
        // In the case of AND branches, variables assigned to subbranches must be removed (graph.removeNode(Intvar v) or graph.removeNode(Intvar[] v)).
        //
        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, BasicTreeBuilding(cp));

        search.setBranching(firstFail());

        search.onSolution(() -> {
            System.out.println(" "+ X[0] + " - " + X[1] + " - " + X[2] );
            System.out.println("  | ");
            System.out.println(" "+ Z[0] + " - " + Z[1] + " - " + Z[2] + "\n");
        });


        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);
        System.out.println("=======================================================================");
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
