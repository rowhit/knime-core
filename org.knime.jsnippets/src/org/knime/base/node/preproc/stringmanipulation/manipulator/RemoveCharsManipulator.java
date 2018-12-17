/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   04.10.2011 (hofer): created
 */
package org.knime.base.node.preproc.stringmanipulation.manipulator;

import org.knime.core.util.string.KnimeStringUtils;

/**
 * The removeChars string manipulator to remove all occurrences of given
 * characters from a string.
 *
 * @author Heiko Hofer
 */
public class RemoveCharsManipulator implements Manipulator {

    /**
     * Remove all occurrences of given characters from a string.
     *
     * @param str the string
     * @param chars the characters that will be removed
     * @return the processed string
     */
    public static String removeChars(final String str, final String chars) {
        return KnimeStringUtils.removeChars(str, chars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategory() {
        return "Remove";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "removeChars";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return getName() + "(str, chars)";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrArgs() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Removes specific characters."
            + "<br/><br/>"
            + "<strong>Examples:</strong>"
            + "<br/>"
            + "<table>"
            + "<tr><td>removeChars(\"a,&nbsp;&nbsp;b&nbsp;,&nbsp;c\", "
            + "\"&nbsp;\")</td>"
            + "<td>=&nbsp;\"a,b,c\"</td></tr>"

            + "<tr><td>removeChars(\"a,&nbsp;&nbsp;b&nbsp;,&nbsp;c\", "
            + "\",\")</td>"
            + "<td>=&nbsp;\"a&nbsp;&nbsp;b&nbsp;&nbsp;c\"</td></tr>"

            + "<tr><td>removeChars(\"a,&nbsp;&nbsp;b&nbsp;,&nbsp;c\", "
            + "\",&nbsp;\")</td>"
            + "<td>=&nbsp;\"abc\" (remove space character and the "
            + "comma character)</td></tr>"

            + "<tr><td>removeChars(\"\", *)</td>"
            + "<td>=&nbsp;\"\"</td></tr>"

            + "<tr><td>removeChars(null, *)</td>"
            + "<td>=&nbsp;null</td></tr>"
            + "</table>"
            + "* can be any character sequence.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getReturnType() {
        return String.class;
    }
}
