/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.vha.isaac.ochre.model.logic.node.external;


import gov.vha.isaac.ochre.api.DataTarget;
import gov.vha.isaac.ochre.api.logic.Node;
import gov.vha.isaac.ochre.model.logic.LogicalExpressionOchreImpl;
import gov.vha.isaac.ochre.api.logic.NodeSemantic;
import gov.vha.isaac.ochre.model.logic.node.AbstractNode;
import gov.vha.isaac.ochre.model.logic.node.internal.RoleNodeSomeWithSequences;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;
import gov.vha.isaac.ochre.api.util.UuidT5Generator;

/**
 *
 * @author kec
 */
public class RoleNodeSomeWithUuids extends TypedNodeWithUuids {

    public RoleNodeSomeWithUuids(LogicalExpressionOchreImpl logicGraphVersion, DataInputStream dataInputStream) throws IOException {
        super(logicGraphVersion, dataInputStream);
    }

    public RoleNodeSomeWithUuids(LogicalExpressionOchreImpl logicGraphVersion, UUID typeConceptUuid, AbstractNode child) {
        super(logicGraphVersion, typeConceptUuid, child);
    }

    public RoleNodeSomeWithUuids(RoleNodeSomeWithSequences internalForm) {
        super(internalForm);
    }

    @Override
    public void writeNodeData(DataOutput dataOutput, DataTarget dataTarget) throws IOException {
        super.writeData(dataOutput, dataTarget);
    }


    @Override
    public NodeSemantic getNodeSemantic() {
        return NodeSemantic.ROLE_SOME;
    }

    @Override
    protected UUID initNodeUuid() {
        return UuidT5Generator.get(getNodeSemantic().getSemanticUuid(),
                typeConceptUuid.toString());

    }

    @Override
    public String toString() {
        return toString("");
    }
    
    @Override
    public String toString(String nodeIdSuffix) {
        return "RoleNodeSome[" + getNodeIndex() + nodeIdSuffix + "]" + super.toString(nodeIdSuffix);
    }
    
    @Override
    protected int compareTypedNodeFields(Node o) {
        // node semantic already determined equals. 
        return 0;
    }
    
}
