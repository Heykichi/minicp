package minicp.ANDOR_use_example_PS;

import minicp.ANDOR_engine.AND_DFSearch_partial_solution;
import minicp.ANDOR_engine.Branch;
import minicp.ANDOR_engine.ConstraintGraph;
import minicp.ANDOR_engine.SubBranch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import static minicp.ANDOR_use_example.AND_example1_1.printSum;

public class AND_PS_example1_3 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 4;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);
        IntVar Y = Factory.makeIntVar(cp,4);

        //Y.fix(2);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        SubBranch subB1 = new SubBranch(X);
        SubBranch subB2 = new SubBranch(Z);
        Branch B = new Branch(new IntVar[]{Y}, new SubBranch[]{subB1,subB2});

        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, () -> {
            if (!Y.isFixed()) {
                System.out.println("111111111111111111");
                return B;
            }
            if (Y.isFixed() ) {
                ConstraintGraph graph = cp.getGraphWithStart();
                return new Branch(graph.getUnfixedVariables().toArray(new IntVar[0]));
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

        SearchStatistics stats = search.solve(2000);
        System.out.println("=======================================================================");
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
