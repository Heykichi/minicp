package minicp.ANDOR_example;

import minicp.ANDOR_engine.AND_DFSearch;
import minicp.ANDOR_engine.Branch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;


public class AND_PS_example1_4 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 2;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);
        IntVar Y = Factory.makeIntVar(cp,5);

        //Y.fix(2);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        Branch subB1 = new Branch(X);
        Branch subB2 = new Branch(Z);
        Branch B = new Branch(new IntVar[]{Y}, new Branch[]{subB1,subB2});

        AND_DFSearch search = Factory.makeAND_Dfs(cp, () -> {
            return B;
        });

        search.onSolution(() ->
                System.out.println( "1) " + X[0] +" + " +  X[1] + " = " + Y + "\n2) " +  Z[0] +" + " +  Z[1] + " = " + Y +"\n")
        );


        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);
	    System.out.println("=======================================================================");
        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);
    }
}
