package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * The status bar.
 */
public class SGStatusBar extends JPanel implements SGIConstants,
        SGIProgressControl {

    /**
     *
     */
    private static final long serialVersionUID = -6426045627363525268L;

    private ProgressPanel mProgressPanel;

    private MessagePanel mMessagePanel;

    private PositionPanel mPositionPanal;
    
    private AxisValuePanel mAxisValuePanel;

    private boolean mSavedFlag = false;

    private String mMessage = "";

    private static final int STATUS_BAR_WIDTH = 18;

    private static final String STATUS_BAR_CHANGED_LABEL = "*";

    /**
     *
     */
    public SGStatusBar(SGDrawingWindow wnd) {
        super();

        // background
        this.setVisible(true);
        this.setOpaque(true);
        this.setBackground(wnd.getBackground());

        boolean isMacOSX = false;
        // boolean isWin32 = false;
        String laf = SGUtility.getLookAndFeelID();
        if (LAF_AQUA.equals(laf)) {
            isMacOSX = true;
        }
        // else if( LAF_WINDOWS.equals(laf)){
        // isWin32 = true;
        // }

        this.setBorder(new EmptyBorder(0, 0, 0, (isMacOSX ? 18 : 0)));

        // size
        this.setSize(0, STATUS_BAR_WIDTH);

        // inner
        ProgressPanel progPanel = new ProgressPanel();
        MessagePanel msgPanel = new MessagePanel();
        PositionPanel posPanel = new PositionPanel();
        AxisValuePanel axisValuePanel = new AxisValuePanel();

        this.setLayout(new java.awt.GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 2);
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        this.add(progPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 2);
        this.add(msgPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        this.add(axisValuePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        this.add(posPanel, gridBagConstraints);

        // set to attribute
        this.mProgressPanel = progPanel;
        this.mMessagePanel = msgPanel;
        this.mPositionPanal = posPanel;
        this.mAxisValuePanel = axisValuePanel;
    }

    /**
     * Starts the progress.
     *
     */
    @Override
    public void startProgress() {
        this.mProgressPanel.startProgress();
    }

    /**
     * Stops the progress.
     * 
     */
    @Override
    public void endProgress() {
        this.mProgressPanel.endProgress();
        this.setMessage("");
    }

    /**
     * Starts the progress in indeterminate mode.
     * 
     */
    @Override
    public void startIndeterminateProgress() {
    	this.mProgressPanel.startIndeterminateProgress();
    }

    /**
     * Sets the messsage of the progress.
     * 
     * @param msg
     *          progress messasge
     */
    @Override
    public void setProgressMessage(final String msg) {
        this.setMessage(msg);
    }
    
    /**
     * Sets the value of progress between 0 and 1.
     * 
     * @param ratio
     *          progress ratio
     */
    @Override
    public void setProgressValue(final float ratio) {
        this.mProgressPanel.setProgressValue(ratio);
    }
    
    void drawPosition(final float x, final float y) {
        final DecimalFormat df = new DecimalFormat("0.0");
        final String strPos = df.format(x) + " : " + df.format(y);
        this.mPositionPanal.setText(strPos);
        this.repaint();
    }
    
    void drawAxisValueString(final String str) {
    	this.mAxisValuePanel.setText(str);
    	this.repaint();
    }

    void setMessage(final String str) {
        String msg = str;
        if ("".equals(str)) {
            msg = (this.mSavedFlag) ? SGStatusBar.STATUS_BAR_CHANGED_LABEL : "";
        }
        this.mMessagePanel.setText(msg);
        this.mMessage = str;
        this.repaint();
    }

    void setSaved(final boolean b) {
        final String msg = (b) ? SGStatusBar.STATUS_BAR_CHANGED_LABEL : "";
        this.mSavedFlag = b;
        if ("".equals(this.mMessage)) {
            this.mMessagePanel.setText(msg);
        }
    }

    // A panel to display a message.
    private static class MessagePanel extends StatusPanel {
    	
        private static final long serialVersionUID = -6437538220874018805L;

        private MessagePanel() {
            super();
            this.setPreferredSize(new Dimension(120,
                    SGStatusBar.STATUS_BAR_WIDTH));
        }
    }

    // A panel to draw progress bar.
    private static class ProgressPanel extends StatusPanel {
    	
        private static final long serialVersionUID = 7911685209399341546L;

        private SGProgressBar mProgressBar = new SGProgressBar();

        private Timer mTimer = null;

        private float mCurrentValue = 0.0f;

        private boolean mEndFlag = true;

        ProgressPanel() {
            super();

            // size
            this.setPreferredSize(new Dimension(0, SGStatusBar.STATUS_BAR_WIDTH));

            // add the progress bar
            this.setLayout(new BorderLayout());
            this.add(this.mProgressBar, BorderLayout.CENTER);
            this.mProgressBar.setVisible(false);
        }

        void startProgress() {
            if (this.mEndFlag == false) {
            	// running now
                return;
            }
            this.mTimer = new Timer(100, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mProgressBar.setProgressValue(mCurrentValue);
        			onTimerActionPerformed(e);
                }
            });
            this.mProgressBar.setVisible(true);
            this.mEndFlag = false;
            this.mCurrentValue = 0.0f;
            if (this.mProgressBar.isIndeterminate()) {
                this.setIndeterminateProgress(false);
            }
            this.mTimer.start();
        }

        void endProgress() {
            if (this.mEndFlag == true) {
            	// already stopped
                return;
            }
            if (this.mProgressBar.isIndeterminate()) {
                this.setIndeterminateProgress(false);
            }
            this.mEndFlag = true;
            this.mProgressBar.setVisible(false);
            if (this.mTimer != null) {
                this.mTimer.stop();
                this.mTimer = null;
            }
        }

        void setProgressValue(final float ratio) {
        	if (this.mProgressBar.isIndeterminate()) {
        		// in the indeterminate mode, do nothing
        		return;
        	}
            this.mCurrentValue = ratio;
        }
        
        private void setIndeterminateProgress(final boolean mode) {
            this.mProgressBar.setIndeterminate(mode);
        }
        
        void startIndeterminateProgress() {
            if (this.mEndFlag == false) {
            	// running now
                return;
            }
            this.mProgressBar.setVisible(true);
            this.mEndFlag = false;
        	this.setIndeterminateProgress(true);
        	this.mTimer = new Timer(100 , new ActionListener() {
        		private int cnt = 0;
        		public void actionPerformed(ActionEvent e) {
        			mProgressBar.setValue(cnt);
        			onTimerActionPerformed(e);
        			cnt++;
        		}
        	});
        	this.mTimer.start();
        }
        
        private void onTimerActionPerformed(ActionEvent e) {
            mProgressBar.paintImmediately(mProgressBar.getVisibleRect());
            if (mEndFlag) {
            	if (this.mTimer != null) {
                    mTimer.stop();
            	}
                mProgressBar.initProgressValue();
            }
        }
    }

    // A panel to display mouse position.
    private static class PositionPanel extends StatusPanel {
    	
        private static final long serialVersionUID = 4728894476527846504L;

        private PositionPanel() {
            super();

            // size
            this.setPreferredSize(new Dimension(80,
                    SGStatusBar.STATUS_BAR_WIDTH));

            // string
            this.mString = "0.0 : 0.0";
        }
    }
    
    private static class AxisValuePanel extends StatusPanel {
    	
		private static final long serialVersionUID = 5955430387128507961L;

		private AxisValuePanel() {
    		super();
    		
            // size
            this.setPreferredSize(new Dimension(240,
                    SGStatusBar.STATUS_BAR_WIDTH));

            // string
            this.mString = "";
    	}
    }

    // The super class of the panels in the status bar.
    private static class StatusPanel extends JPanel {
    	
        private static final long serialVersionUID = 4682736556541214505L;

        protected String mString = "";

        private static final int FONT_SIZE = 12;

        private StatusPanel() {
            super();

            // border
            this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

            // font
            this.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            final int size = FONT_SIZE;
            g2d.drawString(this.mString, size / 2, size + 1);
        }

        protected void setText(final String str) {
            this.mString = str;
        }
    }

}
