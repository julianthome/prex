/*
* prex - approximate regular expression matching
*
* Copyright 2016, Julian Thomé <julian.thome@uni.lu>
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
* the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence. You may
* obtain a copy of the Licence at:
*
* https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/

package org.snt.prex.egraph;


import dk.brics.automaton.Transition;

public class AutomatonEdge implements Cloneable {


    public enum EdgeKind {
        NORMAL,
        BACK,
        FWD,
        CROSS,
        TREE,

        SUBST,
        INS,
        DEL;

        public boolean isMatching() {
            return this == NORMAL || this == BACK || this == FWD || this == CROSS || this == TREE;
        }
    }


    private Transition trans;
    private AutomatonNode src;
    private AutomatonNode dest;
    private EdgeKind kind;
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public AutomatonEdge(EdgeKind kind, AutomatonNode src, AutomatonNode dest, Transition trans) {
        this.src = src;
        this.dest = dest;
        this.trans = trans;
        this.kind = kind;
    }

    public AutomatonEdge(AutomatonEdge e) {
        this(e.kind, e.getSource(), e.getTarget(),e.getTrans());
    }

    public AutomatonEdge(AutomatonNode v1, AutomatonNode v2, EdgeKind kind) {
        this.src = v1;
        this.dest = v2;
        this.kind = kind;
    }

    public AutomatonEdge(AutomatonNode v1, AutomatonNode v2) {
        this.src = v1;
        this.dest = v2;
    }

    public AutomatonNode getSource() {
        return this.src;
    }


    public AutomatonNode getTarget() {
        return this.dest;
    }

    public void setDest(AutomatonNode dest){
        this.dest = dest;
    }

    public void setSrc(AutomatonNode src) {
        this.src = src;
    }

    public void setKind(EdgeKind kind) {
        this.kind = kind;
    }

    public EdgeKind getKind() {
        return kind;
    }

    public void setTrans(Transition t) {
        this.trans = t;
    }
    public Transition getTrans() {
        return this.trans;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof AutomatonEdge)) {
            return false;
        }

        AutomatonEdge e = (AutomatonEdge)o;

        return e.getSource().equals(this.getSource()) && e.getTarget().equals(this.getTarget()) &&
                e.getKind() == this.getKind();

    }


    @Override
    public int hashCode() {
        int hc = kind.hashCode();
        hc = 37 * hc + (this.src != null ? src.hashCode() : 0);
        return 37 * hc + (this.dest != null ? dest.hashCode() : 0);
    }

    @Override
    public String toString() {

        if(trans == null) {
            return "E";
        }

        if(trans.getMax() > trans.getMin())
            return "(" + this.getLevel() + ")" + "[" + Character.toString(trans.getMin()) + '-' + Character.toString(trans.getMax()) + "]";

        else return "(" + this.getLevel() + ")" +  Character.toString(trans.getMin());
    }


    /**
     * Returns a shallow copy of the edge.
     */
    @Override
    public AutomatonEdge clone() {
        return new AutomatonEdge(this);
    }

}
