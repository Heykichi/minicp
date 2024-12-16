package minicp.ANDOR;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.DFSearch;
import minicp.search.SearchStatistics;

import minicp.ANDOR.AND_DFSearch.*;

import java.util.Arrays;

import static minicp.ANDOR.AND_DFSearch.Get_branches;

public class AND_test {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);
        int index = 2;
        IntVar[] V = Factory.makeIntVarArray(cp, index, index);
        IntVar[] H = Factory.makeIntVarArray(cp, index, index);

        //cp.post(Factory.equal(V[0], H[0]));
        for (int i = 0; i < index; i++){
            for (int j = i + 1; j < index; j++) {
                cp.post(Factory.notEqual(V[i], V[j]));
                cp.post(Factory.notEqual(H[i], H[j]));
            }
        }

        AND_DFSearch search = new AND_DFSearch(cp);

        search.onSolution(() ->
                //System.out.println("solution:"  + Arrays.toString(V))
                System.out.println("solution:" + Arrays.toString(H) + " - " + Arrays.toString(V))
        );

        // Get_branches(null)
        //IntVar[] V2 = new IntVar[]{V[1],V[2],V[3]};

        /*
        B_OR H_OR = new B_OR(null, new IntVar[]{H[1],H[2],H[3]});
        B_OR V_OR = new B_OR(null, new IntVar[]{V[1],V[2],V[3]});
        B_OR[] L_ORB = new B_OR[]{H_OR, V_OR};
        B_AND[] ANDB = new B_AND[]{new B_AND(L_ORB)};
        B_OR B_OR = new B_OR(ANDB,new IntVar[]{V[0]});*/

        B_OR B_OR1 = new B_OR(null,new IntVar[]{V[0],V[1]});
        B_OR B_OR2 = new B_OR(null,new IntVar[]{H[0],H[1]});

        B_OR B_OR3 = new B_OR(new B_OR[]{B_OR1},new IntVar[]{H[0],H[1]});


        B_AND b_and = new B_AND(new B_OR[]{B_OR1,B_OR2});

        SearchStatistics stats = search.solve(b_and,statistics -> statistics.numberOfSolutions() == 1000);
        //search.showTree("NQUEENS");

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

    }
}
