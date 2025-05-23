package minicp.ANDOR_test;

import minicp.ANDOR.AND_DFSearch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import minicp.ANDOR.AND_DFSearch.*;

import java.util.Arrays;


public class AND_test {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);
        int index = 3;
        IntVar[] V = Factory.makeIntVarArray(cp, index, index);
        IntVar[] H = Factory.makeIntVarArray(cp, index, index);
        //IntVar[] L = Factory.makeIntVarArray(cp, index, index);

        cp.post(Factory.equal(V[0], H[0]));
        //cp.post(Factory.equal(L[2], H[2]));
        cp.post(Factory.allDifferent(V));
        cp.post(Factory.allDifferent(H));
//        for (int i = 0; i < index; i++){
//            for (int j = i + 1; j < index; j++) {
//                cp.post(Factory.notEqual(V[i], V[j]));
//                cp.post(Factory.notEqual(H[i], H[j]));
//                //cp.post(Factory.notEqual(L[i], L[j]));
//            }
//        }


        AND_DFSearch search = new AND_DFSearch(cp);

        search.onSolution(() ->
                //System.out.println("solution:"  + Arrays.toString(V))
                System.out.println("    V: " + Arrays.toString(V) + "\t H: " + Arrays.toString(H) )//+ "\n L:- " + Arrays.toString(L))
        );
        /*
        B_OR B_OR0 = new B_OR(null,new IntVar[]{L[0],H[1]});
        B_OR B_OR1 = new B_OR(null,new IntVar[]{V[1],V[2]});
        B_AND b_and2 = new B_AND(new B_OR[]{B_OR1,B_OR0});
        B_OR B_OR2 = new B_OR(new Branch[]{b_and2},new IntVar[]{H[2]});
        B_AND b_and = new B_AND(new B_OR[]{B_OR1,B_OR2});
        B_OR B_OR0 = new B_OR(new Branch[]{b_and},new IntVar[]{H[0]});
        */

        // OR
        B_OR B_ORR1 = new B_OR(null,new IntVar[]{V[1],V[2],H[1],H[2],H[0]});

        //
        B_OR B_OR1 = new B_OR(null,new IntVar[]{V[1],V[2]});
        B_OR B_OR2 = new B_OR(null,new IntVar[]{H[1],H[2]});
        B_AND b_and = new B_AND(new B_OR[]{B_OR1,B_OR2});
        B_OR B_ORR2 = new B_OR(new Branch[]{b_and},new IntVar[]{H[0]});

        SearchStatistics stats = search.solve(B_ORR1,statistics -> statistics.numberOfSolutions() == 1000);

        System.out.format("#Solutions: %s\n", stats.numberOfSolutions());
        System.out.format("Statistics: %s\n", stats);

        //System.identityHashCode(List[0]);

    }
}
