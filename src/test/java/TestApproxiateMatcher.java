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

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.Prex;

import java.util.Random;


public class TestApproxiateMatcher {

    final static Logger logger = LoggerFactory.getLogger(TestApproxiateMatcher.class);

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

        RandomStringUtils rs = new RandomStringUtils();
        Random randomGenerator = new Random();

        for(int i = 0; i < 100; i++) {
            int size0 =  randomGenerator.nextInt(25);
            int size1 =  randomGenerator.nextInt(25);
            String s1 = rs.randomAlphanumeric(size0);
            String s2 = rs.randomAlphanumeric(size1);
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2,false,false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1,s2));
            Assert.assertEquals(pdiff, ldiff);
        }
    }


    @Test
    public void testLevenshteinIgnoreCase() {

        RandomStringUtils rs = new RandomStringUtils();
        Random randomGenerator = new Random();

        for(int i = 0; i < 100; i++) {
            int size0 =  randomGenerator.nextInt(25);
            int size1 =  randomGenerator.nextInt(25);
            String s1 = rs.randomAlphanumeric(size0);
            String s2 = rs.randomAlphanumeric(size1);
            Prex am = new Prex(s1);
            double pdiff = am.evaluateCost(s2,true,false);
            double ldiff = Math.abs(StringUtils.getLevenshteinDistance(s1.toUpperCase(),s2.toUpperCase()));
            Assert.assertEquals(pdiff, ldiff);
        }
    }


}

