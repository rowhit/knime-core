/*
 * ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2006
 * University of Konstanz, Germany.
 * Chair for Bioinformatics and Information Mining
 * Prof. Dr. Michael R. Berthold
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any quesions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * -------------------------------------------------------------------
 * 
 * History
 *   11.06.2006 (Tobias Koetter): created
 */
package org.knime.base.node.viz.histogram.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;

/**
 * 
 * @author Peter Ohl, University of Konstanz
 */
public class HistogramNodeFactory extends NodeFactory {
    /**
     * @see org.knime.core.node.NodeFactory#createNodeModel()
     */
    @Override
    public NodeModel createNodeModel() {
        return new HistogramNodeModel();
    }

    /**
     * @see org.knime.core.node.NodeFactory#getNrNodeViews()
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * @see org.knime.core.node.NodeFactory#createNodeView(int, NodeModel)
     */
    @Override
    public NodeView createNodeView(final int viewIndex,
            final NodeModel nodeModel) {
        assert viewIndex == 0;
        return new HistogramNodeView(nodeModel);
    }

    /**
     * @see org.knime.core.node.NodeFactory#createNodeDialogPane()
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return null;
        // return new HistogramNodeDialogPane();
    }

    /**
     * @see org.knime.core.node.NodeFactory#hasDialog()
     */
    @Override
    public boolean hasDialog() {
        return false;
    }
}
