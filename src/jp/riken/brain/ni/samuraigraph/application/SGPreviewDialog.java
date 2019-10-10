package jp.riken.brain.ni.samuraigraph.application;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Line2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import jp.riken.brain.ni.samuraigraph.base.SGButton;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;

/**
 * Preview dialog on exporting to the files.
 */
public final class SGPreviewDialog extends SGDialog implements AdjustmentListener {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 5440130689425050199L;

    /**
     * The OK buttton.
     */
    private JButton mOKButton;

    /**
     * The cancel button.
     */
    private JButton mCancelButton;

    /**
     * A scroll pane object.
     */
    private JScrollPane mScrollPane;

    /**
     * Default width of this dialog.
     */
    public static final int DEFAULT_WIDTH = 600;

    /**
     * Default height of this dialog.
     */
    public static final int DEFAULT_HEIGHT = 500;

    /**
     * The unit increment of the scroll bar.
     */
    private static final int SCROLL_BAR_UNIT_INCREMENT = 20;
    
    /**
     * The block increment of the scroll bar.
     */
    private static final int SCROLL_BAR_BLOCK_INCREMENT = 4 * SCROLL_BAR_UNIT_INCREMENT;

    /**
     * The default constructor.
     */
    public SGPreviewDialog() {
        super();
        this.initComponents();
    }

    /**
     * Builds a preview dialog.
     * @param owner
     *              the owner of this dialog
     * @param title
     *              the title of this dialog
     * @param modal
     *              true if modal
     */
    public SGPreviewDialog(final Dialog owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        this.initComponents();
    }

    /**
     * Builds a preview dialog.
     * @param owner
     *              the owner of this dialog
     * @param title
     *              the title of this dialog
     * @param modal
     *              true if modal
     */
    public SGPreviewDialog(final Frame owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        this.initComponents();
    }

    /**
     * Initialize this object.
     */
    private void initComponents() {
        
        // create buttons
        this.mOKButton = new SGButton(OK_BUTTON_TEXT);
        this.mCancelButton = new SGButton(CANCEL_BUTTON_TEXT);
        
        // create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.mOKButton);
        buttonPanel.add(this.mCancelButton);
        buttonPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        this.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        // set action listener
        this.mOKButton.addActionListener(this);
        this.mCancelButton.addActionListener(this);

        this.setResizable(true);
        this.setVisible(false);
    }

    /**
     * Called when the escape key is typed.
     *
     */
    protected void onEscKeyTyped() {
        this.setVisible(false);
        this.setCloseOption(CANCEL_OPTION);
    }

