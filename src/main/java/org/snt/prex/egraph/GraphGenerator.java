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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GraphGenerator {


    final static Logger LOGGER = LoggerFactory.getLogger(GraphGenerator.class);

    private Map<State, AutomatonNode> nlookup = null;
    private int timer;
    private AutomatonGraph g;
    private static GraphGenerator gen = null;

    private Set<Set<AutomatonNode>> components = null;
    private Map<AutomatonNode, Integer> low = null;
    private Stack<AutomatonNode> nstack = null;


    public static GraphGenerator getInstance() {
        if (gen == null)
            gen = new GraphGenerator();
        return gen;
    }

    private GraphGenerator() {
    }


    public AutomatonGraph buildEditGraph(AutomatonGraph g) {

        AutomatonGraph cp = g.clone();

        //LOGGER.info("HH " + cp.toString());

        for (AutomatonNode v : cp.vertexSet()) {

            Set<AutomatonEdge> toadd = new HashSet<AutomatonEdge>();

            AutomatonEdge del = new AutomatonEdge(AutomatonEdge.EdgeKind.DEL, v, v, null);
            //AutomatonEdge ins = new AutomatonEdge(AutomatonEdge.EdgeKind.INS, v, v, null);

            toadd.add(del);
            //toadd.add(ins);

            for (AutomatonEdge out : cp.outgoingEdgesOf(v)) {

                if (out.getKind() == AutomatonEdge.EdgeKind.DEL || out.getKind() == AutomatonEdge.EdgeKind.INS ||
                        out.getKind() == AutomatonEdge.EdgeKind.SUBST)
                    continue;

                AutomatonEdge sub = new AutomatonEdge(AutomatonEdge.EdgeKind.SUBST, v, out.getTarget(), out.getTrans());
                AutomatonEdge ins = new AutomatonEdge(AutomatonEdge.EdgeKind.INS, v, out.getTarget(), out.getTrans());


                toadd.add(ins);
                toadd.add(sub);
            }
            cp.addAllEdges(toadd);
        }

        return cp;

    }


    public AutomatonGraph buildGraphFromAutomaton(Automaton a) {

        g = new AutomatonGraph();

        nlookup = new HashMap<>();

        timer = 1;

        for (State s : a.getStates()) {

            AutomatonNode.NodeKind kind = AutomatonNode.NodeKind.NORMAL;

            if (s.isAccept()) {
                kind = AutomatonNode.NodeKind.ACCEPT;
            }


            AutomatonNode n = new AutomatonNode(kind, s, 0);


            if (a.getInitialState().equals(s)) {
                g.setStart(n);
            }

            this.nlookup.put(s, n);
        }


        dfsVisit(g.getStart());

        for (AutomatonEdge e : g.edgeSet()) {
            e.setLevel(e.getSource().getLevels() + 1);
        }

        return g;
    }


    public Set<Set<AutomatonNode>> doGetComponents(AutomatonGraph g) {

        this.components = new HashSet<Set<AutomatonNode>>();
        this.low = new HashMap<AutomatonNode, Integer>();
        this.nstack = new Stack<AutomatonNode>();

        for (AutomatonNode n : g.vertexSet()) {
            getComponents(g, n);
        }

        return components;
    }

    public void getComponents(AutomatonGraph g, AutomatonNode n) {
        if (this.low.containsKey(n))
            return;

        int num = this.low.size();

        this.low.put(n, num);

        int spos = this.nstack.size();

        nstack.push(n);

        for (AutomatonEdge succ : g.outgoingEdgesOf(n)) {
            getComponents(g, succ.getTarget());
            this.low.put(n, Math.min(this.low.get(n), this.low.get(succ.getTarget())));
        }

        if (num == this.low.get(n)) {

            Set<AutomatonNode> comp = new LinkedHashSet<AutomatonNode>();
            while (true) {
                AutomatonNode sn = this.nstack.pop();
                comp.add(sn);
                if (this.nstack.size() == spos) {
                    break;
                }
            }

            for (AutomatonNode c : comp) {
                this.low.put(c, g.vertexSet().size());
            }
            this.components.add(comp);
        }
    }


    public void dfsVisit(AutomatonNode srcnode) {


        this.timer++;

        srcnode.setD(this.timer);
        srcnode.setColor(AutomatonNode.Color.GRAY);

        State srcstate = srcnode.getState();
        Set<Transition> trans = srcstate.getTransitions();

        //LOGGER.info("SRCNODE " + srcnode + " " + trans.size());
        for (Transition t : trans) {

            AutomatonNode destnode = nlookup.get(t.getDest());

            //LOGGER.info("ID " + srcnode + " " + destnode);
            //LOGGER.info("DTIME " + srcnode.getD() + " " + destnode.getD());
            //LOGGER.info("COLOR " + srcnode.getColor() + " " + destnode.getColor());

            if (destnode.getColor() == AutomatonNode.Color.WHITE) {
                //LOGGER.info("WHITE " + srcnode + " " + destnode);

                destnode.setLevel(srcnode.getLevels() + 1);
                AutomatonEdge e = new AutomatonEdge(AutomatonEdge.EdgeKind.TREE, srcnode, destnode, t);
                g.addEdge(e);
                dfsVisit(destnode);
            } else if (destnode.getColor() == AutomatonNode.Color.GRAY) {
                AutomatonEdge e = new AutomatonEdge(AutomatonEdge.EdgeKind.BACK, srcnode, destnode, t);
                g.addEdge(e);
            } else if (destnode.getColor() == AutomatonNode.Color.BLACK) {

                AutomatonEdge.EdgeKind k = AutomatonEdge.EdgeKind.TREE;
                //LOGGER.info("SRCNODE " + srcnode.getD() + " Destnode" + destnode.getD());
                if (srcnode.getD() < destnode.getD()) {
                    k = AutomatonEdge.EdgeKind.FWD;
                } else if (srcnode.getD() > destnode.getD()) {
                    k = AutomatonEdge.EdgeKind.CROSS;
                }

                AutomatonEdge e = new AutomatonEdge(k, srcnode, destnode, t);
                g.addEdge(e);
            }

        }

        srcnode.setColor(AutomatonNode.Color.BLACK);
        //LOGGER.info("srcnode color black :" + srcnode);
        this.timer++;
        srcnode.setF(this.timer);
    }


}
