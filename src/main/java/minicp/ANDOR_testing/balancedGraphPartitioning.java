package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.engine.core.IntVar;

import java.util.*;

/**
 * Utility class to find "cut vertices" for balanced graph partitioning
 * in a ConstraintGraph using unweighted, undirected edges.
 *
 * Requires: ConstraintGraph, IntVar from MiniCP.
 */
public class balancedGraphPartitioning {


    // Fiduccia–Mattheyses algorithm
    public static Set<IntVar> fiducciaMattheysesCut(ConstraintGraph graph) {
        Set<IntVar> nodes = new HashSet<>(graph.getUnfixedVariables());
        List<IntVar> nodeList = new ArrayList<>(nodes);

        // 1. Initial balanced partition
        Set<IntVar> partA = new HashSet<>();
        Set<IntVar> partB = new HashSet<>();
        for (int i = 0; i < nodeList.size(); i++) {
            if (i < nodeList.size() / 2) partA.add(nodeList.get(i));
            else partB.add(nodeList.get(i));
        }

        boolean improvement = true;
        while (improvement) {
            improvement = false;
            // Calculate gain for each node
            Map<IntVar, Integer> gainA = new HashMap<>();
            Map<IntVar, Integer> gainB = new HashMap<>();
            for (IntVar node : partA) {
                int ext = 0, inter = 0;
                for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                    if (partB.contains(neighbor)) ext++;
                    else if (partA.contains(neighbor)) inter++;
                }
                gainA.put(node, ext - inter);
            }
            for (IntVar node : partB) {
                int ext = 0, inter = 0;
                for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                    if (partA.contains(neighbor)) ext++;
                    else if (partB.contains(neighbor)) inter++;
                }
                gainB.put(node, ext - inter);
            }
            // Find max gain node in A and B
            IntVar moveA = gainA.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            IntVar moveB = gainB.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

            // Move node with highest gain that maintains balance
            if (moveA != null && (partA.size() > partB.size() || partA.size() == partB.size())) {
                int gain = gainA.get(moveA);
                if (gain > 0) {
                    partA.remove(moveA);
                    partB.add(moveA);
                    improvement = true;
                }
            }
            if (!improvement && moveB != null && (partB.size() > partA.size() || partA.size() == partB.size())) {
                int gain = gainB.get(moveB);
                if (gain > 0) {
                    partB.remove(moveB);
                    partA.add(moveB);
                    improvement = true;
                }
            }
        }
        // Nodes to cut = nodes with edges crossing partitions
        Set<IntVar> cutNodes = new HashSet<>();
        for (IntVar node : nodes) {
            for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                if ((partA.contains(node) && partB.contains(neighbor)) ||
                        (partB.contains(node) && partA.contains(neighbor))) {
                    cutNodes.add(node);
                    break;
                }
            }
        }
        return cutNodes;
    }
}
