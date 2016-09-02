package org.snt.prex.egraph;

import org.jgrapht.EdgeFactory;

public class EdgeFact implements EdgeFactory<AutomatonNode,AutomatonEdge> {

    public AutomatonEdge createEdge(AutomatonNode v1, AutomatonNode v2) {
        return new AutomatonEdge( v1, v2, AutomatonEdge.EdgeKind.NORMAL);
    }
}