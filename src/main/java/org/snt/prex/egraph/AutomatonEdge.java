/**
 * prex - approximate regular expression matching
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package org.snt.prex.egraph;

import dk.brics.automaton.Transition;

public class AutomatonEdge implements Cloneable {

    public enum EdgeKind {
        SUBST,
        INS,
        DEL,
        MATCH;

        public boolean isMatching() {
            return this == MATCH;
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
        this(e.kind, e.getSource(), e.getTarget(), e.getTrans());
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

    public void setDest(AutomatonNode dest) {
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

        if (!(o instanceof AutomatonEdge)) {
            return false;
        }

        AutomatonEdge e = (AutomatonEdge) o;

        return e.getSource().equals(this.getSource()) && e.getTarget().equals(this.getTarget()) &&
                e.getKind() == this.getKind();

    }

    @Override
    public String toString() {

        if (trans == null) {
            return "E";
        }

        if (trans.getMax() > trans.getMin())
            return "(" + this.getLevel() + ")" + "[" + trans.toString() + "]";

        else
            return "(" + this.getLevel() + ")" + Character.toString(trans.getMin());
    }

    @Override
    public AutomatonEdge clone() {
        return new AutomatonEdge(this);
    }

}
