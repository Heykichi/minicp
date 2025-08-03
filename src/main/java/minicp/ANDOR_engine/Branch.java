package minicp.ANDOR_engine;

import minicp.engine.core.IntVar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Branch {

    private Set<IntVar> variables = null;
    private SubBranch[] subBranches = null;

    public Branch(Set<IntVar> variables, SubBranch[] subBranches) {
        this.variables = variables;
        this.subBranches = subBranches;
    }

    public Branch(IntVar[] varList, SubBranch[] subBranches) {
        this.variables = new HashSet<>(Arrays.asList(varList));
        this.subBranches = subBranches;
    }

    public Branch(Set<IntVar> variables) {
        this.variables = variables;
    }

    public Branch(IntVar[] varList) {
        this.variables = new HashSet<>(Arrays.asList(varList));
    }

    public Branch(SubBranch[] subBranches) {
        this.subBranches = subBranches;
    }

    public void setBranches(SubBranch[] subBranches) {this.subBranches = subBranches;}

    public void setVariables(Set<IntVar> variables) {this.variables = variables;}

    public Set<IntVar> getVariables() {return this.variables;}

    public SubBranch[] getBranches() {return this.subBranches;}
}