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

import org.jgrapht.EdgeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutomatonGraph extends AbstractGraph implements Cloneable {

    final static Logger logger = LoggerFactory.getLogger(AutomatonGraph.class);


    protected EdgeFactory<AutomatonNode,AutomatonEdge> SDGFactory;

    private AutomatonNode start = null;

    public AutomatonNode getStart() {
        return start;
    }

    public void setStart(AutomatonNode start) {
        this.start = start;
    }


    public Set<AutomatonEdge> getIncomingEdgesOfKind(AutomatonNode n, AutomatonEdge.EdgeKind k) {

        Set<AutomatonEdge> ret = new HashSet<AutomatonEdge>();

        if(this.incomingEdgesOf(n) != null) {
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

        if(this.outgoingEdgesOf(n) != null) {
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

        for(AutomatonNode n : vertexSet()) {
            if(n.getKind() == AutomatonNode.NodeKind.ACCEPT) {
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

        super.addEdge(src,dst, e);

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


        for (AutomatonEdge e : this.edgeSet())  {

            AutomatonNode src = e.getSource();
            AutomatonNode dst = e.getTarget();

            String label = "";
            switch(e.getKind()){

                case NORMAL:
                    label = " [label=\"" + e.toString() + "\",color=black];\n";
                    break;
                case BACK:
                    label = " [label=\"" + e.toString() + "\",color=red];\n";
                    break;
                case FWD:
                    label = " [label=\"" + e.toString() + "\",color=blue];\n";
                    break;
                case CROSS:
                    label = " [label=\"" + e.toString() + "\",color=orange];\n";
                    break;
                case TREE:
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
        for(AutomatonEdge e : edges) {
            addEdge(e);
        }
    }

    public void addAllVertices(Collection<AutomatonNode> nodes) {
        for(AutomatonNode n : nodes) {
            addVertex(n);
        }
    }

    @Override
    public EdgeFactory<AutomatonNode,AutomatonEdge> getEdgeFactory() {
        if (SDGFactory == null)
            SDGFactory = new EdgeFact();
        return SDGFactory;
    }

    /**
     * Replaces the edge factory.
     */
    public void setEdgeFactory(EdgeFactory<AutomatonNode,AutomatonEdge> fac) {
        SDGFactory = fac;
    }



    public AutomatonGraph subgraph(Collection<AutomatonNode> vertices) {
        AutomatonGraph g = new AutomatonGraph();

        for (AutomatonNode n : vertices) {
            g.addVertex(n);
        }

        for (AutomatonNode n : vertices) {
            for (AutomatonEdge e : outgoingEdgesOf(n)) {
                if (vertices.contains(e.getTarget())) {
                    g.addEdge(e);
                }
            }
        }

        return g;
    }

    @Override
    public AutomatonGraph clone() {
        AutomatonGraph g = new AutomatonGraph();

        Map<Integer, AutomatonNode> nlookup = new HashMap<Integer,AutomatonNode>();

        for (AutomatonNode n : this.vertexSet()) {
            AutomatonNode ncp = n.clone();

            //logger.info("put " + n.getId());
            nlookup.put(n.getId(),ncp);
            g.addVertex(ncp);


            if(this.getStart().equals(n)) {
                //g.addVertex(ncp);
                g.setStart(ncp);
            }

        }

        for (AutomatonEdge e : this.edgeSet()) {
            AutomatonEdge ecp = e.clone();


            AutomatonNode src = nlookup.get(e.getSource().getId());
            AutomatonNode dst = nlookup.get(e.getTarget().getId());

            //logger.info(ecp.toString());


            assert(src != null);
            assert(dst != null);

            ecp.setSrc(src);
            ecp.setDest(dst);

            g.addEdge(src,dst, ecp);
        }

        return g;
    }


}
