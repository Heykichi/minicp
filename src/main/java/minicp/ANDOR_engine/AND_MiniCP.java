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
import minicp.engine.core.*;
import minicp.search.Objective;
import minicp.state.StateManager;
import minicp.util.Procedure;
import minicp.util.exception.InconsistencyException;

import java.util.*;


public class AND_MiniCP implements Solver {

    private Queue<Constraint> propagationQueue = new ArrayDeque<>();
    private List<Procedure> fixPointListeners = new LinkedList<>();

    private final StateManager sm;
    private ConstraintGraph graph;
    private int id = -1;


    public AND_MiniCP(StateManager sm) {
        this.sm = sm;
        this.graph = new ConstraintGraph(this);
    }

    @Override
    public StateManager getStateManager() {
        return sm;
    }

    public void schedule(Constraint c) {
        if (c.isActive() && !c.isScheduled()) {
            c.setScheduled(true);
            propagationQueue.add(c);
        }
    }

    @Override
    public void onFixPoint(Procedure listener) {
        fixPointListeners.add(listener);
    }

    private void notifyFixPoint() {
        fixPointListeners.forEach(s -> s.call());
    }

    @Override
    public void fixPoint() {
        try {
            notifyFixPoint();
            while (!propagationQueue.isEmpty()) {
                propagate(propagationQueue.remove());
            }
        } catch (InconsistencyException e) {
            // empty the queue and unset the scheduled status
            while (!propagationQueue.isEmpty())
                propagationQueue.remove().setScheduled(false);
            throw e;
        }
    }

    private void propagate(Constraint c) {
        c.setScheduled(false);
        if (c.isActive())
            c.propagate();
    }

    @Override
    public Objective minimize(IntVar x) {
        return new Minimize(x);
    }

    @Override
    public Objective maximize(IntVar x) {
        return minimize(Factory.minus(x));
    }

    @Override
    public void post(Constraint c) {
        post(c, true);
    }

    @Override
    public void post(Constraint c, boolean enforceFixPoint) {
        IntVar[] variables = c.getVars();
        if (variables != null && variables.length > 0) {
            this.graph.addEdge(variables);
        }
        c.post();
        if (enforceFixPoint) fixPoint();

    }

    @Override
    public void post(BoolVar b) {
        b.fix(true);
        fixPoint();
    }

    @Override
    public String toString() {
        return "MiniCP(" + sm + ")";
    }

    @Override
    public ConstraintGraph getGraphWithStart() {
        this.graph.newState();
        return this.graph;
    }

    @Override
    public ConstraintGraph getGraph() {
        return this.graph;
    }

    @Override
    public int getId() {
        id ++;
        return this.id;
    }
}
