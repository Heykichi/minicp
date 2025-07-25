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

import static minicp.cp.Factory.equal;
import static minicp.cp.Factory.notEqual;

public class AND_BranchingScheme {

    public static <T, N extends Comparable<N>> T selectMin(T[] x, Predicate<T> p, Function<T, N> f) {
        T sel = null;
        for (T xi : x) {
            if (p.test(xi)) {
                sel = sel == null || f.apply(xi).compareTo(f.apply(sel)) < 0 ? xi : sel;
            }
        }
        return sel;
    }

    public static Function<IntVar[], Procedure[]> firstFail() {
        return (IntVar[] Variable) -> {
            if (Variable == null || Variable.length == 0){
                return new Procedure[0];
            }
            IntVar xs = selectMin(Variable,
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

    public static Function<IntVar[], Procedure[]> firstOrder() {
        return (IntVar[] Variable) -> {
            if (Variable == null || Variable.length == 0){
                return new Procedure[0];
            }
            int idx = -1; // index of the first variable that is not fixed
            for (int k = 0; k < Variable.length; k++)
                if (Variable[k].size() > 1) {
                    idx = k;
                    break;
                }
            if (idx == -1)
                return new Procedure[0];
            else {
                IntVar qi = Variable[idx];
                int v = qi.min();
                return new Procedure[]{
                        () -> qi.getSolver().post(Factory.equal(qi, v)),
                        () -> qi.getSolver().post(Factory.notEqual(qi, v))};
            }
        };
    }

    public static Supplier<Branch> BasicTreeBuilding(Solver cp){
        return () -> {
            ConstraintGraph graph = cp.getGraph();

            List<Set<IntVar>> subgraphs = graph.findIndependentSubgraphs();

            if (subgraphs != null & subgraphs.size() > 1 ){
                List<SubBranch> subBranches = new ArrayList<>();

                for (Set<IntVar> s : subgraphs){
                    IntVar[] v = s.toArray(new IntVar[0]);
                    graph.removeNode(v);
                    subBranches.add(new SubBranch(v,v.length <=5 ));

                }
                SubBranch[] b = subBranches.toArray(new SubBranch[0]);
                return new Branch(b);
            }

            ArrayList<IntVar> Variables = graph.getUnfixedVariables();
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
            return new Branch(new IntVar[]{v});
        };
    }


}
