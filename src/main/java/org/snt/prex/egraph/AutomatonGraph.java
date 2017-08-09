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

import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutomatonGraph extends DirectedPseudograph<AutomatonNode, AutomatonEdge>
        implements
        Cloneable {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonGraph.class);

    private AutomatonNode start = null;

    public AutomatonGraph() {
        super(AutomatonEdge.class);
    }

    public AutomatonNode getStart() {
        return start;
    }

    public void setStart(AutomatonNode start) {
        this.start = start;
    }


    public Set<AutomatonEdge> getIncomingEdgesOfKind(AutomatonNode n, AutomatonEdge.EdgeKind k) {

        Set<AutomatonEdge> ret = new HashSet<AutomatonEdge>();

        if (this.incomingEdgesOf(n) != null) {
            for (AutomatonEdge e : this.outgoingEdgesOf(n)) {
                if (e.getKind() == k) {
                    ret.add(e);
                }
            }
        }

        return ret;
    }

    public Set<AutomatonEdge> getOutgoingEdgesOfKind(AutomatonNode n, AutomatonEdge.EdgeKind k) {

        Set<AutomatonEdge> ret = new HashSet<AutomatonEdge>();

        if (this.outgoingEdgesOf(n) != null) {
            for (AutomatonEdge e : this.outgoingEdgesOf(n)) {
                if (e.getKind() == k) {
                    ret.add(e);
                }
            }
        }

        return ret;
    }

    public Set<AutomatonNode> getAcceptingStates() {
        Set<AutomatonNode> ret = new HashSet<AutomatonNode>();

        for (AutomatonNode n : vertexSet()) {
            if (n.getKind() == AutomatonNode.NodeKind.ACCEPT) {
                ret.add(n);
            }
        }

        return ret;
    }

    public void addEdge(AutomatonEdge e) {


        AutomatonNode src = e.getSource();
        AutomatonNode dst = e.getTarget();

        super.addVertex(src);
        super.addVertex(dst);

        super.addEdge(src, dst, e);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\tdigraph {\n");


        for (AutomatonNode n : this.vertexSet()) {
            String shape = "";
            String color = "";

            if (n.equals(this.start)) {
                color = "green";
            } else if (n.getKind() == AutomatonNode.NodeKind.ACCEPT) {
                shape = "doublecircle";
            }

            sb.append("\t" + n.getName() + " [label=\"" + n.toString() + "\",shape=\"" + shape + "\", color=\"" + color + "\"];\n");
        }


        for (AutomatonEdge e : this.edgeSet()) {

            AutomatonNode src = e.getSource();
            AutomatonNode dst = e.getTarget();

            String label = "";
            switch (e.getKind()) {

                case MATCH:
                    label = " [label=\"" + e.toString() + "\",color=green];\n";
                    break;
                case SUBST:
                    label = " [label=\"S->" + e.toString() + "\",color=purple];\n";
                    break;
                case INS:
                    label = " [label=\"I->" + e.toString() + "\",color=khaki];\n";
                    break;
                case DEL:
                    label = " [label=\"D->" + e.toString() + "\",color=gray;]\n";
                    break;

            }
            sb.append("\t" + src.getName() + " -> " + dst.getName() + label);
        }


        sb.append("}\n");

        return sb.toString();


    }

    public void addAllEdges(Collection<AutomatonEdge> edges) {
        for (AutomatonEdge e : edges) {
            addEdge(e);
        }
    }

    @Override
    public AutomatonGraph clone() {
        AutomatonGraph g = new AutomatonGraph();

        Map<Integer, AutomatonNode> nlookup = new HashMap<Integer, AutomatonNode>();

        for (AutomatonNode n : this.vertexSet()) {
            AutomatonNode ncp = n.clone();

            //LOGGER.info("put " + n.getId());
            nlookup.put(n.getId(), ncp);
            g.addVertex(ncp);


            if (this.getStart().equals(n)) {
                //g.addVertex(ncp);
                g.setStart(ncp);
            }

        }

        for (AutomatonEdge e : this.edgeSet()) {
            AutomatonEdge ecp = e.clone();


            AutomatonNode src = nlookup.get(e.getSource().getId());
            AutomatonNode dst = nlookup.get(e.getTarget().getId());

            //LOGGER.info(ecp.toString());


            assert (src != null);
            assert (dst != null);

            ecp.setSrc(src);
            ecp.setDest(dst);

            g.addEdge(src, dst, ecp);
        }

        return g;
    }


}
