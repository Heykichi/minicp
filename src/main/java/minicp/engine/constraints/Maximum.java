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


package minicp.engine.constraints;

import minicp.engine.core.AbstractConstraint;
import minicp.engine.core.IntVar;
import minicp.util.exception.NotImplementedException;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Maximum Constraint
 */
public class Maximum extends AbstractConstraint {

    private final IntVar[] x;
    private final IntVar y;

    /**
     * Creates the maximum constraint y = maximum(x[0],x[1],...,x[n])?
     *
     * @param x the variable on which the maximum is to be found
     * @param y the variable that is equal to the maximum on x
     */
    public Maximum(IntVar[] x, IntVar y) {
        super(x[0].getSolver());
        assert (x.length > 0);
        this.x = x;
        this.y = y;
    }


    @Override
    public void post() {
        // TODO
        //  - call the constraint on all bound changes for the variables (x.propagateOnBoundChange(this))
        //  - call a first time the propagate() method to trigger the propagation
        for (IntVar x1 : x){
            x1.propagateOnBoundChange(this);
        }
        y.propagateOnBoundChange(this);
        propagate();
    }


    @Override
    public void propagate() {
        // TODO
        //  - update the min and max values of each x[i] based on the bounds of y
        //  - update the min and max values of each y based on the bounds of all x[i]
        int max = Integer.MIN_VALUE;
        int min = Integer.MIN_VALUE;
        int n = 0;
        IntVar i = null;
        for (IntVar var : x){
            var.removeAbove(y.max());
            if (var.max() >= y.min() && var.max() <= y.max()) {
                n ++;
                i = var;
            }
            else if (var.min() <= y.max() && var.min() >= y.min()) {
                n ++;
                i = var;
            }
            else if (var.min() <= y.min() && var.max() >= y.max()) {
                n ++;
                i = var;
            }
            max = Math.max(max, var.max());
            min = Math.max(min, var.min());
        }
        y.removeBelow(min);
        y.removeAbove(max);
        if (n == 1){
            i.removeBelow(y.min());
        }
    }
    @Override
    public IntVar[] getVars() {
        return Stream.concat(Arrays.stream(x), Stream.of(y))
                .toArray(IntVar[]::new);
    }
}
