/* 
 * -------------------------------------------------------------------
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
 *   Apr 13, 2006 (wiswedel): created
 */
package org.knime.base.node.mine.scorer.entrop;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.property.hilite.DefaultHiLiteHandler;
import org.knime.core.node.property.hilite.DefaultHiLiteMapper;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteTranslator;


/**
 * 
 * @author Bernd Wiswedel, University of Konstanz
 */
public class EntropyNodeModel extends NodeModel {
    // DO NOT SWAP VALUES WITHOUT UPDATING THE XML!
    /** Inport port of the reference clustering. */
    static final int INPORT_REFERENCE = 0;

    /** Inport port of the clustering to judge. */
    static final int INPORT_CLUSTERING = 1;

    /** Config identifier: column name in reference table. */
    static final String CFG_REFERENCE_COLUMN = "reference_table_col_column";

    /** Config identifier: column name in clustering table. */
    static final String CFG_CLUSTERING_COLUMN = "clustering_table_col_column";

    private String m_referenceCol;

    private String m_clusteringCol;

    private EntropyCalculator m_calculator;

    private final HiLiteTranslator m_translator;

    public EntropyNodeModel() {
        super(2, 0);
        m_translator = new HiLiteTranslator(new DefaultHiLiteHandler());
    }

    /**
     * Get the hilite handler that the view talks to. So far only needed to
     * trigger hilite events (unless there are duplicate views).
     * 
     * @return the hilite handler for the view
     */
    public HiLiteHandler getViewHiliteHandler() {
        return m_translator.getFromHiLiteHandler();
    }

    /**
     * @return get the statistics which has been created in the last execute or
     *         <code>null</code> if the node has been reset
     */
    public EntropyCalculator getCalculator() {
        return m_calculator;
    }

    /**
     * @see NodeModel#saveSettingsTo(NodeSettingsWO)
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        if (m_referenceCol != null) {
            settings.addString(CFG_REFERENCE_COLUMN, m_referenceCol);
            settings.addString(CFG_CLUSTERING_COLUMN, m_clusteringCol);
        }
    }

    /**
     * @see NodeModel#validateSettings(NodeSettingsRO)
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        settings.getString(CFG_REFERENCE_COLUMN);
        settings.getString(CFG_CLUSTERING_COLUMN);
    }

    /**
     * @see NodeModel#loadValidatedSettingsFrom(NodeSettingsRO)
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_referenceCol = settings.getString(CFG_REFERENCE_COLUMN);
        m_clusteringCol = settings.getString(CFG_CLUSTERING_COLUMN);
    }

    /**
     * @see NodeModel#execute(BufferedDataTable[], ExecutionContext)
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        DataTable reference = inData[INPORT_REFERENCE];
        DataTable clustering = inData[INPORT_CLUSTERING];
        int referenceColIndex = reference.getDataTableSpec().findColumnIndex(
                m_referenceCol);
        int clusteringColIndex = clustering.getDataTableSpec().findColumnIndex(
                m_clusteringCol);
        m_calculator = new EntropyCalculator(reference, clustering,
                referenceColIndex, clusteringColIndex, exec);
        Map<DataCell, Set<DataCell>> map = m_calculator.getClusteringMap();
        m_translator.setMapper(new DefaultHiLiteMapper(map));
        return new BufferedDataTable[0];
    }

    /**
     * @see NodeModel#reset()
     */
    @Override
    protected void reset() {
        m_calculator = null;
        m_translator.setMapper(null);
    }

    /**
     * @see NodeModel#configure(DataTableSpec[])
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        if (m_referenceCol == null || m_clusteringCol == null) {
            throw new InvalidSettingsException(
                    "No auto configuration available\n"
                            + "Please configure in dialog.");

        }
        DataTableSpec reference = inSpecs[INPORT_REFERENCE];
        DataTableSpec clustering = inSpecs[INPORT_CLUSTERING];
        if (!reference.containsName(m_referenceCol)) {
            throw new InvalidSettingsException("Invalid reference column name "
                    + m_referenceCol);
        }
        if (!clustering.containsName(m_clusteringCol)) {
            throw new InvalidSettingsException(
                    "Invalid clustering column name " + m_clusteringCol);
        }
        return new DataTableSpec[0];
    }

    /**
     * @see NodeModel#setInHiLiteHandler(int, HiLiteHandler)
     */
    @Override
    protected void setInHiLiteHandler(final int inIndex,
            final HiLiteHandler hiLiteHdl) {
        if (inIndex == INPORT_CLUSTERING) {
            m_translator.removeAllToHiliteHandlers();
            m_translator.addToHiLiteHandler(hiLiteHdl);
        }
        super.setInHiLiteHandler(inIndex, hiLiteHdl);
    }

    /**
     * @see org.knime.core.node.NodeModel
     *      #loadInternals(java.io.File,ExecutionMonitor)
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        try {
            m_calculator = EntropyCalculator.load(internDir, exec);
            m_translator.setMapper(new DefaultHiLiteMapper(m_calculator
                    .getClusteringMap()));
        } catch (InvalidSettingsException ise) {
            IOException ioe = new IOException("Unable to read settings.");
            ioe.initCause(ise);
            throw ioe;
        }
    }

    /**
     * @see org.knime.core.node.NodeModel
     *      #saveInternals(java.io.File,ExecutionMonitor)
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        assert m_calculator != null;
        m_calculator.save(internDir, exec);
    }
}
