package minicp.ANDOR_test;

import minicp.ANDOR.AND_DFSearch_partial_solution;
import minicp.ANDOR.Branch;
import minicp.ANDOR.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class AND_PS_test2 {
    public static void main(String[] args) {

        Solver cp = Factory.makeANDSolver(false);
        int index = 2;
        IntVar[] X = Factory.makeIntVarArray(cp, index, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, index, 4);
        //IntVar[] L = Factory.makeIntVarArray(cp, index, index);



        IntVar Y = Factory.makeIntVar(cp,4);

        //Y.fix(2);

        cp.post(Factory.sum(X, Y));
        cp.post(Factory.sum(Z, Y));

        //System.out.println(cp.getGraph().toString());

        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, () -> {
            ConstraintGraph graph = cp.getGraph();

            List<Set<IntVar>> sub = graph.findIndependentSubgraphs();
            if (sub.size() > 1){

                Branch[] branches = new Branch[sub.size()];
                int a = 0;
                for (Set<IntVar> s : sub){
                    if (s.size() < 3){
                        IntVar[] v = s.toArray(new IntVar[0]);
                        System.out.println(s);
                        branches[a] = new Branch(v,false);
                        a ++;
                    }
                }
                // TODO CHANGE FALSE TO TRUE
                if (a > 0) return new Branch(branches,false);
            }

            ArrayList<IntVar> Variables = graph.getVariables();
            if (Variables.isEmpty()) return null;

            IntVar v = null;
            int connexion = -1;
            for (IntVar var : Variables){
                if (!var.isFixed() ) {
                    int c = graph.getUnfixedNeighbors(var).size();
                    if (c >= connexion){
                        v = var;
                        connexion = c;
                    }
                }
            }
            if (v == null) return null;
            return new Branch(new IntVar[]{v},true);
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
