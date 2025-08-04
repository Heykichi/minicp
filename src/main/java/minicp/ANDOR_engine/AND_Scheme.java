/*
 * mini-cp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License  v3
 * as published by the Free Software Foundation.
 *
 * mini-cp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY.
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with mini-cp. If not, see http://www.gnu.org/licenses/lgpl-3.0.en.html
 *
 * Copyright (c)  2018. by Laurent Michel, Pierre Schaus, Pascal Van Hentenryck
 */

package minicp.ANDOR_engine;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.util.Procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static minicp.ANDOR_testing.GreedyPartitioning.findBalancedSeparator;
import static minicp.ANDOR_testing.balancedGraphPartitioning.fiducciaMattheysesCut;
import static minicp.cp.Factory.equal;
import static minicp.cp.Factory.notEqual;

public class AND_Scheme {

    public static <T, N extends Comparable<N>> T selectMin(T[] x, Predicate<T> p, Function<T, N> f) {
        T sel = null;
        for (T xi : x) {
            if (p.test(xi)) {
                sel = sel == null || f.apply(xi).compareTo(f.apply(sel)) < 0 ? xi : sel;
            }
        }
        return sel;
    }
    public static Function<Set<IntVar>, Procedure[]> firstFail() {
        return (Set<IntVar> Variables) -> {
            if (Variables == null || Variables.isEmpty()){
                return new Procedure[0];
            }
            IntVar xs = selectMin(Variables.toArray(new IntVar[0]),
                    xi -> xi.size() > 1,
                    xi -> xi.size());
            if (xs == null)
                return new Procedure[0];
            else {
                int v = xs.min();
                return new Procedure[]{
                        () -> xs.getSolver().post(equal(xs, v)),
                        () -> xs.getSolver().post(notEqual(xs, v))};
            }
        };
    }

    public static Function<Set<IntVar>, Procedure[]> firstOrder() {
        return (Set<IntVar> variables) -> {
            if (variables == null || variables.isEmpty()){
                return new Procedure[0];
            }
            IntVar[] varList = variables.toArray(new IntVar[0]);
            int idx = -1; // index of the first variable that is not fixed
            for (int k = 0; k < varList.length; k++)
                if (varList[k].size() > 1) {
                    idx = k;
                    break;
                }
            if (idx == -1)
                return new Procedure[0];
            else {
                IntVar qi = varList[idx];
                int v = qi.min();
                return new Procedure[]{
                        () -> qi.getSolver().post(Factory.equal(qi, v)),
                        () -> qi.getSolver().post(Factory.notEqual(qi, v))};
            }
        };
    }

    public static Supplier<Branch> naiveTreeBuilding(Solver cp, int nVars, int sizeToFix){
        return () -> {
            ConstraintGraph graph = cp.getGraphWithStart();
            List<SubBranch> subBranches = graph.splitGraph(sizeToFix);
            if (subBranches != null){
                return new Branch(subBranches);
            }

            Set<IntVar> variables = graph.getUnfixedVariables();
            if (variables.isEmpty()) return null;

            Set<IntVar> varSet = variables.stream()
                    .sorted((a, b) -> Integer.compare(graph.getUnfixedNeighbors(b).size(), graph.getUnfixedNeighbors(a).size()))
                    .limit(nVars)
                    .collect(Collectors.toSet());

            return new Branch(varSet);
        };
    }

    public static Supplier<Branch> naiveTreeBuilding2(Solver cp, int nVars, int sizeToFix){
        boolean[] firstCall = {true};
        return () -> {
            ConstraintGraph graph = cp.getGraphWithStart();
            Set<IntVar> variables = graph.getUnfixedVariables();
            if (variables.isEmpty()) return null;

            Set<IntVar> varSet = variables.stream()
                    .sorted((a, b) -> Integer.compare(graph.getUnfixedNeighbors(b).size(), graph.getUnfixedNeighbors(a).size()))
                    .limit(nVars)
                    .collect(Collectors.toSet());

            if (!varSet.isEmpty()) {
                graph.removeNode(varSet);
            }

            List<SubBranch> subBranches = graph.splitGraph(sizeToFix);

            return new Branch(varSet,subBranches);

        };
    }



    public static Supplier<Branch> greedyPartitioning(Solver cp, int sizeToFix){
        boolean[] firstCall = {true};
        return () -> {
            if (firstCall[0]) {
                firstCall[0] = false;
                ConstraintGraph graph = cp.getGraphWithStart();
                List<SubBranch> b = graph.splitGraph(sizeToFix);
                if (b != null) return new Branch(b);
            }
            ConstraintGraph graph = cp.getGraphWithStart();
            Set<IntVar> unFixedVars = graph.getUnfixedVariables();
            if (unFixedVars.isEmpty()) {
                return null;
            }
            if (unFixedVars.size() <= sizeToFix) {
                return new Branch(unFixedVars);
            }
            Set<IntVar>[] end = findBalancedSeparator(graph);
            SubBranch[] s = {new SubBranch(end[1]), new SubBranch(end[2])};

            List<SubBranch> subBranches = new ArrayList<>();
            if (!end[1].isEmpty()){
                subBranches.add(new SubBranch(end[1]));
            }
            if (!end[2].isEmpty()){
                subBranches.add(new SubBranch(end[2]));
            }
            if (subBranches.size() == 2){
                return new Branch(end[0], subBranches);
            }

            return new Branch(end[0]);

        };
    }

    public static Supplier<Branch> fiducciaMattheyses(Solver cp, int sizeToFix){
        boolean[] firstCall = {true};
        return () -> {
            if (firstCall[0]) {
                firstCall[0] = false;
                ConstraintGraph graph = cp.getGraphWithStart();
                List<SubBranch> b = graph.splitGraph(sizeToFix);
                if (b != null) return new Branch(b);
            }
            ConstraintGraph graph = cp.getGraphWithStart();
            Set<IntVar> unFixedVars = graph.getUnfixedVariables();
            if (unFixedVars.isEmpty()) {
                return null;
            }
            if (unFixedVars.size() <= sizeToFix) {
                return new Branch(unFixedVars);
            }

            Set<IntVar> cut = fiducciaMattheysesCut(graph);

            graph.removeNode(cut);
            List<Set<IntVar>> subSet = graph.findConnectedComponents();

            if (subSet.size() > 1) {
                List<SubBranch> subBranches = new ArrayList<>();
                for (Set<IntVar> s1 : subSet) {
                    subBranches.add(new SubBranch(s1,s1.size() <= sizeToFix));
                }
                return new Branch(cut, subBranches);
            }
            return new Branch(cut);

        };
    }
}
