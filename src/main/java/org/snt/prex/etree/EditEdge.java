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

package org.snt.prex.etree;


import org.snt.prex.egraph.AutomatonEdge;

public class EditEdge {

    private EditNode src;
    private EditNode dst;
    private int cost;
    private AutomatonEdge ae;
    private AutomatonEdge.EdgeKind kind;

    public EditEdge(EditNode src, EditNode dst, int cost, AutomatonEdge.EdgeKind kind, AutomatonEdge ae){
        this.src = src;
        this.dst = dst;
        this.cost = cost;
        this.ae = ae;
        this.kind = kind;
    }


    public EditNode getSource() {
        return this.src;
    }

    public EditNode getTarget() {
        return this.dst;
    }

    public int getCost() {
        return this.cost;
    }

    public AutomatonEdge.EdgeKind getKind() {
        return this.ae.getKind();
    }

    public AutomatonEdge getAutomatonEdge() {
        return this.ae;
    }
}

