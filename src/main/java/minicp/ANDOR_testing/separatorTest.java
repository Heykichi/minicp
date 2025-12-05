package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.util.io.InputReader;
import minicp.ANDOR_testing.FiducciaMattheysesCut;


import java.util.*;

import static minicp.ANDOR_testing.FiducciaMattheysesCut.fiducciaMattheysesCut;
import static minicp.ANDOR_testing.GreedyPartitioning.findBalancedSeparator;


/** Undirected, unweighted graph — iterative Tarjan articulation-point splitter. */
public class separatorTest {
    /* ===== tiny sanity demo ===== */
    public static void main(String[] args) {

        InputReader reader1 = new InputReader("data/graph_coloring/world/countries.txt");

        Map<Integer, String> countriesNames = new HashMap<>();
        try {
            while (true) {
                String name = reader1.getString();
                int code = reader1.getInt();
                countriesNames.put(code, name);
            }
        } catch (RuntimeException e) {}

        Solver cp = Factory.makeANDSolver(false);
        IntVar[] countriesVars = Factory.makeIntVarArray(cp, countriesNames.size(), 4);

        InputReader reader2 = new InputReader("data/graph_coloring/world/countries_neighbor.txt");

        try {
            while (true) {
                Integer[] neighbor = reader2.getIntLine();
                for (int k = 1; k < neighbor.length; k++) {
                    cp.post(Factory.allDifferent(new IntVar[]{countriesVars[neighbor[0]], countriesVars[neighbor[k]]}));
                }
            }
        } catch (RuntimeException e) {}

        ConstraintGraph graph = cp.getGraphWithStart();
        Set<IntVar> variables = graph.getUnfixedVariables();
        for (IntVar v : variables) {
            if (graph.getUnfixedNeighbors(v).isEmpty()) {
                graph.removeNode(v);
            }
        }
        variables = graph.getUnfixedVariables();

//        Set<IntVar>[] cut = findBalancedSeparator(graph);
        Set<IntVar> cut = fiducciaMattheysesCut(graph);


        graph.removeNode(cut);
        List<Set<IntVar>> end = new ArrayList<>();
        end.add(cut);
        end.addAll(graph.findConnectedComponents());

        System.out.println("=====");
        int color = 1;
        for (Set<IntVar> set : end) {
            for (IntVar v : set) {
                System.out.println(countriesNames.get(v.getId()) + " " + color);
            }
            color++;
        }

        System.out.println(end.size());
    }
}
