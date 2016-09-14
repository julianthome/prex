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

