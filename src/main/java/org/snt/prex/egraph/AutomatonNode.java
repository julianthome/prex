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

import dk.brics.automaton.State;

public class AutomatonNode implements Cloneable {


    public enum NodeKind {
        ACCEPT,
        NORMAL,
        PARTITION
    }


    public enum Color {
        WHITE,
        GRAY,
        BLACK
    }


    private State s;
    private int id;
    private static int sid = 1;
    private Color color;

    private int d;
    private int f;

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private NodeKind kind;


    private int level;
    private int barrier;

    public int getBarrier() {
        return barrier;
    }

    public void setBarrier(int barrier) {
        this.barrier = barrier;
    }

    public AutomatonNode(NodeKind kind, State s, int level) {
        this.s = s;
        this.level = level;
        this.id = sid ++;
        this.kind = kind;
        this.color = Color.WHITE;
        this.d = 0;
        this.f = 0;
        this.barrier = 0;

    }

    public State getState() {
        return s;
    }

    public void setState(State s) {
        this.s = s;
    }


    public void setId(int id) {
        this.id = id;
    }



    public void setKind(NodeKind kind) {
        this.kind = kind;
    }


    public AutomatonNode(AutomatonNode o) {
        this(o.kind, o.s, o.level);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevels() {
        return this.level;
    }


    public int getId() {
        return this.id;
    }

    public String getName() {
        return "n" + this.id;
    }

    public NodeKind getKind() {
        return this.kind;
    }


    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AutomatonNode))
            return false;

        AutomatonNode n = (AutomatonNode)o;

        return this.id == n.id;
    }

    @Override
    public String toString() {
        return "id: " + this.id + "(" + this.level + ")";
    }


    public AutomatonNode clone() {
        return new AutomatonNode(this);
    }
}