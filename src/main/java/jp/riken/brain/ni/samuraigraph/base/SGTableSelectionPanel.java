
package jp.riken.brain.ni.samuraigraph.base;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

/**
 * This panel has a table and a text area. Information of selected row is displayed in the text area.
 *
 */
public abstract class SGTableSelectionPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 8246986947494258210L;

    /** Creates new form SGAttributePanel */
    public SGTableSelectionPanel() {
        super();
        this.initProperty();
    }

    /**
     * The table model.
     */
    protected SelectionTableModel mTableModel = null;

    private void initProperty() {
    }

    /**
     * Initializes the table model.
     */
    protected void initTableModel() {
        this.mTableModel = this.createTableModelInstance();
        this.mTableModel.setColumnIdentifiers(this.getColumnNameArray());
        this.getTable().setModel(this.mTableModel);
    }
    
    /**
     * Update the table.
     */
    public void updateTable() {
        this.setPreferredColumnWidth();
    }

    /**
     * Set preferred size of columns.
     */
    private void setPreferredColumnWidth() {
        String[] cArray = getColumnNameArray();
        int[] pArray = getPreferredColumnWidthArray();
        JTable table = this.getTable();
        for (int ii = 0; ii < cArray.length; ii++) {
            TableColumn cl = table.getColumn(cArray[ii]);
            cl.setPreferredWidth(pArray[ii]);
        }
    }

    protected abstract String[] getColumnNameArray();
    
    protected abstract int[] getPreferredColumnWidthArray();

    /**
     * Clear all data.
     *
     */
    public void clear() {
        this.initTableModel();
    }

    protected abstract JTable getTable();
    
    protected abstract JTextComponent getTextComponent();
    
    protected abstract SelectionTableModel createTableModelInstance();

    /**
     * Abstract table model class.
     *
     */
    protected static abstract class SelectionTableModel extends DefaultTableModel {

        private static final long serialVersionUID = -6469371272912213709L;
        
        /**
         * The default constructor.
         *
         */
        protected SelectionTableModel() {
            super();
        }

        /**
         * Always returns false.
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
	}

    /**
     * Called when the mouse press event is invoked.
     *
     * @param e mouse event
     */
	@Override
    public void mousePressed(MouseEvent e) {
        Object source = e.getSource();
        if (source.equals(this.getTable())) {
            // updates the text area
        	this.updateTextArea();
        	
            // set the caret position
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    getTextComponent().setCaretPosition(0);
                }
            });
        }
    }
	
	protected abstract void updateTextArea();

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
