package minicp.ANDOR_engine;

import minicp.engine.core.IntVar;

public class Branch {

    private IntVar[] variables = null;
    private SubBranch[] subBranches = null;

    public Branch(IntVar[] variables, SubBranch[] subBranches) {
        this.variables = variables;
        this.subBranches = subBranches;
    }

    public Branch(IntVar[] variables) {
        this.variables = variables;
    }

    public Branch(SubBranch[] subBranches) {
        this.subBranches = subBranches;
    }

    public void setBranches(SubBranch[] subBranches) {this.subBranches = subBranches;}

    public void setVariables(IntVar[] variables) {this.variables = variables;}

    public IntVar[] getVariables() {return this.variables;}

    public SubBranch[] getBranches() {return this.subBranches;}
}