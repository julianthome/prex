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