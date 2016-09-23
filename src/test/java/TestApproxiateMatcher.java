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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.Prex;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class TestApproxiateMatcher {

    final static Logger logger = LoggerFactory.getLogger(TestApproxiateMatcher.class);

    private static String genRandString() {
        RandomStringUtils rs = new RandomStringUtils();
        Random randomGenerator = new Random();
        int size0 = randomGenerator.nextInt(25);
        return rs.randomAlphanumeric(size0);
    }


    @Test
    public void testSimple() {
        Prex am = new Prex("gcg[abc]+");
        Assert.assertTrue(am.evaluateCost("GCGa", true, true) == 0);
        Assert.assertTrue(am.evaluateCost("GCGa", false, true) == 0.75);
        Assert.assertTrue(am.evaluateCost("GCGa", true, false) == 0);
        Assert.assertTrue(am.evaluateCost("GCGa", false, false) == 3);
    }


    @Test
    public void testLevenshtein() {


        for(int i = 0; i < 100; i++) {
            String s1 = genRandString();
            String s2 = genRandString();
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2,false,false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1,s2));
            Assert.assertEquals(pdiff, ldiff);
        }
    }


    @Test
    public void testLevenshteinIgnoreCase() {

        for(int i = 0; i < 100; i++) {
            String s1 = genRandString();
            String s2 = genRandString();
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2,true,false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1.toUpperCase(),s2.toUpperCase()));
            Assert.assertEquals(pdiff, ldiff);
        }
    }

    @Test
    public void testRandRegExp() {

        for(int runs = 0; runs < 20; runs ++ ) {
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

            logger.info("Min ld is: " + min + " input:" + input + " " + mins);
            Prex am = new Prex(a, 1.0, 1.0, 1.0);
            double cost = am.evaluateCost(input, false, false);
            Assert.assertTrue(cost == min);
        }
    }


}

