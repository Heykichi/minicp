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

/**
 * Absolute value constraint
 */
public class Absolute extends AbstractConstraint {

    private final IntVar x;
    private final IntVar y;

    /**
     * Creates the absolute value constraint {@code y = |x|}.
     *
     * @param x the input variable such that its absolut value is equal to y
     * @param y the variable that represents the absolute value of x
     */
    public Absolute(IntVar x, IntVar y) {
        super(x.getSolver());
        this.x = x;
        this.y = y;
    }

    public void post() {
        if (y.isFixed()) {
            if (x.contains(y.min()) && x.contains(-y.min())) {
                propagate();
            } else if (x.contains(y.min())) {
                x.fix(y.min());
            } else if (x.contains(-y.min())) {
                x.fix(-y.min());
            }
        } else if (x.isFixed()) {
            y.fix(Math.abs(x.min()));
        } else {
            propagate();
            int[] domVal = new int[Math.max(x.size(), y.size())];
            x.whenDomainChange(() -> {
                boundsIntersect();
                int nVal = y.fillArray(domVal);
                for (int k = 0; k < nVal; k++)
                    if (!(x.contains(domVal[k]) || x.contains(-domVal[k])))
                        y.remove(domVal[k]);
            });
            y.whenDomainChange(() -> {
                boundsIntersect();
                int nVal = x.fillArray(domVal);
                for (int k = 0; k < nVal; k++)
                    if (!y.contains(Math.abs(domVal[k])))
                        x.remove(domVal[k]);

            });
        }
    }

    @Override
    public void propagate() {
        y.removeBelow(0);
        boundsIntersect();
        int[] domVal = new int[Math.max(x.size(), y.size())];
        int nVal = x.fillArray(domVal);
        for (int k = 0; k < nVal; k++)
            if (!y.contains(Math.abs(domVal[k])))
                x.remove(domVal[k]);
        nVal = y.fillArray(domVal);
        for (int k = 0; k < nVal; k++) {
            System.out.println(domVal[k]);
            if (!(x.contains(domVal[k]) || x.contains(-domVal[k])))
                y.remove(domVal[k]);
        }
    }

    private void boundsIntersect() {
        x.removeBelow(-y.max());
        x.removeAbove(y.max());
        if (x.max() < 0) {
            y.removeBelow(Math.abs(x.max()));
            y.removeAbove(Math.abs(x.min()));
        }else {
            y.removeAbove(Math.max(Math.abs(x.min()), x.max()));
            y.removeBelow(Math.min(x.min(), x.max()));
        }
    }

    @Override
    public IntVar[] getVars() {
        return new IntVar[]{x, y};
    }
}
