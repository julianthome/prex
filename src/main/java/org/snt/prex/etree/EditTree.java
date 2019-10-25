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

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.egraph.AutomatonNode;

import java.util.LinkedList;


public class EditTree extends DirectedAcyclicGraph<EditNode, EditEdge> {

    final static Logger logger = LoggerFactory.getLogger(EditTree.class);

    public EditTree() {
        super(EditEdge.class);
    }

    public LinkedList<EditNode> getLeafNodes() {

        LinkedList<EditNode> leafs = new LinkedList<EditNode>();

        for (EditNode n : this.vertexSet()) {
            if (this.outDegreeOf(n) == 0) {
                leafs.add(n);
            }

        }

        return leafs;
    }

    public LinkedList<EditNode> getExtendableLeafNodes() {

        LinkedList<EditNode> leafs = new LinkedList<EditNode>();

        for (EditNode n : this.vertexSet()) {
            if (this.outDegreeOf(n) == 0 && n.isExtendable()) {
                leafs.add(n);
            }

        }

        return leafs;
    }

    public boolean isInExtendedList(EditNode n) {

        for (EditNode v : this.vertexSet()) {

            if (v.equals(n))
                continue;

            if (v.getPos() == n.getPos() && n.getAutomatonNode().equals(v.getAutomatonNode())
                    && this.outDegreeOf(v) > 0)
                return true;
        }

        return false;
    }

    public LinkedList<EditNode> getMinLeafs(double globalMin) {

        assert (!this.vertexSet().isEmpty());

        LinkedList<EditNode> ret = new LinkedList<EditNode>();

        LinkedList<EditNode> leafs = getExtendableLeafNodes();

        if (leafs == null || leafs.size() == 0) {
            return ret;
        }

        double mincost = leafs.iterator().next().getSum();

        for (EditNode n : leafs) {

            //logger.info("SUM " + n.getSum());
            mincost = Math.min(n.getSum(), mincost);

            if (globalMin >= 0) {
                mincost = Math.min(globalMin, mincost);
            }
        }

        //logger.info("MINCOST " + mincost);

        for (EditNode n : leafs) {

            //logger.info("SUM " + n.getSum() + " < " + mincost);

            if (n.getSum() <= mincost) {
                //logger.info("ADD " + n.toString());
                ret.add(n);
            }
        }

        return ret;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\tdigraph {\n");

        for (EditNode n : this.vertexSet()) {
            String shape = "";
            String color = "";

            if (!n.isExtendable()) {
                color = "gray";
            }

            if (n.getAutomatonNode().getKind() == AutomatonNode.NodeKind.ACCEPT) {
                color = "green";
            }

            sb.append("\t" + n.getName() + " [label=\"" + n.getLabel() + "\\npos:" + n.getPos() +
                    "\\nsum:" + n.getSum() + "\\nsteps:" + n.getSteps() + "\\nstate:" +
                    n.getAutomatonNode().getId() + "\\nid:" + n.getId() + "\\n\",shape=\"" +
                    shape + "\", color=\"" + color + "\"];\n");
        }

        for (EditEdge e : this.edgeSet()) {

            EditNode src = e.getSource();
            EditNode dst = e.getTarget();

            String ext = "";
            if (e.getKind().isMatching())
                ext = "MATCHING";
            else
                ext = "" + e.getKind();

            sb.append("\t" + src.getName() + " -> " + dst.getName() +
                    "[label=\"" + e.getCost() + "," +
                    ext + "\"];\n");

        }
        sb.append("}");

        return sb.toString();
    }
}
