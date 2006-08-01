/*
 * --------------------------------------------------------------------- *
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
 * --------------------------------------------------------------------- *
 */
package org.knime.base.node.mine.bfn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.RowIterator;


/**
 * Iterator over all
 * {@link de.unikn.knime.dev.node.mine.bfn.BasisFunctionLearnerRow}s within the
 * model.
 * 
 * Supports to skip certain classes.
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
public final class BasisFunctionIterator extends RowIterator {
    /*
     * The underlying table containing the basisfunctions.
     */
    private final BasisFunctionLearnerTable m_table;

    /*
     * A map containing the basisfunctions in an <code>ArrayList</code> of
     * each class addressed by the class label as <code>DataCell</code> key.
     */
    private final Map<DataCell, List<BasisFunctionLearnerRow>> m_map;

    /*
     * The iterator from the underlying table.
     */
    private final Iterator m_it;

    /*
     * Current basisfunctions for class index <code>m_bfIndex</code>.
     */
    private ArrayList m_bfs;

    /*
     * Current class index for the basisfunction array <code>m_bfs</code>.
     */
    private int m_bfIndex;

    /**
     * Creates a new basisfunction iterator. Does not perform error checking.
     * 
     * @param table the underlying learner table
     * @throws NullPointerException if the table is <code>null</code>
     */
    public BasisFunctionIterator(final BasisFunctionLearnerTable table) {
        m_table = table;
        m_map = m_table.getBasisFunctions();
        m_it = m_map.keySet().iterator();
        skipClass();
    }

    /**
     * Checks if the iterator already reached the end of the iteration. Here, we
     * need to check to conditions. One, if the current iterator is at end, and,
     * second, if the all classes have been processed.
     * 
     * @return <code>true</code> if the end has been reached otherwise.
     */
    @Override
    public boolean hasNext() {
        if (m_bfs == null) {
            return false;
        }
        if (m_bfIndex == m_bfs.size()) {
            skipClass();
            if (m_bfs == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next row in the iteration. If the current iteration is at
     * end, the iteration will be start over at the next class, otherwise the
     * basisfunction index is increased.
     * 
     * @return the next row in the iteration
     * @throws NoSuchElementException if there are no more rows
     */
    public BasisFunctionLearnerRow nextBasisFunction() {
        if (hasNext()) {
            return (BasisFunctionLearnerRow)m_bfs.get(m_bfIndex++);
        }
        throw new NoSuchElementException("No more elements to return.");
    }

    /**
     * @see #nextBasisFunction()
     * @return the next row in the iteration
     */
    @Override
    public DataRow next() {
        return nextBasisFunction();
    }

    /**
     * Skips the current class, {@link #next()} will then return the first basis
     * function of the next class. If the current class is the last, the
     * basefuntion index is set to the last element <code>+1</code> and
     * {@link #hasNext()} will return <code>false</code>.
     */
    public void skipClass() {
        m_bfIndex = 0;
        // step forward until non-empty list is found
        while (m_it.hasNext()) {
            // jump to next class and init next list of basisfunctions
            m_bfs = (ArrayList)m_map.get(m_it.next());
            if (m_bfs.size() > 0) {
                // break while loop, new bf found
                return;
            }
            assert false;
        }
        m_bfs = null;
        assert (!m_it.hasNext());
    }
}
