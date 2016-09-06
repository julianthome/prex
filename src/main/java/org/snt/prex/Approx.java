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

package org.snt.prex;

import dk.brics.automaton.Automaton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.egraph.AutomatonEdge;
import org.snt.prex.egraph.AutomatonGraph;
import org.snt.prex.egraph.AutomatonNode;
import org.snt.prex.egraph.GraphGenerator;
import org.snt.prex.etree.EditEdge;
import org.snt.prex.etree.EditNode;
import org.snt.prex.etree.EditTree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


public class Approx {

    final static Logger logger = LoggerFactory.getLogger(Approx.class);

    private AutomatonGraph eg = null;
    private Automaton a = null;

    private int step = 0;

    private HashSet<EditNode> extendedList = null;

    public Approx(Automaton a) {
        super();

        Automaton ch = a;

        AutomatonGraph g = GraphGenerator.getInstance().buildGraphFromAutomaton(ch);
        this.eg = GraphGenerator.getInstance().buildEditGraph(g);
        this.extendedList = new HashSet<EditNode>();
        this.a = a;
        //logger.info("EG " + this.eg.toString());
    }

    public void extendsNode(EditTree et, String s, EditNode ancestor) {
        //logger.info("ANCESTPOR " + ancestor.toString());


        boolean lengthok = false;
        char tocheck = '%';

        //logger.info("NXTPOS " + nxtPos);
        if (ancestor.getPos() < s.length()) {
            //logger.info("LENOK");
            lengthok = true;
        } else {
            //logger.info("LENNOK");
            lengthok = false;
        }

        Set<AutomatonEdge> out = null;

        if(!lengthok) {
            out = this.eg.getOutgoingEdgesOfKind(ancestor.getAutomatonNode(), AutomatonEdge.EdgeKind.INS);

            if(out == null || out.size() == 0) {
                ancestor.setState(EditNode.State.NOT_EXTENDABLE);
            }
        } else {
            tocheck = s.charAt(ancestor.getPos());
            out = this.eg.outgoingEdgesOf(ancestor.getAutomatonNode());
        }


        for (AutomatonEdge e : out) {
            //logger.info("EDGE " + e.toString());
            int apos = 0;
            int cost = 1;


            //if(ancestor.getPos() == 0 && e.getKind() == AutomatonEdge.EdgeKind.SUBST) {
            //    continue;
           // }


            if (e.getKind() == AutomatonEdge.EdgeKind.INS) {
                apos = ancestor.getPos();
            } else {
                apos = ancestor.getPos() + 1;
            }

            if (e.getKind().isMatching()) {
                // NON-Match
                if (!(e.getTrans().getMin() <= tocheck && e.getTrans().getMax() >= tocheck)) {
                    //logger.info("TOCHECK " + tocheck + " vs " + e.getTrans().getMin());
                    continue;
                } else {
                    cost = 0;
                }
            }

            this.step = Math.max(step, ancestor.getSteps() + 1);

            EditNode en = new EditNode(e.getTarget(), tocheck, apos, ancestor.getSteps() + 1);
            EditEdge ee = new EditEdge(ancestor, en, cost, e.getKind(), e);

            et.addVertex(en);
            et.addEdge(ancestor, en, ee);
            en.setSum(ancestor.getSum() + cost);

        }

    }

    public double evaluateCost(String s) {

        this.step = 0;
        EditTree et = new EditTree();

        EditNode start = new EditNode(this.eg.getStart(), 'S', 0, 0);

        et.addVertex(start);

        // heuristic to initialize global minimum
        int globalMin = Math.max(this.eg.vertexSet().size() + 1, s.length());


        //logger.info("GLOBAL MIN " + globalMin);


        LinkedList<EditNode> leafs = et.getExtendableLeafNodes();


        while (!leafs.isEmpty()) {


            EditNode ancestor = leafs.pop();

            if (ancestor.getAutomatonNode().getKind() == AutomatonNode.NodeKind.ACCEPT &&
                    ancestor.getPos() > s.length() - 1) {
                globalMin = globalMin < 0 ? ancestor.getSum() : Math.min(globalMin, ancestor.getSum());
            }


            if (et.isInExtendedList(ancestor) && !leafs.isEmpty()) {
                ancestor.setState(EditNode.State.NOT_EXTENDABLE);
            }

            if (ancestor.getSum() >= globalMin) {
                ancestor.setState(EditNode.State.NOT_EXTENDABLE);
            }

            if (ancestor.isExtendable()) {
                extendsNode(et,s,ancestor);
            }

            leafs = et.getMinLeafs(globalMin);
        }


        //logger.info("MINLEAFS " + leafs);
        //logger.info(et.toString());

        //logger.info("GLOBAL MIN " + globalMin);

        //logger.info("MMIN " + et.getMinLeafs(globalMin));

        double cost;
        if(this.step != 0.0)
            cost = (double)globalMin/(double)this.step;
        else
            cost = 1.0;


        logger.info("COST " + cost);

        return cost;
    }


    public int getStep() {
        return this.step;
    }



}
