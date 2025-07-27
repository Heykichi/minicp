package minicp.ANDOR_use_example;

import minicp.ANDOR_engine.AND_DFSearch;
import minicp.ANDOR_engine.Branch;
import minicp.ANDOR_engine.SubBranch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import static minicp.ANDOR_use_example.AND_example1_1.printSum;


public class AND_example1_0 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 4;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);
        IntVar Y = Factory.makeIntVar(cp,4);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        //System.out.println(cp.getGraph().toString());

        // Branch(IntVar[] variables, Branch[] branches, boolean rebranching)
        // we fix variables, then branches. If rebranching == true, we call rebranching (in series for an OR branch or in parallel for an AND branch)
        // only one rebranching is possible to avoid searching for a node multiple times

        // the branching must return a branch.
        // In the case of AND branches, variables assigned to subbranches must be removed (graph.removeNode(Intvar v) or graph.removeNode(Intvar[] v)).
        //
        SubBranch subB1 = new SubBranch(X, true);
        SubBranch subB2 = new SubBranch(Z, true);

        IntVar[] combined = new IntVar[9];
        combined[0] = Y;
        System.arraycopy(X, 0, combined, 1, X.length);
        System.arraycopy(Z, 0, combined, 5, Z.length);

        Branch B = new Branch(combined);

        AND_DFSearch search = Factory.makeAND_Dfs(cp, () -> {
            if (!Y.isFixed()) {
                return B;
            }
            return null;
        });

        search.onSolution(() -> {
            System.out.print("1) ");
            printSum(X,Y);

            System.out.print("2) ");
            printSum(Z,Y);
            System.out.println();
        });

        SearchStatistics stats = search.solve(1);
        System.out.println("=======================================================================");
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);
    }
}
