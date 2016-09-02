package org.snt.prex.etree;


import org.snt.prex.egraph.AutomatonEdge;

/**
 * Created by julian on 12/03/16.
 */
public class EditEdge {

    private EditNode src;
    private EditNode dst;
    private int cost;
    private AutomatonEdge ae;
    private AutomatonEdge.EdgeKind kind;

    public EditEdge(EditNode src, EditNode dst, int cost, AutomatonEdge.EdgeKind kind, AutomatonEdge ae){
        this.src = src;
        this.dst = dst;
        this.cost = cost;
        this.ae = ae;
        this.kind = kind;
    }


    public EditNode getSource() {
        return this.src;
    }

    public EditNode getTarget() {
        return this.dst;
    }

    public int getCost() {
        return this.cost;
    }

    public AutomatonEdge.EdgeKind getKind() {
        return this.ae.getKind();
    }

    public AutomatonEdge getAutomatonEdge() {
        return this.ae;
    }
}

