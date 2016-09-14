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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.Prex;


public class TestApproxiateMatcher {

    final static Logger logger = LoggerFactory.getLogger(TestApproxiateMatcher.class);



    @Test
    public void testInvocation() {

        //Automaton b = new RegExp("598d4c200461b81522a3328565c25f7c").toAutomaton();

        Prex am = new Prex("gcg[abc]+");

        System.out.println(am.evaluateCost("GCGa", true));
        System.out.println(am.evaluateCost("GCGa", false));

    }




}

