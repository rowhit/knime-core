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
package org.knime.base.node.viz.plotter2D;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.knime.core.data.property.ColorAttr;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;

import org.knime.base.util.coordinate.CoordinateMapping;

/**
 * Implements the ScrollPane including a scatter plot of data points.
 * 
 * @author Christoph Sieb, University of Konstanz
 * @author ohl University of Konstanz
 */
public abstract class AbstractPlotter2D extends JPanel implements
        ActionListener, MouseListener, MouseMotionListener, HiLiteListener,
        ComponentListener {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(AbstractPlotter2D.class);

    /** Popup menue entry constant. */
    public static final String POPUP_HILITE_SELECTED = 
        HiLiteHandler.HILITE_SELECTED;

    /** Popup menue entry constant. */
    public static final String POPUP_UNHILITE_SELECTED = 
        HiLiteHandler.UNHILITE_SELECTED;

    /** Popup menue entry constant. */
    public static final String POPUP_CLEAR_HILITED = HiLiteHandler.CLEAR_HILITE;

    /** Popup menue entry constant. */
    public static final String POPUP_FADE = "Fade UnHiLited";

    /** Popup menu entry for hide unhilited data points. */
    public static final String POPUP_HIDE = "Hide UnHiLited";

    /** Popup menue entry constant. */
    public static final String POPUP_CANCEL = "Cancel";

    
    // this is where we draw the data.
    private final AbstractDrawingPane m_drawingPane;
    

    /**
     * The properties panel for the scatter plot. To adjust zoom levels and get
     * its size
     */
    private final PlotterPropertiesPanel m_properties;

    private JScrollPane m_scroller;

    private Header m_colHeader;

    private Header m_rowHeader;

    private int m_width;

    private int m_height;

    private final int m_initWidth; // the width at zoom factor one

    private final int m_initHeight; // the height at zoom factor one

    private String m_xColName;

    private String m_yColName;

    private boolean m_crosshair;

//    private JMenu m_hiliteMenu;

    /**
     * If in zoom mode a selection in the drawing pane will result in zooming
     * instead of selecting items.
     */
    private boolean m_zoomMode;

    /**
     * Holds an instatnce of a zoom cursor. Just created once
     */
    private Cursor m_zoomCursor;

    /**
     * Creates a new instance of a AbstractPlotter2D.
     * 
     * @param initialWidth The width at zoom 1x.
     * @param propertiesPanel pthe scatterplot properties associated with this
     *            scatterplot
     * @param pane The underlying pane where the points are drawn in.
     */
    public AbstractPlotter2D(final int initialWidth,
            final PlotterPropertiesPanel propertiesPanel,
            final AbstractDrawingPane pane) {
        if (propertiesPanel == null) {
            throw new IllegalArgumentException(PlotterPropertiesPanel.class
                    .getName()
                    + " shouldn't be null.");
        }
        if (pane == null) {
            throw new IllegalArgumentException(AbstractDrawingPane.class
                    .getName()
                    + " shouldn't be null.");
        }

        m_properties = propertiesPanel;

        m_properties.setPlotter(this);

        // set them null - to indicate user didn't set em yet.
        m_xColName = null;
        m_yColName = null;

        // BoxLayout is the only layout that honours max/preferred size,
        // which makes our stuff look right
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

//        // Set up the area of the drawing area.
        if (m_properties != null) {
            m_initWidth = m_properties.getPreferredSize().width;
            // - AUTOFIT_WIDTH_OFFSET;
        } else {
            m_initWidth = initialWidth;
        }

//        m_initWidth = initialWidth;
        m_initHeight = m_initWidth;
        m_width = m_initWidth;
        m_height = m_initHeight;
        

        // our thing to draw in
        m_drawingPane = pane;

        m_drawingPane.setBackground(ColorAttr.BACKGROUND);
        m_drawingPane.addMouseListener(this);
        m_drawingPane.addMouseMotionListener(this);

        
        // Put the drawing area in a scroll pane.
        m_scroller = new Plotter2DScrollPane(m_drawingPane);

        m_colHeader = new Header(Header.HORIZONTAL, m_width);
        m_rowHeader = new Header(Header.VERTICAL, m_height);

        m_scroller.setColumnHeaderView(m_colHeader);
        m_scroller.setRowHeaderView(m_rowHeader);
        

        add(m_scroller);
        add(m_properties);
        
        adjustSizes();

        m_crosshair = true; // by default paint a crosshair cursor
        addComponentListener(this);
        if (getParent() != null) {
            getParent().addComponentListener(this);
        }
        
    }

//    /**
//     * Sets the hilite menue to use if hiliting is supported.
//     * 
//     * @param hiliteMenu the hilite menu to use
//     */
//    protected void setHiliteMenu(final JMenu hiliteMenu) {
//
//        m_hiliteMenu = hiliteMenu;
//    }

    /**
     * @see java.awt.Component#setBackground(java.awt.Color)
     */
    @Override
    public void setBackground(final Color bg) {
        super.setBackground(bg);
        if (m_drawingPane != null) {
            m_drawingPane.setBackground(bg);
        }
    }

    /**
     * Triggers the drawing of the grid.
     * 
     * @param x if true the x grid is drawn.
     * @param y if true the y grid is drawn.
     */
    public void showGrid(final boolean x, final boolean y) {
        if (x) {
           CoordinateMapping[] positions = m_colHeader.getCoordinate()
               .getTickPositions(m_colHeader.getPreferredSize().width, true);
           for (CoordinateMapping coord : positions) {
               assert coord == coord;
           }
        }
        assert y == y;

    }

    /**
     * Sets the dimension of the drawing pane to the passed width and height.
     * Depending on the size of the drawing pane in the scroll pane and the
     * borders it sets the preferred and maximum size, revalidates and repaints.
     */
    protected void adjustSizes() { 
        m_drawingPane.setPreferredSize(new Dimension(m_width, m_height));
        m_rowHeader.setPreferredLength(m_height);
        m_colHeader.setPreferredLength(m_width);
        updatePaintModel();
        if (!m_zoomMode) {
        m_scroller.getViewport().setPreferredSize(new Dimension(
                m_drawingPane.getPreferredSize().width
                + m_scroller.getVerticalScrollBar().getPreferredSize().width,
                m_drawingPane.getPreferredSize().height 
                + m_scroller.getHorizontalScrollBar().getPreferredSize().height
                ));  
        setSize(new Dimension(
                m_scroller.getPreferredSize().width,
                m_scroller.getPreferredSize().height
//                + m_properties.getHeight()));
                + m_properties.getPreferredSize().height));
        }
//        invalidate();
        
    }
    
    /**
     * Adapts the plotter size to the available space of the window and adjusts
     * the appropriate zoom level.
     * 
     * @param enlargeOnly if true fitting is just performed if this will result
     *            in an enlargement of the plotter
     */
    void fitToScreen(final boolean enlargeOnly) {
        boolean changed = false;
        int newWidth = m_scroller.getViewport().getWidth();
        int newHeight = m_scroller.getViewport().getHeight();
        if (m_width == newWidth && m_height == newHeight) {
            return;
        }       
//        resizeProperties();
        // if enlarge only is true check if the new values are smaler
        Dimension newDim;
        if (enlargeOnly) {
            if (m_width < newWidth) {
                m_width = newWidth;
                newDim = new Dimension(
                        m_width, m_drawingPane.getPreferredSize().height);
                m_drawingPane.setPreferredSize(newDim);
                m_colHeader.setPreferredLength(m_width);
                changed = true;
            }

            if (m_height < newHeight) {
                m_height = newHeight;
                newDim = new Dimension(
                        m_drawingPane.getPreferredSize().width,
                        m_height);
                m_drawingPane.setPreferredSize(newDim);
                m_rowHeader.setPreferredLength(m_height);
                changed = true;
            }
        } else {
            m_width = newWidth;
            m_height = newHeight;
            newDim = new Dimension(m_width, m_height);
            m_drawingPane.setPreferredSize(newDim);
            m_colHeader.setPreferredLength(m_width);
            m_rowHeader.setPreferredLength(m_height);
            changed = true;
        }
        if (changed) {
            LOGGER.debug("changed");
            m_scroller.getViewport().revalidate(); 
            updatePaintModel();
            repaint();
        }        
    }
    
    
    /**
     * 
     *
     */
    public void resizeProperties() {
        if (getWidth() == m_properties.getPreferredSize().width) {
            return;
        }
        Component[] comps = m_properties.getComponents();
        if (getWidth() < m_properties.getPreferredSize().width) {
            LOGGER.debug("arrage props at Y axis");
            m_properties.removeAll();
            m_properties.setLayout(new BoxLayout(m_properties, 
                    BoxLayout.Y_AXIS));
            for (Component comp : comps) {
                m_properties.add(comp);
                m_properties.revalidate();
                m_properties.add(Box.createVerticalGlue());
            }
        } else {
            LOGGER.debug("arrange props at X axis");
            m_properties.removeAll();
            m_properties.setLayout(new BoxLayout(m_properties, 
                    BoxLayout.X_AXIS));
            for (Component comp : comps) {
                m_properties.add(comp);
                m_properties.revalidate();
                m_properties.add(Box.createHorizontalGlue());
            }
        }

        revalidate();
    }

    /**
     * This method is invoked in case the sub class should update the model to
     * draw in the <code>AbstractDrawingPane</code>.
     */
    protected abstract void updatePaintModel();

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(final MouseEvent e) {
        if (m_crosshair) {
            m_drawingPane.setCursorCoord(e.getX(), e.getY());

            // change the mouse pointer if zoom mode
            if (m_zoomMode) {

                setCursor(getZoomCursor());
            }

            m_drawingPane.repaint();
        }
    }

    /**
     * Returns the zoom mouse cursor. If not created yet it is created.
     * 
     * @return the zoom cursor
     */
    private Cursor getZoomCursor() {

        if (m_zoomCursor == null) {

            ImageIcon zoomCursorImage = getZoomIcon();
            m_zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    zoomCursorImage.getImage(), new Point(0, 0), "Zoom");
        }

        return m_zoomCursor;
    }

    /**
     * Returns the image icon intended for zoom representation.
     * 
     * @return the zoom icon
     */
    static ImageIcon getZoomIcon() {
        return new ImageIcon(AbstractPlotter2D.class
                .getResource("zoomInCursor.gif"));
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(final MouseEvent e) {
        if (m_crosshair) {
            m_drawingPane.clearCursorCoord();

            // set the cursor back
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            repaint();
        }
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(final MouseEvent e) {

        // // if another button is already pressed do not process this click
        // if (m_drawingPane.mouseDownSet()) {
        // return;
        // }

        if (e.getButton() == MouseEvent.BUTTON1
                || e.getButton() == MouseEvent.BUTTON3) {
            m_drawingPane.setMouseDown(e.getX(), e.getY());

            if (e.getButton() == MouseEvent.BUTTON3) {
                m_drawingPane.setRightMouseDown(true);
            } else {
                m_drawingPane.setRightMouseDown(false);
            }
        }
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged
     *      (java.awt.event.MouseEvent)
     */
    public void mouseDragged(final MouseEvent e) {
        // only with mouse down coord we drag (btw. getButton doesn't work
        // here properly (with Windows at least)
        if (m_drawingPane.mouseDownSet()) {
            m_drawingPane.setDragCoord(e.getX(), e.getY());
            m_drawingPane.scrollRectToVisible(new Rectangle(e.getX(), e.getY(),
                    1, 1));
            repaint();
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased
     *      (java.awt.event.MouseEvent)
     */
    public void mouseReleased(final MouseEvent e) {
        if (m_drawingPane.mouseDownSet()) {
            int xPos = e.getX();
            int yPos = e.getY();

            // in case the right button was pressed only
            // process the zoom event if the drag size is larger than 1 pixel in
            // each direction. Otherwise open the popup menue.
            if (m_drawingPane.isRightMouseDown() || m_zoomMode) {

                int difX = Math.abs(m_drawingPane.getMouseDownX() - xPos);
                int difY = Math.abs(m_drawingPane.getMouseDownY() - yPos);
                if (difX <= 3 && difY <= 3) {
                    // open popup menue
                    if (m_drawingPane.isRightMouseDown()) {
                        popupMenu(e);
                    }
                } else { // handle the zoom event

                    // see if this is a drag or a click event (Linux triggers
                    // down/release even when clicking - which is a bit
                    // unfortunate...)
                    if (m_drawingPane.dragCoordSet()) {
                        m_drawingPane.setDragCoord(xPos, yPos);

                        // process zooming

                        doZoomingByZoomWindow();
                    }

                    m_drawingPane.clearDragTangle();
                    validate();
                    repaint();

                    // switch back to selection mode
                    m_properties.setZoomMode(false);
                }
            } else {

                // clear any old selection - unless the Ctrl key is pressed
                if (!e.isControlDown()) {
                    m_drawingPane.clearSelection();
                }

                // see if this is a drag or a click event (Linux triggers
                // down/release even when clicking - which is a bit
                // unfortunate...)
                if (m_drawingPane.dragCoordSet()) {
                    m_drawingPane.setDragCoord(xPos, yPos);
                    m_drawingPane.selectElementsInDragTangle(m_drawingPane
                            .getMouseDownX(), m_drawingPane.getMouseDownY(),
                            m_drawingPane.getDragX(), m_drawingPane.getDragY());
                } else {
                    m_drawingPane.toggleSelectionAt(xPos, yPos);
                }
                m_drawingPane.clearDragTangle();
                repaint();

//                // activate the appropriate menu items from the hilite menu
//                // if there is a hilite menu
//                if (m_hiliteMenu != null) {
//                    if (m_drawingPane.getNumberSelectedElements() > 0) {
//                        // index 0 is the "hilite selected" entry
//                        // index 1 is the "unhilite selected" entry
//                        m_hiliteMenu.getMenuComponent(0).setEnabled(true);
//                        m_hiliteMenu.getMenuComponent(1).setEnabled(true);
//                    } else {
//                        m_hiliteMenu.getMenuComponent(0).setEnabled(false);
//                        m_hiliteMenu.getMenuComponent(1).setEnabled(false);
//                    }
//                }

                // the clear hiliting entry is always activated
                // it could happen, that the plotter does not show
                // all data points but another view does
            }
        }
    }

    private void doZoomingByZoomWindow() {

        // to calculate the zoom factor it is neccessary to
        // determine the percentage of the zoom window to the
        // visible view port. This is because the zoom area is inteded to
        // fit exactly into the view port. The next step is to extend
        // the draw pane according to this percentage which results in the
        // appropriate size. Afterwards the only thing to do is to
        // adjust the scroll bars to the upper left position of the
        // zoom window.

        JViewport viewPort = m_scroller.getViewport();

        int zoomWindowWidth = Math.abs(m_drawingPane.getMouseDownX()
                - m_drawingPane.getDragX());

        int zoomWindowHeight = Math.abs(m_drawingPane.getMouseDownY()
                - m_drawingPane.getDragY());

        float widthZoomFactor = ((float)viewPort.getViewRect().width)
                / ((float)zoomWindowWidth);

        float heightZoomFactor = ((float)viewPort.getViewRect().height)
                / ((float)zoomWindowHeight);

        // adjust the draw pane ranges
        m_width = Math.round(m_width * widthZoomFactor);
        m_height = Math.round(m_height * heightZoomFactor);

        // adjust and repaint
        adjustSizes();

        // calculate the upper left corner corespondence in the adapted draw
        // pane. this is the original upper left point multiplied by the
        // zoom factor
        int upperLeftX = Math.min(m_drawingPane.getMouseDownX(), m_drawingPane
                .getDragX());

        int upperLeftY = Math.min(m_drawingPane.getMouseDownY(), m_drawingPane
                .getDragY());

        // adapt by zoom factors
        upperLeftX = Math.round(upperLeftX * widthZoomFactor);
        upperLeftY = Math.round(upperLeftY * heightZoomFactor);

        // reset the drawing pane to force the scroll pane to validate
        // its components (workaround, as validate() does not work)
        m_scroller.setViewportView(m_drawingPane);

        // set the viewport to this coordinates
        Rectangle recToVisible = new Rectangle(upperLeftX, upperLeftY, viewPort
                .getVisibleRect().width, viewPort.getVisibleRect().height);
        m_drawingPane.scrollRectToVisible(recToVisible);

        revalidate();
        repaint();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(final MouseEvent e) {
        // we don't do anything here. Linux triggers a Mouse down and release
        // event in addition to the clicked event. So we handle everything
        // in the mouse release.
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved
     *      (java.awt.event.MouseEvent)
     */
    public void mouseMoved(final MouseEvent e) {
        if (m_crosshair) { // && m_zoomMode) {
            m_drawingPane.setCursorCoord(e.getX(), e.getY());
            m_drawingPane.repaint();
        }
        
    }

    /**
     * This basically handles the right mouse click. A JMenu is created that can
     * be filled by the derived class via the <code>fillPopupMenu</code>
     * method.
     * 
     * @param e - the MouseEvent from the right-click of the mouse
     */
    protected void popupMenu(final MouseEvent e) {

        JPopupMenu menu = new JPopupMenu();

        // fill the menu by the derived class
        fillPopupMenu(menu);

        // check if there there are entries in the popup menu
        // if so, show the menu
        if (menu.getComponentCount() > 0) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Abstract method to be overwritten by the derived child class to fill the
     * popup menue on right mouse click. A class that derives from an already
     * implementing class must invoke <code>super.fillPopupMenu</code> to get
     * the menu items from the implementing parent class
     * 
     * @param menu the menu to fill with items
     */
    protected abstract void fillPopupMenu(JPopupMenu menu);

    /**
     * Creates a menue entry for Hiliting if not done so far. Has to be
     * implemented by the sub class.
     * 
     * @return - a JMenu entry handling the hiliting of objects.
     */
    public abstract JMenu getHiLiteMenu();
    

    /**
     * If true is passed a crosshair cursor will be painted in the
     * scatterplotter (causing a repaint each time the mouse is moved!!) Default
     * value is true.
     * 
     * @param enable set true to have a crosshair painted at the mouse pointer
     *            position, pass false to disable the crosshair cursor
     */
    public void setCrosshairCursorEnabled(final boolean enable) {
        m_crosshair = enable;
        if (!enable) {
            m_drawingPane.clearCursorCoord();
            repaint();
        }
    }

    /**
     * @return the current state of the flag deciding on whether a crosshair
     *         cursor is being painted or not.
     */
    public boolean isCrosshairCursorEnabled() {
        return m_crosshair;
    }

    /**
     * Be aware that the returned drawingPane is the original drawingPane, it's
     * not a copy. Never ever change data of that drawingPane. This method is
     * only to read the drawingPanes data like DotInfoArray.
     * 
     * @return Returns the m_drawingPane.
     */
    public AbstractDrawingPane getDrawingPane() {
        return m_drawingPane;
    }

    /**
     * @return Returns the <code>PlotterPropertiesPanel</code> with default
     *         methods to adjust zoom levels and get its size.
     */
    public PlotterPropertiesPanel getPropertiesPanel() {
        return m_properties;
    }

    /**
     * @param zoomMode true means to switch to zoom mode
     */
    void setZoomMode(final boolean zoomMode) {
        m_zoomMode = zoomMode;

        // if not zoom mode, switch cursor to default
        if (!m_zoomMode) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * @return the column header of this plotter
     */
    protected Header getColHeader() {
        return m_colHeader;
    }

    /**
     * @return the row header of this plotter
     */
    protected Header getRowHeader() {
        return m_rowHeader;
    }

    /**
     * If the component is resized, it is checked whether there is space to
     * expand the plotter.
     * 
     * @see ComponentListener#componentResized(ComponentEvent)
     */
    public void componentResized(final ComponentEvent e) {
        fitToScreen(true);
    }

    /**
     * Nothing done yet.
     * 
     * @see ComponentListener#componentMoved(ComponentEvent)
     */
    public void componentMoved(final ComponentEvent e) {
        // Not needed yet
    }

    /**
     * Nothing done yet.
     * 
     * @see ComponentListener#componentShown(ComponentEvent)
     */
    public void componentShown(final ComponentEvent e) { 
    }

    /**
     * Nothing done yet.
     * 
     * @see ComponentListener#componentHidden(ComponentEvent)
     */
    public void componentHidden(final ComponentEvent e) {
        // Not needed yet

    }

    /**
     * @return the x column name
     */
    public String getXColName() {
        return m_xColName;
    }

    /**
     * @return the y column name
     */
    public String getYColName() {
        return m_yColName;
    }

    /**
     * Sets the x column name.
     * 
     * @param colName the column name to set
     */
    protected void setXColName(final String colName) {
        m_xColName = colName;
    }

    /**
     * Sets the y column name.
     * 
     * @param colName the column name to set
     */
    protected void setYColName(final String colName) {
        m_yColName = colName;
    }

    /**
     * @return this plotters actual height
     * 
     * @see java.awt.Component#getHeight()
     */
    protected int getPlotterHeight() {
        return m_height;
    }

    /**
     * @return this plotters actual width
     * 
     * @see java.awt.Component#getWidth()
     */
    protected int getPlotterWidth() {
        return m_width;
    }

}
