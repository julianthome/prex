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

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.EdgeSetFactory;

import java.util.Collection;
import java.util.Set;

public class AbstractGraph implements DirectedGraph<AutomatonNode, AutomatonEdge> {


    private final DirectedPseudograph<AutomatonNode, AutomatonEdge> delegate;

    public AbstractGraph() {
        this.delegate = new DirectedPseudograph<AutomatonNode, AutomatonEdge>(new EdgeFact());
    }

    public boolean addEdge(AutomatonNode arg0, AutomatonNode arg1, AutomatonEdge arg2) {
        return delegate.addEdge(arg0, arg1, arg2);
    }

    public AutomatonEdge addEdge(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.addEdge(arg0, arg1);
    }

    public boolean addVertex(AutomatonNode arg0) {
        return delegate.addVertex(arg0);
    }

    public boolean containsEdge(AutomatonEdge arg0) {
        return delegate.containsEdge(arg0);
    }

    public boolean containsEdge(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.containsEdge(arg0, arg1);
    }

    public boolean containsVertex(AutomatonNode arg0) {
        return delegate.containsVertex(arg0);
    }

    public int degreeOf(AutomatonNode arg0) {
        return delegate.degreeOf(arg0);
    }


    public Set<AutomatonEdge> edgeSet() {
        return delegate.edgeSet();
    }


    public Set<AutomatonEdge> edgesOf(AutomatonNode arg0) {
        return delegate.edgesOf(arg0);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof AbstractGraph)) {
            return false;
        } else {
            AbstractGraph other = (AbstractGraph) obj;
            DirectedPseudograph<AutomatonNode, AutomatonEdge> otherDelegate = (DirectedPseudograph<AutomatonNode, AutomatonEdge>) other.delegate;
            return delegate.equals(otherDelegate);
        }
    }


    public Set<AutomatonEdge> getAllEdges(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.getAllEdges(arg0, arg1);
    }

    public AutomatonEdge getEdge(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.getEdge(arg0, arg1);
    }

    public EdgeFactory<AutomatonNode, AutomatonEdge> getEdgeFactory() {
        return delegate.getEdgeFactory();
    }

    public AutomatonNode getEdgeSource(AutomatonEdge arg0) {
        return delegate.getEdgeSource(arg0);
    }

    public AutomatonNode getEdgeTarget(AutomatonEdge arg0) {
        return delegate.getEdgeTarget(arg0);
    }

    public double getEdgeWeight(AutomatonEdge arg0) {
        return delegate.getEdgeWeight(arg0);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public int inDegreeOf(AutomatonNode arg0) {
        return delegate.inDegreeOf(arg0);
    }

    public Set<AutomatonEdge> incomingEdgesOf(AutomatonNode arg0) {
        return delegate.incomingEdgesOf(arg0);
    }

    public boolean isAllowingLoops() {
        return delegate.isAllowingLoops();
    }

    public boolean isAllowingMultipleEdges() {
        return delegate.isAllowingMultipleEdges();
    }

    public int outDegreeOf(AutomatonNode arg0) {
        return delegate.outDegreeOf(arg0);
    }

    public Set<AutomatonEdge> outgoingEdgesOf(AutomatonNode arg0) {
        return delegate.outgoingEdgesOf(arg0);
    }

    public boolean removeAllEdges(Collection<? extends AutomatonEdge> arg0) {
        return delegate.removeAllEdges(arg0);
    }

    public Set<AutomatonEdge> removeAllEdges(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.removeAllEdges(arg0, arg1);
    }

    public boolean removeAllVertices(Collection<? extends AutomatonNode> arg0) {
        return delegate.removeAllVertices(arg0);
    }

    public boolean removeEdge(AutomatonEdge arg0) {
        return delegate.removeEdge(arg0);
    }

    public AutomatonEdge removeEdge(AutomatonNode arg0, AutomatonNode arg1) {
        return delegate.removeEdge(arg0, arg1);
    }

    public boolean removeVertex(AutomatonNode arg0) {
        return delegate.removeVertex(arg0);
    }

    public void setEdgeSetFactory(EdgeSetFactory<AutomatonNode, AutomatonEdge> arg0) {
        delegate.setEdgeSetFactory(arg0);
    }

    public void setEdgeWeight(AutomatonEdge arg0, double arg1) {
        delegate.setEdgeWeight(arg0, arg1);
    }

    public String toString() {
        return delegate.toString();
    }

    public Set<AutomatonNode> vertexSet() {
        return delegate.vertexSet();
    }

}
