package minicp.ANDOR_example;

import minicp.ANDOR_engine.AND_DFSearch_partial_solution;
import minicp.ANDOR_engine.Branch;
import minicp.ANDOR_engine.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.search.SearchStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class AND_PS_example1 {
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
        AND_DFSearch_partial_solution search = Factory.makeAND_Dfs_PS(cp, () -> {
            ConstraintGraph graph = cp.getGraph();

            List<Set<IntVar>> sub = graph.findIndependentSubgraphs();

            if (sub != null & sub.size() > 1 ){
                // AND branch for the connected component of size <= 3
                List<Branch> branches = new ArrayList<>();
                int a = 0;
                for (Set<IntVar> s : sub){
                    if (s.size() <= 5){
                        IntVar[] v = s.toArray(new IntVar[0]);
                        graph.removeNode(v);
                        branches.add(new Branch(v,false));
                        a ++;
                    }
                }
                if (a > 0) return new Branch(branches.toArray(new Branch[0]),true);
            }

            ArrayList<IntVar> Variables = graph.getVariables();
            if (Variables.isEmpty()) return null;
            // OR -> variable with the most connections
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

        search.onSolution(() -> {
            System.out.print("1) ");
            printSum(X,Y);

            System.out.print("2) ");
            printSum(Z,Y);
            System.out.println();
        });


        SearchStatistics stats = search.solve(statistics -> statistics.numberOfSolutions() == 1000);
        System.out.println("=======================================================================");
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
