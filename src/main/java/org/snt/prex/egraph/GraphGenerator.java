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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GraphGenerator {


    final static Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

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

        //logger.info("HH " + cp.toString());

        for(AutomatonNode v : cp.vertexSet()) {

            Set<AutomatonEdge> toadd = new HashSet<AutomatonEdge>();

            AutomatonEdge del = new AutomatonEdge(AutomatonEdge.EdgeKind.DEL, v, v, null);
            //AutomatonEdge ins = new AutomatonEdge(AutomatonEdge.EdgeKind.INS, v, v, null);

            toadd.add(del);
            //toadd.add(ins);

            for(AutomatonEdge out : cp.outgoingEdgesOf(v)){

                if(out.getKind() == AutomatonEdge.EdgeKind.DEL || out.getKind() == AutomatonEdge.EdgeKind.INS ||
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

        nlookup = new HashMap<State, AutomatonNode>();

        timer = 1;

        for (State s : a.getStates()) {

            AutomatonNode.NodeKind kind = AutomatonNode.NodeKind.NORMAL;

            if(s.isAccept()) {
                kind = AutomatonNode.NodeKind.ACCEPT;
            }


            AutomatonNode n = new AutomatonNode(kind, s, 0);


            if(a.getInitialState().equals(s)) {
                g.setStart(n);
            }

            this.nlookup.put(s, n);
        }


        dfsVisit(g.getStart());

        for(AutomatonEdge e : g.edgeSet()) {
            e.setLevel(e.getSource().getLevels() + 1);
        }

        return g;
    }



    public Set<Set<AutomatonNode>> doGetComponents(AutomatonGraph g) {

        this.components = new HashSet<Set<AutomatonNode>>();
        this.low = new HashMap<AutomatonNode, Integer>();
        this.nstack = new Stack<AutomatonNode>();

        for(AutomatonNode n : g.vertexSet()) {
            getComponents(g, n);
        }

        return components;
    }

    public void getComponents(AutomatonGraph g, AutomatonNode n) {
        if(this.low.containsKey(n))
            return;

        int num = this.low.size();

        this.low.put(n, num);

        int spos = this.nstack.size();

        nstack.push(n);

        for(AutomatonEdge succ : g.outgoingEdgesOf(n)) {
            getComponents(g, succ.getTarget());
            this.low.put(n, Math.min(this.low.get(n), this.low.get(succ.getTarget())));
        }

        if(num == this.low.get(n)) {

            Set<AutomatonNode> comp = new LinkedHashSet<AutomatonNode>();
            while(true) {
                AutomatonNode sn = this.nstack.pop();
                comp.add(sn);
                if(this.nstack.size() == spos) {
                    break;
                }
            }

            for(AutomatonNode c : comp) {
                this.low.put(c,g.vertexSet().size());
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

        //logger.info("SRCNODE " + srcnode + " " + trans.size());
        for (Transition t : trans) {

            AutomatonNode destnode = nlookup.get(t.getDest());

            //logger.info("ID " + srcnode + " " + destnode);
            //logger.info("DTIME " + srcnode.getD() + " " + destnode.getD());
            //logger.info("COLOR " + srcnode.getColor() + " " + destnode.getColor());

            if (destnode.getColor() == AutomatonNode.Color.WHITE) {
                //logger.info("WHITE " + srcnode + " " + destnode);

                destnode.setLevel(srcnode.getLevels() + 1);
                AutomatonEdge e = new AutomatonEdge(AutomatonEdge.EdgeKind.TREE, srcnode, destnode, t);
                g.addEdge(e);
                dfsVisit(destnode);
            } else if (destnode.getColor() == AutomatonNode.Color.GRAY) {
                AutomatonEdge e = new AutomatonEdge(AutomatonEdge.EdgeKind.BACK, srcnode, destnode, t);
                g.addEdge(e);
            } else if (destnode.getColor() == AutomatonNode.Color.BLACK) {

                AutomatonEdge.EdgeKind k = AutomatonEdge.EdgeKind.TREE;
                //logger.info("SRCNODE " + srcnode.getD() + " Destnode" + destnode.getD());
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
        //logger.info("srcnode color black :" + srcnode);
        this.timer++;
        srcnode.setF(this.timer);
    }

    public AutomatonGraph group(AutomatonGraph sg, List<LoopPartition> l) {

        for(LoopPartition t : l) {

            List<AutomatonNode> nods = t.getPlainNodes();
            assert(nods.size() > 0);


            AutomatonNode root = nods.get(0);

            Set<AutomatonEdge> incoming = g.incomingEdgesOf(root);

            AutomatonGraph subgraph = g.subgraph(nods);

            logger.info(subgraph.toString());
            g.removeAllVertices(subgraph.vertexSet());

            break;
        }

        return null;


    }

    public List<LoopPartition> getCyclePartitions(AutomatonGraph g) {
        SzwarcfiterLauerSimpleCycles<AutomatonNode, AutomatonEdge> slauer = new SzwarcfiterLauerSimpleCycles(g);
        List<List<AutomatonNode>> cyc = slauer.findSimpleCycles();

        List<LoopPartition> pset = new ArrayList<LoopPartition>();

        Collections.sort(cyc, new Comparator<List<AutomatonNode>>(){
            public int compare(List<AutomatonNode> a1, List<AutomatonNode> a2) {
                return a2.size() - a1.size(); // assumes you want biggest to smallest
            }
        });

        for(List<AutomatonNode> l : cyc) {
            LoopPartition np = new LoopPartition(l);
            pset.add(np);
        }

        boolean cont = true;

        // Fixed Point Iteration
        while(cont) {
            cont = false;
            for(LoopPartition p : pset) {
                int idx = pset.indexOf(p);
                idx ++;
                while(idx < pset.size()) {
                    LoopPartition op = pset.get(idx);
                    //logger.info("CC " + p);
                    //logger.info("OTH " + op);
                    cont = p.subAndReplace(op) || cont;
                    idx ++;
                }
            }
        }
        return pset;
    }

}
