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


import org.snt.prex.egraph.AutomatonEdge;

public class EditEdge {

    private EditNode src;
    private EditNode dst;
    private double cost;
    private AutomatonEdge ae;
    private AutomatonEdge.EdgeKind kind;

    public EditEdge(EditNode src, EditNode dst, double cost, AutomatonEdge.EdgeKind kind, AutomatonEdge ae) {
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

    public double getCost() {
        return this.cost;
    }

    public AutomatonEdge.EdgeKind getKind() {
        return this.ae.getKind();
    }

    public AutomatonEdge getAutomatonEdge() {
        return this.ae;
    }
}

