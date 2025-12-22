package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.util.io.InputReader;

import java.util.*;

import static minicp.ANDOR_testing.FiducciaMattheysesCut.fiducciaMattheysesCut;


/** Undirected, unweighted graph — iterative Tarjan articulation-point splitter. */
public class separatorTestFrance {
    /* ===== tiny sanity demo ===== */
    public static void main(String[] args) {

        InputReader reader1 = new InputReader("data/graph_coloring/france/departments.txt");

        Map<String, String> names = new HashMap<>();
        try {
            while (true) {
                String name = reader1.getString();
                String code = reader1.getString();
                names.put(code, name);
            }
        } catch (RuntimeException e) {}

        List<String> index = new ArrayList<>(names.keySet());

        Solver cp = Factory.makeANDSolver(false);
        IntVar[] vars = Factory.makeIntVarArray(cp, names.size(), 4);


        InputReader reader2 = new InputReader("data/graph_coloring/france/neighbors.txt");
        try {
            while (true) {
                String input = reader2.getString();
                String[] parts = input.split(":");
                String[] neighbors = parts[1].split(",");
                for (String neighbor : neighbors) {
                    cp.post(Factory.allDifferent(new IntVar[]{vars[index.indexOf(parts[0])], vars[index.indexOf(neighbor)]}));
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

//        Set<IntVar>[] cut = findBalancedSeparator(graph);

        Set<IntVar> cut = fiducciaMattheysesCut(graph);

        System.out.println(cut.size());

        for (IntVar v : cut) {
            System.out.println(v.getId());
        }

        graph.removeNode(cut);
        List<Set<IntVar>> end = new ArrayList<>();
        end.add(cut);
        end.addAll(graph.findConnectedComponents());

        System.out.println("=====");
        int color = 1;
        for (Set<IntVar> set : end) {
            for (IntVar v : set) {
                System.out.println(names.get(index.get(v.getId())) + " " + color);
            }
            color++;
        }

        System.out.println(end.size());
    }
}
