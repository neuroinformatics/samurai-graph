package jp.riken.brain.ni.samuraigraph.base;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class SGTable extends JTable {

	private static final long serialVersionUID = -165984398826074285L;

    /**
     * The height of each row.
     */
    public static final int ROW_HEIGHT = 20;

    /**
     * The default constructor.
     */
	public SGTable() {
		super();
		this.initProperty();
	}
	
	private void initProperty() {
        this.getTableHeader().setReorderingAllowed(false);
        
        // set the height of all rows
        this.setRowHeight(ROW_HEIGHT);
	}

	@Override
	public String getToolTipText(MouseEvent e){
		final int rowIndex = rowAtPoint(e.getPoint());
		final int colIndex = columnAtPoint(e.getPoint());
		Object obj = getModel().getValueAt(rowIndex, colIndex);
        if (obj != null && !"".equals(obj.toString())) {
			return obj.toString();
		} else {
			return null;
		}
    }
}
