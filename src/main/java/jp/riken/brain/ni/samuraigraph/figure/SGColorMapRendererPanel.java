package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;

/**
 * A panel to render overview of the color map.
 *
 */
public class SGColorMapRendererPanel extends JPanel {
    
	private static final long serialVersionUID = 1625285220004904267L;

	/**
     * Color map manager.
     */
    private SGColorMapManager mColorMapManager = null;
    
    private String mColorMapName = null;
    
    private boolean mAvailable = true;
    
    /**
     * Default constructor.
     */
    public SGColorMapRendererPanel(SGColorMapManager man) {
        super();
        if (man == null) {
        	throw new IllegalArgumentException("man == null");
        }
        this.setOpaque(false);
        this.mColorMapManager = man;
    }

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	if (this.mAvailable) {
        	if (this.mColorMapName != null) {
            	SGColorMap colorMap = this.mColorMapManager.getColorMap(this.mColorMapName);
            	if (colorMap != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    Rectangle2D bounds = this.getBounds();
                    final double width = bounds.getWidth();
                    final int nWidth = (int) width;
                    final int nHeight = (int) bounds.getHeight();
                    for (int ii = 0; ii < nWidth; ii ++) {
                        final double ratio = (double) ii / nWidth;
                        Rectangle rect = new Rectangle(ii, 0, 1, nHeight);
                        Color cl = colorMap.evaluate(ratio);
                        g2d.setPaint(cl);
                        g2d.fill(rect);
                    }
            	}
        	}
    	}
    }
    
    /**
     * Returns the flag whether this component is available.
     * 
     * @return true if this component is available
     */
    public boolean isAvailable() {
    	return this.mAvailable;
    }
    
    /**
     * Sets the flag whether this component is available.
     * 
     * @param b
     *          a flag whether this component is available
     */
    public void setAvailable(final boolean b) {
    	this.mAvailable = b;
    }

    /**
     * Sets the name of color map.
     * 
     * @param name
     *           the name of color map
     */
    public void setColorMapName(String name) {
    	this.mColorMapName = name;
    }
    
    public void setColors(Color[] colors) {
    	if (this.mColorMapName != null && colors != null) {
        	SGColorMap colorMap = this.mColorMapManager.getColorMap(this.mColorMapName);
        	if (colorMap != null) {
        		colorMap.setColors(colors);
        	}
    	}
    }
    
    /**
     * Sets whether the color map is reversed.
     * 
     * @param b
     *           true to set to be reversed
     */
    public void setReversedOrder(final Boolean b) {
    	if (this.mColorMapName != null && b != null) {
        	SGColorMap colorMap = this.mColorMapManager.getColorMap(this.mColorMapName);
        	if (colorMap != null) {
        		colorMap.setReversedOrder(b);
        	}
    	}
    }
}
