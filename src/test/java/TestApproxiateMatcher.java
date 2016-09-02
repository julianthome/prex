import dk.brics.automaton.RegExp;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.prex.Approx;


public class TestApproxiateMatcher {

    final static Logger logger = LoggerFactory.getLogger(TestApproxiateMatcher.class);



    @Test
    public void testInvocation() {

        //Automaton b = new RegExp("598d4c200461b81522a3328565c25f7c").toAutomaton();

        Approx am = new Approx(new RegExp("gcg[abc]+").toAutomaton());

        logger.info("RET "+ am.evaluateCost("GCGa"));

    }




}

