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
 * A StringManipulator to search for a substring. This Manipulator has
 * various binary modifiers like 'ignore case' and 'right to left'.
 *
 * @author Heiko Hofer
 */
public class IndexOfModifiersManipulator implements Manipulator {

    /**
     * Gives the first index of toSearch in the string or -1 if toSearch is
     * not found.
     *
     * @param str the string
     * @param needle the character sequence to search
     * @param options string with binary options
     * @return the index of the first occurrence of needle in s
     */
    public static int indexOf(final CharSequence str,
            final CharSequence needle,
            final String options) {
        return KnimeStringUtils.indexOf(str, needle, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategory() {
        return "Search";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "indexOf";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return getName() + "(str, toSearch, modifiers)";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrArgs() {
        return 3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Gives the first position of <i>toSearch</i> in the string. "
        + " <i>modifiers</i> gives several options "
        + "to control the search:"
        + "<br/>"
        + "<table style=\"padding: 0px 0px 0px 5px;\">"
        + "<tr><td style=\"padding: 0px 8px 0px 0px;\">i</td> "
        + "<td>ignore case</td></tr>"
        + "<tr><td style=\"padding: 0px 8px 0px 0px;\">b</td> "
        + "<td>backward search</td></tr>"
        + "<tr><td style=\"padding: 0px 8px 0px 0px;\">w</td> "
        + "<td>whole word (word boundaries are "
        + "whitespace characters)</td></tr>"
        + "</table>"
        + ""
        + "<br/>"
        + "<strong>Examples:</strong>"
        + "<br/>"
        + "<table>"
        + "<tr><td>indexOf(\"abcABCabc\", \"ab\", \"\")</td>"
        + "<td>=&nbsp;0</td></tr>"

        + "<tr><td>indexOf(\"abcABCabc\", \"ab\", \"b\")</td>"
        + "<td>=&nbsp;6</td></tr>"

        + "<tr><td>indexOf(\"ab abAB AB\", \"ab\", \"w\")</td>"
        + "<td>=&nbsp;0</td></tr>"

        + "<tr><td>indexOf(\"ab abAB AB\", \"AB\", \"w\")</td>"
        + "<td>=&nbsp;8</td></tr>"

        + "<tr><td>indexOf(\"ab abAB AB\", \"abAB\", \"w\")</td>"
        + "<td>=&nbsp;3</td></tr>"

        + "<tr><td>indexOf(\"ab abAB AB\", \"abab\", \"iw\")</td>"
        + "<td>=&nbsp;3</td></tr>"

        + "<tr><td>indexOf(\"\", \"\", \"\")</td>"
        + "<td>=&nbsp;0</td></tr>"

        + "<tr><td>indexOf(\"\", *, \"\")</td>"
        + "<td>=&nbsp;-1 (except when * = \"\")</td></tr>"

        + "<tr><td>indexOf(null, *, \"\")</td>"
        + "<td>=&nbsp;-1 (except when * = \"\")</td></tr>"

        + "<tr><td>indexOf(\"\", null, \"\")</td>"
        + "<td>=&nbsp;-1 (except when * = \"\")</td></tr>"

        + "<tr><td>indexOf(\"\", *, null)</td>"
        + "<td>=&nbsp;-1 (except when * = \"\")</td></tr>"
        + "</table>"
        + "* can be any character sequence.<br/>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getReturnType() {
        return Integer.class;
    }
}
