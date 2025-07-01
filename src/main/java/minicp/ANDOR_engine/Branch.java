package minicp.ANDOR_engine;

import minicp.engine.core.IntVar;

public class Branch {

    private IntVar[] variables = null;
    private Branch[] branches = null;
    private boolean rebranching = false;

    public Branch(IntVar[] variables, Branch[] branches, boolean rebranching) {
        this.variables = variables;
        this.branches = branches;
        this.rebranching = rebranching;
    }

    public Branch(IntVar[] variables, Branch[] branches) {
        this.variables = variables;
        this.branches = branches;
    }

    public Branch(IntVar[] variables, boolean rebranching) {
        this.variables = variables;
        this.rebranching = rebranching;
    }
    public Branch(IntVar[] variables) {
        this.variables = variables;
    }

    public Branch(Branch[] branches, boolean rebranching) {
        this.branches = branches;
        this.rebranching = rebranching;
    }

    public void setBranches(Branch[] branches) {this.branches = branches;}

    public void setVariables(IntVar[] variables) {this.variables = variables;}

    public void setRebranching(boolean rebranching) {this.rebranching = rebranching;}

    public IntVar[] getVariables() {
        return this.variables;
    }

    public Branch[] getBranches() {return this.branches;}

    public boolean getRebranching() {return this.rebranching;}
}
