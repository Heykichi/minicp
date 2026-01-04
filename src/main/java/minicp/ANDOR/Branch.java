package minicp.ANDOR;

import minicp.engine.core.IntVar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Branch {

    private Set<IntVar> variables = null;
    private List<SubBranch> subBranches = null;

    public Branch(Set<IntVar> variables, List<SubBranch> subBranches) {
        this.variables = variables;
        this.subBranches = subBranches;
    }

    public Branch(IntVar[] varList, List<SubBranch> subBranches) {
        this.variables = new HashSet<>(Arrays.asList(varList));
        this.subBranches = subBranches;
    }

    public Branch(Set<IntVar> variables) {
        this.variables = variables;
    }

    public Branch(IntVar[] varList) {
        this.variables = new HashSet<>(Arrays.asList(varList));
    }

    public Branch(List<SubBranch> subBranches) {
        this.subBranches = subBranches;
    }

    public void setBranches(List<SubBranch> subBranches) {this.subBranches = subBranches;}

    public void setVariables(Set<IntVar> variables) {this.variables = variables;}

    public Set<IntVar> getVariables() {return this.variables;}

    public List<SubBranch> getBranches() {return this.subBranches;}
}