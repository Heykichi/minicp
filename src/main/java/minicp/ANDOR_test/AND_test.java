package minicp.ANDOR_test;

import minicp.ANDOR.AND_DFSearch;
import minicp.ANDOR.AND_DFSearch.B_AND;
import minicp.ANDOR.AND_DFSearch.B_OR;
import minicp.ANDOR.Branch;
import minicp.ANDOR.Branch;
import minicp.cp.Factory;
import minicp.engine.core.Constraint;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;
import minicp.util.Procedure;

import static minicp.ANDOR.AND_DFSearch.Branching2;


public class AND_test {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 3;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);

        IntVar Y = Factory.makeIntVar(cp,5);

        Y.fix(4);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));


        B_OR B_OR1 = new B_OR(null,new IntVar[]{X[1],X[0]});
        B_OR B_OR2 = new B_OR(null,new IntVar[]{Z[1],Z[0]});
        B_AND b_and = new B_AND(new B_OR[]{B_OR1,B_OR2});
        B_OR B_ORR2 = new B_OR(new AND_DFSearch.AND_Branch[]{b_and},new IntVar[]{Y});

        AND_DFSearch search = Factory.makeAND_Dfs(cp, () -> {
            Procedure[] P = Branching2(cp,X);
            Procedure left = () -> System.out.println("1");
            Procedure right = () -> System.out.println("2");
            return new Procedure[]{left, right};

        });
        //AND_DFSearch search = Factory.makeAND_Dfs(cp);


        search.onSolution(() ->
                System.out.println( X[0] +" + " +  X[1] + " + " +  X[2] + " = " + Y + "\n" +  Z[0] +" + " +  Z[1] + " + " +  Z[2] + " = " + Y +"\n")
        );

        //


        SearchStatistics stats = search.solve(B_ORR2,statistics -> statistics.numberOfSolutions() == 1000);

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);
    }
}
