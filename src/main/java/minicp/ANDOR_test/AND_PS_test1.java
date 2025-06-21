package minicp.ANDOR_test;

import minicp.ANDOR.AND_DFSearch_partial_solution;
import minicp.ANDOR.Branch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import java.util.concurrent.atomic.AtomicInteger;


public class AND_PS_test1 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 2;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);
        //IntVar[] L = Factory.makeIntVarArray(cp, index, index);



        IntVar Y = Factory.makeIntVar(cp,5);

        //Y.fix(0);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        Branch subB1 = new Branch(X);
        Branch subB2 = new Branch(new IntVar[]{Z[0]},null, true);
        Branch B = new Branch(new IntVar[]{Y}, new Branch[]{subB1},true);

        AtomicInteger a = new AtomicInteger(1);

        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, () -> {
            if (!Y.isFixed()) {
                return B;
            }
            if (Y.isFixed() & !Z[0].isFixed() & !Z[1].isFixed()) {
                return subB2;
            }
            if (Y.isFixed() & Z[0].isFixed()) {
                return new Branch(Z);
            }
            return null;
        });

        search.onSolution(() ->
                System.out.println( "1) " + X[0] +" + " +  X[1] + " = " + Y + "\n2) " +  Z[0] +" + " +  Z[1] + " = " + Y +"\n")
        );



        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

        //System.identityHashCode(List[0]);

    }
}
