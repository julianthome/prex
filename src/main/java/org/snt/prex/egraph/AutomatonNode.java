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

    public AutomatonNode(NodeKind kind, State s, int level) {
        this.s = s;
        this.level = level;
        this.id = sid++;
        this.kind = kind;
        this.color = Color.WHITE;
        this.d = 0;
        this.f = 0;
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

        AutomatonNode n = (AutomatonNode) o;

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