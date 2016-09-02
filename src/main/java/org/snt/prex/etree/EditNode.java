package org.snt.prex.etree;

import org.snt.prex.egraph.AutomatonNode;

public class EditNode {

    public boolean isExtendable() {
        return this.state == State.EXTENDABLE;
    }

    public enum State {
        EXTENDABLE,
        NOT_EXTENDABLE,
        DUMMY
    };


    private static int eid = 0;

    private int id = 0;
    private char c;
    private int pos = 0;
    private int sum = 0;
    private int steps = 0;
    private State state;


    private AutomatonNode nptr;

    public EditNode(AutomatonNode nptr, char c, int pos, int steps) {
        this.id = eid++;
        this.c = c;
        this.nptr = nptr;
        this.pos = pos;
        this.sum = 0;
        this.steps = steps;
        this.state = State.EXTENDABLE;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getId() {
        return this.id;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getName() {
        return "n" + this.id;
    }

    public String getLabel() {
        return Character.toString(c);
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return this.steps;
    }

    public int getPos() {
        return this.pos;
    }

    public AutomatonNode getAutomatonNode() {
        return this.nptr;
    }

    @Override
    public String toString() {
        return "" + this.id;
    }

}

