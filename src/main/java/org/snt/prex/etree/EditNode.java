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
    }


    private static int eid = 0;

    private int id = 0;
    private char c;
    private int pos = 0;
    private double sum = 0;
    private double steps = 0;
    private State state;


    private AutomatonNode nptr;

    public EditNode(AutomatonNode nptr, char c, int pos, double steps) {
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

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
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

    public double getSteps() {
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

