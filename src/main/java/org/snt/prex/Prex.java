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

import java.util.LinkedList;
import java.util.Set;

public class Prex {

    final static Logger LOGGER = LoggerFactory.getLogger(Prex.class);

    private AutomatonGraph eg = null;
    private Automaton a = null;

    // step size is used for normalization
    private double step = 0;

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
        boolean normalize = false;

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

        Option n = Option.builder("n")
                .longOpt("normalize")
                .numberOfArgs(0)
                .valueSeparator(' ')
                .desc("normalize cost to a value in range [0,1]")
                .required(false)
                .build();


        options.addOption(s);
        options.addOption(r);
        options.addOption(c);
        options.addOption(t);
        options.addOption(ic);
        options.addOption(n);


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

        if (cmd.hasOption("n"))
            normalize = true;

        Prex prex = new Prex(regex, icost, scost, dcost);

        double cost = prex.evaluateCost(string, ignoreCase, normalize, printTree);

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
        //LOGGER.info("ANCESTPOR " + ancestor.toString());


        boolean lengthok = false;
        char tocheck = '%';

        //LOGGER.info("NXTPOS " + nxtPos);
        if (ancestor.getPos() < s.length()) {
            //LOGGER.info("LENOK");
            lengthok = true;
        } else {
            //LOGGER.info("LENNOK");
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
            //LOGGER.info("EDGE " + e.toString());
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
                if (ignoreCase) {
                    tocheck = Character.toUpperCase(tocheck);
                    cmin = (char) Math.min(Character.toUpperCase(cmin), Character.toUpperCase(cmax));
                    cmax = (char) Math.max(Character.toUpperCase(cmin), Character.toUpperCase(cmax));
                }

                if (!(cmin <= tocheck && cmax >= tocheck)) {
                    //LOGGER.info("TOCHECK " + tocheck + " vs " + e.getTrans().getMin());
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
        return evaluateCost(s, false, true, false);
    }

    public double evaluateCost(String s, boolean ignoreCase, boolean normalize) {
        return evaluateCost(s, ignoreCase, normalize, false);
    }

    private double evaluateCost(String s,
                                boolean ignoreCase,
                                boolean normalize,
                                boolean printTree) {

        // special case - if automaton is empty string
        // we can just return the lenght of s as cost
        if (this.a.isEmptyString()) {
            return s.length();
        }

        this.step = 0;
        EditTree et = new EditTree();

        EditNode start = new EditNode(this.eg.getStart(), 'S', 0, 0);

        et.addVertex(start);

        // heuristic to initialize global minimum
        double globalMin = Math.max(this.eg.vertexSet().size() + 1, s.length());


        //LOGGER.info("GLOBAL MIN " + globalMin);


        LinkedList<EditNode> leafs = et.getExtendableLeafNodes();


        while (!leafs.isEmpty()) {


            EditNode extendableLeaf = leafs.pop();

            if (extendableLeaf.getAutomatonNode().getKind() == AutomatonNode.NodeKind.ACCEPT &&
                    extendableLeaf.getPos() > s.length() - 1) {
                globalMin = globalMin < 0 ? extendableLeaf.getSum() : Math.min(globalMin, extendableLeaf.getSum());
            }


            if (et.isInExtendedList(extendableLeaf) && !leafs.isEmpty()) {
                extendableLeaf.setState(EditNode.State.NOT_EXTENDABLE);
                continue;
            }

            if (extendableLeaf.getSum() >= globalMin) {
                extendableLeaf.setState(EditNode.State.NOT_EXTENDABLE);
                continue;
            }

            if (extendableLeaf.isExtendable()) {
                extendsNode(et, s, extendableLeaf, ignoreCase);
            }

            leafs = et.getMinLeafs(globalMin);
        }


        double cost = 0.0;

        if (this.step != 0.0) {
            cost = (double) globalMin;
        } else {
            cost = 1.0;
        }

        if (printTree) {
            System.out.println(et.toString());
        }

        if (normalize) {
            cost = (double) cost / (double) this.step;
        }

        return cost;
    }


    public double getStep() {
        return this.step;
    }


}
