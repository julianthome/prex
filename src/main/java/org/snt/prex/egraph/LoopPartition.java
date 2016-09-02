package org.snt.prex.egraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LoopPartition extends AutomatonNode {

    final static Logger logger = LoggerFactory.getLogger(LoopPartition.class);

    private LinkedList<AutomatonNode> part = null;

    public LoopPartition(Collection<AutomatonNode> nods) {
        super(NodeKind.PARTITION,null,0);
        this.part = new LinkedList<AutomatonNode>();
        this.part.addAll(nods);
    }

    public void add(AutomatonNode n) {
        this.part.add(n);
    }

    public void addAll(Collection<AutomatonNode> n) {
        this.part.addAll(n);
    }

    public int getLen() {
        return this.part.size();
    }

    public boolean subAndReplace(LoopPartition p) {

        logger.info("-----");
        //if(this.part.size() > p.part.size())
        //    return false;

        AutomatonNode n = p.part.getFirst();

        LinkedList<AutomatonNode> npart = new LinkedList<AutomatonNode>();

        if(!this.part.contains(n)) {
            return false;
        }

        int idx = this.part.indexOf(n);

        npart.addAll(this.part.subList(0,idx));

        //assert(this.part.size() - idx >= p.part.size());

        for(int k = 0; k < p.part.size(); k++) {

            logger.info("FIRST " + this.part.get(idx + k));
            logger.info("SND " + p.part.get(k));

            if(!this.part.get(idx + k).equals((p.part.get(k)))) {
                logger.info("FALSE");
                return false;

            }
        }

        npart.add(p);
        npart.addAll(this.part.subList(idx + p.part.size(), this.part.size()));

        this.part = npart;

        return true;
    }

    public boolean containsAtomicNodes() {
        for(AutomatonNode n : this.part) {
            if(n.getKind().equals(NodeKind.PARTITION))
                return false;
        }
        return true;
     }

    public String serializeString() {
        return this.part.toString();
    }

    public List<AutomatonNode> getPlainNodes(){

        List<AutomatonNode> ret = new LinkedList<AutomatonNode>();

        for(AutomatonNode n : this.part) {
            if (n instanceof LoopPartition) {
                ret.addAll(((LoopPartition) n).part);
                continue;
            }
            ret.add(n);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "pid: " + this.getId() + "::" + this.part;
    }




}
