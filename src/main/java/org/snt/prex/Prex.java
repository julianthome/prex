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
import dk.brics.automaton.RegExp;
import org.apache.commons.cli.*;
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

public class Prex {

    final static Logger logger = LoggerFactory.getLogger(Prex.class);

    private AutomatonGraph eg = null;
    private Automaton a = null;

    // step size is used for normalization
    private double step = 0;

    private HashSet<EditNode> extendedList = null;

    // used for defining cost as double values
    private static int INSIDX = 0;
    private static int SUBIDX = 1;
    private static int DELIDX = 2;
    private double[] cost = {1.0, 1.0, 1.0};


    public Prex(String s) {
        this(s, 1.0, 1.0, 1.0);
    }

    public Prex(String s, double icost, double scost, double dcost) {
        this(new RegExp(s).toAutomaton(), icost, scost, dcost);
    }

    public Prex(Automaton a, double icost, double scost, double dcost) {
        Automaton ch = a;
        AutomatonGraph g = GraphGenerator.getInstance().buildGraphFromAutomaton(ch);
        this.eg = GraphGenerator.getInstance().buildEditGraph(g);
        this.extendedList = new HashSet<EditNode>();
        this.a = a;
        cost[INSIDX] = icost;
        cost[SUBIDX] = scost;
        cost[DELIDX] = dcost;
    }

    public static void main(String[] args) {

        String regex = "";
        String string = "";

        boolean printTree = false;
        boolean ignoreCase = false;

        double icost = 1.0;
        double scost = 1.0;
        double dcost = 1.0;


        HelpFormatter hformatter = new HelpFormatter();

        Options options = new Options();
        // Binary arguments
        options.addOption("h", "print this message");

        // string
        Option s = Option.builder("s")
                .longOpt("string")
                .numberOfArgs(1)
                .valueSeparator(' ')
                .desc("constant string")
                .argName("string").valueSeparator()
                .required(true)
                .build();

        // regular expression
        Option r = Option.builder("r")
                .longOpt("regex")
                .numberOfArgs(1)
                .valueSeparator(' ')
                .desc("regular expression")
                .argName("regex").valueSeparator()
                .required(true)
                .build();

        // cost
        Option c = Option.builder("c")
                .longOpt("cost")
                .numberOfArgs(3)
                .valueSeparator(' ')
                .desc("cost values <insertion> <substitution> <deletion> as double")
                .argName("cost").valueSeparator()
                .required(true)
                .build();


        Option t = Option.builder("t")
                .longOpt("print-tree")
                .numberOfArgs(0)
                .valueSeparator(' ')
                .desc("print branch and bounds edit tree")
                .required(false)
                .build();

        Option ic = Option.builder("ic")
                .longOpt("ignore-case")
                .numberOfArgs(0)
                .valueSeparator(' ')
                .desc("ignore case of s when matching against r")
                .required(false)
                .build();


        options.addOption(s);
        options.addOption(r);
        options.addOption(c);
        options.addOption(t);
        options.addOption(ic);


        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                hformatter.printHelp("java -jar prex.jar -s <input string> -r <regex> [t]", options);
                System.exit(0);
            }
        } catch (ParseException e) {
            hformatter.printHelp("java -jar prex.jar -s <input string> -r <regex> [t]", options);
            System.err.println("Wrong command line argument combination " + e.getMessage());
            System.exit(-1);
        }

        regex = cmd.getOptionValue("regex");
        string = cmd.getOptionValue("string");


        if (cmd.hasOption("cost")) {
            String[] acost = cmd.getOptionValues("cost");
            icost = Double.parseDouble(acost[INSIDX]);
            dcost = Double.parseDouble(acost[DELIDX]);
            scost = Double.parseDouble(acost[SUBIDX]);
        }

        if (cmd.hasOption("t"))
            printTree = true;

        if (cmd.hasOption("ic"))
            ignoreCase = true;

        Prex prex = new Prex(regex, icost, scost, dcost);

        double cost = prex.evaluateCost(string, ignoreCase, printTree);

        System.out.println("Cost : " + cost);
    }

    public double getCostFromEdgeKind(AutomatonEdge.EdgeKind kind) {

        switch (kind) {
            case SUBST:
                return cost[SUBIDX];
            case INS:
                return cost[INSIDX];
            case DEL:
                return cost[DELIDX];
            case NORMAL:
            case BACK:
            case FWD:
            case CROSS:
            case TREE:
                return 0.0;
        }
        return 0.0;
    }


    public void extendsNode(EditTree et, String s, EditNode ancestor, boolean ignoreCase) {
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

        if (!lengthok) {
            out = this.eg.getOutgoingEdgesOfKind(ancestor.getAutomatonNode(), AutomatonEdge.EdgeKind.INS);

            if (out == null || out.size() == 0) {
                ancestor.setState(EditNode.State.NOT_EXTENDABLE);
            }
        } else {
            tocheck = s.charAt(ancestor.getPos());
            out = this.eg.outgoingEdgesOf(ancestor.getAutomatonNode());
        }


        for (AutomatonEdge e : out) {
            //logger.info("EDGE " + e.toString());
            int apos = 0;
            double cost = getCostFromEdgeKind(e.getKind());


            //if(ancestor.getPos() == 0 && e.getKind() == AutomatonEdge.EdgeKind.SUBST) {
            //    continue;
            // }

            if (e.getKind() == AutomatonEdge.EdgeKind.INS) {
                apos = ancestor.getPos();
            } else {
                apos = ancestor.getPos() + 1;
            }

            if (e.getKind().isMatching()) {
                char cmin = e.getTrans().getMin();
                char cmax = e.getTrans().getMax();
                // NON-Match
                if(ignoreCase) {
                    tocheck = Character.toUpperCase(tocheck);
                    cmin = (char)Math.min(Character.toUpperCase(cmin), Character.toUpperCase(cmax));
                    cmax = (char)Math.max(Character.toUpperCase(cmin), Character.toUpperCase(cmax));
                }

                if (!(cmin <= tocheck && cmax >= tocheck)) {
                    //logger.info("TOCHECK " + tocheck + " vs " + e.getTrans().getMin());
                    continue;
                } else {
                    cost = 0;
                }
            }

            this.step = Math.max(step, ancestor.getSteps() + cost);

            EditNode en = new EditNode(e.getTarget(), tocheck, apos, ancestor.getSteps() + cost);
            EditEdge ee = new EditEdge(ancestor, en, cost, e.getKind(), e);

            et.addVertex(en);
            et.addEdge(ancestor, en, ee);
            en.setSum(ancestor.getSum() + cost);

        }

    }

    public double evaluateCost(String s) {
        return evaluateCost(s, false, false);
    }

    public double evaluateCost(String s, boolean ignoreCase) {
        return evaluateCost(s, ignoreCase, false);
    }

    private double evaluateCost(String s, boolean ignoreCase, boolean printTree) {

        this.step = 0;
        EditTree et = new EditTree();

        EditNode start = new EditNode(this.eg.getStart(), 'S', 0, 0);

        et.addVertex(start);

        // heuristic to initialize global minimum
        double globalMin = Math.max(this.eg.vertexSet().size() + 1, s.length());


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
                extendsNode(et, s, ancestor, ignoreCase);
            }

            leafs = et.getMinLeafs(globalMin);
        }


        double cost = 0.0;

        if (this.step != 0.0) {
            cost = (double) globalMin / (double) this.step;
        } else {
            cost = 1.0;
        }

        if (printTree) {
            System.out.println(et.toString());
        }

        return cost;
    }


    public double getStep() {
        return this.step;
    }


}