    /**
     * Paint scroll bars and the corner after painted subcomponents.
     */
    public void paint(Graphics g) {
        super.paint(g);
        JScrollPane sPane = this.mScrollPane;
        if (sPane != null) {
            Component corner = sPane
                    .getCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER);
            if (corner != null) {
                corner.repaint();
            }
            sPane.getHorizontalScrollBar().repaint();
            sPane.getVerticalScrollBar().repaint();
        }
    }

	/**
	 * Sets the target component.
	 * 
	 * @param target
	 *            the target component
	 * @param w
	 *            width of the image
	 * @param h
	 *            height of the image
	 * @param paperColor
	 *            color of the paper
	 * @return true if succeeded
	 */
    public boolean setTargetObject(Component target, final int w, final int h,
            final Color paperColor) {

        // remove the old component
        if (this.mScrollPane != null) {
            this.getContentPane().remove(this.mScrollPane);
        }

        // paper panel
        PaperPanel pp = new PaperPanel(paperColor, w, h);
        pp.add(target);

        // background panel
        BackgroundPanel bp = new BackgroundPanel(pp);
        bp.add(pp);

        // add to the scroll pane
        JPanel p = new JPanel();
        p.add(bp);
        this.mScrollPane = new JScrollPane(p);
        this.mScrollPane.setBorder(null);
        
        // set up the scroll bar properties
        JScrollBar vBar = this.mScrollPane.getVerticalScrollBar();
        vBar.setUnitIncrement(SCROLL_BAR_UNIT_INCREMENT);
        vBar.setBlockIncrement(SCROLL_BAR_BLOCK_INCREMENT);
        vBar.addAdjustmentListener(this);
        JScrollBar hBar = this.mScrollPane.getHorizontalScrollBar();
        hBar.setUnitIncrement(SCROLL_BAR_UNIT_INCREMENT);
        hBar.setBlockIncrement(SCROLL_BAR_BLOCK_INCREMENT);
        hBar.addAdjustmentListener(this);

        // add corner
        JPanel corner = new JPanel();
        corner.setOpaque(true);
        corner.setBackground(this.getBackground());
        this.mScrollPane.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER,
                corner);

        // add to the content pane
        this.getContentPane().add(this.mScrollPane, BorderLayout.CENTER);

        return true;
    }

    /**
     * Sets the text of the "OK" button.
     * @param text
     *             a text string to be set
     */
    public void setOKButtonText(final String text) {
        this.mOKButton.setText(text);
    }

    /**
     * Sets the text of the "Cancel" button.
     * @param text
     *             a text string to be set
     */
    public void setCancelButtonText(final String text) {
        this.mCancelButton.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        String command = e.getActionCommand();
        if (command.equals(this.mOKButton.getText())) {
            this.setCloseOption(OK_OPTION);
            this.setVisible(false);
        } else if (command.equals(this.mCancelButton.getText())) {
            this.setCloseOption(CANCEL_OPTION);
            this.setVisible(false);
        }
    }

    /**
     * Called when scroll bars move.
     * @param e
     *          an adjustment event
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // repaint this dialog
        this.repaint();
    }
    
    /**
     * A panel to draw the paper.
     */
    private static class PaperPanel extends JPanel {
        
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = -706564932789175016L;

        /**
         * The rectangle of the paper.
         */
        private Rectangle mRect;

        /**
         * Background Color.
         */
        private Color mColor;

        /**
         * Builds the paper panel
         * @param cl
         *           the background color
         * @param rect
         *           the rectangle of the paper
         */
        PaperPanel(Color cl, final int w, final int h) {
            super();
            this.mColor = cl;
            this.mRect = new Rectangle(0, 0, w, h);
            this.setPreferredSize(new Dimension(w, h));
            this.setLayout(null);
            this.setOpaque(true);
            this.setBackground(Color.WHITE);
        }

        /**
         * Paint the paper.
         * @param g
         *          graphic context
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if (this.mColor != null) {
                g2d.setPaint(this.mColor);
                g2d.fill(this.mRect);
            }
        }
    }

    /**
     * A panel to draw the shade of the paper.
     */
    private static class BackgroundPanel extends JPanel {
        
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = -4100869823393295998L;

        private Component mComponent;

        /**
         * Builds the background panel.
         * @param com
         */
        public BackgroundPanel(Component com) {
            super();
            this.mComponent = com;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            if (this.mComponent != null) {
                g2d.setPaint(Color.BLACK);

                Rectangle rect = this.mComponent.getBounds();
                final int lineWidth = 4;
                g2d.setStroke(new BasicStroke(lineWidth));

                final int x = rect.x + rect.width + lineWidth / 2;
                Line2D line0 = new Line2D.Float(x, rect.y + 1.5f * lineWidth,
                        x, rect.y + rect.height + lineWidth / 2);
                final int y = rect.y + rect.height + lineWidth / 2;
                Line2D line1 = new Line2D.Float(rect.x + 1.5f * lineWidth, y,
                        rect.x + rect.width + lineWidth / 2, y);
                g2d.draw(line0);
                g2d.draw(line1);

                g2d.setStroke(new BasicStroke(2));
                g2d.draw(rect);

            }
        }
    }

}
