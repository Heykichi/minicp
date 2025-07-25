package minicp.ANDOR_engine;

import minicp.engine.core.IntVar;

public class SubBranch {
    private IntVar[] variables;
    private boolean toFix = false;

    public SubBranch(IntVar[] variables, boolean toFix) {
        this.variables = variables;
        this.toFix = toFix;
    }

    public SubBranch(IntVar[] variables) {
        this.variables = variables;
    }

    public IntVar[] getVariables() {return this.variables;}

    public boolean getToFix() {return this.toFix;}
}