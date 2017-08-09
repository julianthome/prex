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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.Prex;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class TestApproxiateMatcher {

    final static Logger LOGGER = LoggerFactory.getLogger(TestApproxiateMatcher.class);

    private static String genRandString() {
        RandomStringUtils rs = new RandomStringUtils();
        Random randomGenerator = new Random();
        int size0 = randomGenerator.nextInt(30) + 1;
        return rs.randomAlphanumeric(size0);
    }


    @Test
    public void testSimple1() {
        Prex am = new Prex("gcg[abc]+");
        Assert.assertTrue(am.evaluateCost("GCGa", true, true) == 0);
        Assert.assertTrue(am.evaluateCost("GCGa", false, true) == 0.75);
        Assert.assertTrue(am.evaluateCost("GCGa", true, false) == 0);
        Assert.assertTrue(am.evaluateCost("GCGa", false, false) == 3);
    }

    @Test
    public void testAllAccepting(){
        Prex am = new Prex(".*3 rue des cerisiers.*");
        Assert.assertTrue(am.evaluateCost("123 3 rue des cerisiers 456", true, true) == 0);
        Assert.assertTrue(am.evaluateCost("world 3 rue des cerisiers hello", true, true) == 0);
    }

    @Test
    public void testSimple2() {
        Prex am = new Prex("[0-9]+ [Oo][Rr] 1=1");
        LOGGER.info("++{}",am.evaluateCost("10 or 1",true,true));
    }

    @Test
    public void testDistance() {

        String s1 = "o AZ 1";
        String s2 = "10 kr 1=o";
        int dist = StringUtils.getLevenshteinDistance(s1, s2);

        LOGGER.info("Dist {}", dist);

        double d = dist/(1.0*Math.max(s1.length(),s2.length()));

        LOGGER.info("D {}",d);


    }


    @Test
    public void testLevenshtein() {


        for (int i = 0; i < 100; i++) {
            String s1 = genRandString();
            String s2 = genRandString();
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2, false, false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1, s2));
            Assert.assertEquals(pdiff, ldiff,0);
        }
    }


    @Test
    public void testLevenshteinIgnoreCase() {

        for (int i = 0; i < 100; i++) {
            String s1 = genRandString();
            String s2 = genRandString();
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2, true, false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1.toUpperCase(), s2.toUpperCase()));
            Assert.assertEquals(pdiff, ldiff,0);
        }
    }

    @Test
    public void testRandRegExp() {

        for (int runs = 0; runs < 20; runs++) {
            Automaton a = new Automaton();
            Set<String> finiteStringSet = new HashSet<String>();

            for (int i = 0; i < 10; i++) {
                String s = genRandString();
                finiteStringSet.add(s);
                Automaton sa = new RegExp(s).toAutomaton();
                a = a.union(sa);
            }

            String input = genRandString();
            String mins = finiteStringSet.iterator().next();

            double min = (double) Math.abs(StringUtils.getLevenshteinDistance(input, finiteStringSet.iterator().next()));
            // find min levenshtein distance
            for (String fs : finiteStringSet) {
                int ld = Math.abs(StringUtils.getLevenshteinDistance(input, fs));
                if (ld < min) {
                    min = ld;
                    mins = fs;
                }
            }

            LOGGER.info("Min ld is: " + min + " input:" + input + " " + mins);
            Prex am = new Prex(a, 1.0, 1.0, 1.0);
            double cost = am.evaluateCost(input, false, false);
            Assert.assertTrue(cost == min);
        }
    }

}

