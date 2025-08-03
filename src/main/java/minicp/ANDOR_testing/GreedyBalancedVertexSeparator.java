package minicp.ANDOR_testing;

import java.util.*;

public class GreedyBalancedVertexSeparator {

    // Main method for demo
    public static void main(String[] args) {
        // Example usage:
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(0, 2, 3));
        graph.put(2, Arrays.asList(0, 1, 3));
        graph.put(3, Arrays.asList(1, 2, 4, 5));
        graph.put(4, Arrays.asList(3, 5));
        graph.put(5, Arrays.asList(3, 4));
        graph.put(6, Arrays.asList(5, 4));
        graph.put(7, Arrays.asList(5, 6));
        graph.put(35, Arrays.asList(1, 3, 4));

        Result result = findBalancedSeparator(graph);

        System.out.println("Group A: " + result.groupA);
        System.out.println("Group B: " + result.groupB);
        System.out.println("Separator: " + result.separator);
    }

    public static class Result {
        Set<Integer> groupA;
        Set<Integer> groupB;
        Set<Integer> separator;

        public Result(Set<Integer> a, Set<Integer> b, Set<Integer> sep) {
            groupA = a;
            groupB = b;
            separator = sep;
        }
    }

    public static Result findBalancedSeparator(Map<Integer, List<Integer>> graph) {
        List<Integer> allNodes = new ArrayList<>(graph.keySet());
        Collections.sort(allNodes);

        // Partition into two roughly equal groups
        int n = allNodes.size();
        Set<Integer> groupA = new HashSet<>(allNodes.subList(0, n / 2));
        Set<Integer> groupB = new HashSet<>(allNodes.subList(n / 2, n));

        // Step 1: Find initial separator set (vertices that link both groups)
        Set<Integer> separator = new HashSet<>();
        for (int v : allNodes) {
            boolean inA = groupA.contains(v);
            boolean inB = groupB.contains(v);
            for (int nbr : graph.get(v)) {
                if ((inA && groupB.contains(nbr))) {
                    separator.add(v);
                    groupA.remove(v);
                    break;
                }
                if (inB && groupA.contains(nbr)){
                    separator.add(v);
                    groupB.remove(v);
                    break;
                }

            }
        }

        // Optional Step 2: Prune separator set (remove redundant separators)
        //separator = pruneSeparator(graph, groupA, groupB, separator);

        return new Result(groupA, groupB, separator);
    }

    private static Set<Integer> pruneSeparator(Map<Integer, List<Integer>> graph, Set<Integer> groupA, Set<Integer> groupB, Set<Integer> separator) {
        Set<Integer> minimalSeparator = new HashSet<>(separator);

        for (int v : separator) {
            Set<Integer> tempSeparator = new HashSet<>(minimalSeparator);
            tempSeparator.remove(v);

            if (!isConnectedAfterRemoval(graph, groupA, groupB, tempSeparator)) {
                // Removing v still disconnects A and B, so v is redundant
                minimalSeparator.remove(v);
            }
        }
        return minimalSeparator;
    }

    // Checks if any node in groupB is reachable from groupA after removing separator nodes
    private static boolean isConnectedAfterRemoval(Map<Integer, List<Integer>> graph, Set<Integer> groupA, Set<Integer> groupB, Set<Integer> separator) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        // Start BFS from any node in groupA not in separator
        Integer start = null;
        for (int v : groupA) {
            if (!separator.contains(v)) {
                start = v;
                break;
            }
        }
        if (start == null) return false; // No start point

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (int nbr : graph.get(curr)) {
                if (separator.contains(nbr) || visited.contains(nbr)) continue;
                visited.add(nbr);
                queue.add(nbr);
            }
        }

        // If any node in groupB is visited, A and B are still connected
        for (int v : groupB) {
            if (visited.contains(v)) return true;
        }
        return false;
    }
}
