package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIPlugin;
import jp.riken.brain.ni.samuraigraph.base.SGIPluginManager;
import jp.riken.brain.ni.samuraigraph.base.SGPopupMenu;
import jp.riken.brain.ni.samuraigraph.base.SGUserProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants;

public abstract class SGDataPopupMenu extends SGPopupMenu 
		implements SGILegendConstants {

	private static final long serialVersionUID = 7831256502597635653L;

	protected SGDrawingWindow mWnd;
	
	protected SGElementGroupSetForData mGroupSet;
	
	protected boolean mInGraph;

	public SGDataPopupMenu(final SGDrawingWindow wnd,
			final SGElementGroupSetForData gs, final boolean inGraph) {
		super();
		if (gs == null) {
			throw new IllegalArgumentException("gs == null");
		}
		if (wnd == null) {
			throw new IllegalArgumentException("wnd == null");
		}
		this.mWnd = wnd;
		this.mGroupSet = gs;
		this.mInGraph = inGraph;
		
		this.create();
	}
	
	protected abstract boolean checkDataType(final String dataType);
	
	private void create() {
        this.setBounds(0, 0, 100, 100);
        
        StringBuffer sb = new StringBuffer();
        sb.append("  -- Data: ");
        final int id = this.mGroupSet.getID();
        sb.append(id);
        sb.append(" ");
        sb.append(SGUtility.removeEscapeChar(this.mGroupSet.getName()));
        sb.append(" --");
        
        this.add(new JLabel(sb.toString()));
        this.addSeparator();
        
        List<List<PopupMenuItem>> commandList = this.getCommands();
        for (int ii = 0; ii < commandList.size(); ii++) {
            List<PopupMenuItem> list = commandList.get(ii);
            addCommands(this, list);
            if (list.size() != 0 && ii != commandList.size() - 1) {
                this.addSeparator();
            }
        }
	}

    /**
     * Returns all commands.
     *
     * @return the list of all commands
     */
    protected List<List<PopupMenuItem>> getCommands() {
    	SGData data = this.mGroupSet.getData();
    	
    	List<List<PopupMenuItem>> rootList = new ArrayList<List<PopupMenuItem>>();
    	
    	// arrange
    	List<String> moveCommandList = this.getMoveCommandList();
    	PopupMenuItem arrangeItem = new PopupMenuItem(MENUCMD_ARRANGE, this.mGroupSet);
    	for (int ii = 0; ii < moveCommandList.size(); ii++) {
    		String command = moveCommandList.get(ii);
    		arrangeItem.addChild(new PopupMenuItem(command, this.mGroupSet));
    	}
    	List<PopupMenuItem> arrangeItemList = new ArrayList<PopupMenuItem>();
    	arrangeItemList.add(arrangeItem);
    	rootList.add(arrangeItemList);

    	// edit
    	rootList.add(convertCommandToPopupMenuItem(this.getEditCommandList()));
        if (SGDataUtility.isNetCDFData(data)) {
            rootList.add(convertCommandToPopupMenuItem(this.getNetCDFCommandList()));
        } else if (SGDataUtility.isMDArrayData(data)) {
            rootList.add(convertCommandToPopupMenuItem(this.getMDArrayCommandList()));
        }

    	// operation
    	rootList.add(this.getOperationCommandList(this.mGroupSet));

    	// plug-in
    	ActionListener l = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String command = e.getActionCommand();
    			mDataPluginManager.execCommand(command, mWnd);
    		}
    	};
        List<PopupMenuItem> pluginItemList = new ArrayList<PopupMenuItem>();
        if (mDataPluginList != null && mDataPluginList.size() != 0) {
        	for (SGIPlugin lib : mDataPluginList) {
        		String text = SGUtility.createPluginItemString(lib);
        		if (text == null) {
        			continue;
        		}
        		String cmd = lib.getCommand();
        		PopupMenuItem item = new PopupMenuItem(text, cmd, l, true);
        		pluginItemList.add(item);
        	}
        } else {
        	String cmd = SGIPluginManager.NO_PLUGIN;
    		PopupMenuItem item = new PopupMenuItem(cmd, cmd, l, false);
    		pluginItemList.add(item);
        }
    	PopupMenuItem pluginTopItem = new PopupMenuItem(MENUCMD_PLUGIN, this.mGroupSet);
    	for (int ii = 0; ii < pluginItemList.size(); ii++) {
    		PopupMenuItem item = pluginItemList.get(ii);
    		pluginTopItem.addChild(item);
    	}
    	List<PopupMenuItem> pluginTopItemList = new ArrayList<PopupMenuItem>();
    	pluginTopItemList.add(pluginTopItem);
    	rootList.add(pluginTopItemList);

    	// export
    	rootList.add(convertCommandToPopupMenuItem(this.getExportCommandList()));
    	
    	// save properties into global attributes
        if (SGDataUtility.isNetCDFData(data)) {
    		String path = data.getPath();
    		if (path != null) {
            	File dataFile = new File(path);
            	if (dataFile.exists()) {
                	List<PopupMenuItem> list = new ArrayList<PopupMenuItem>();
                	list.add(convertCommandToPopupMenuItem(MENUCMD_ADD_COMMANDS_TO_NETCDF));
                    rootList.add(list);
            	}
    		}
        }
        
        // data viewer
        List<PopupMenuItem> dataViewerList = new ArrayList<PopupMenuItem>();
    	dataViewerList.add(convertCommandToPopupMenuItem(MENUCMD_SHOW_DATA_VIEWER));
    	rootList.add(dataViewerList);
    	
    	// property
    	rootList.add(convertCommandToPopupMenuItem(this.getPropertyCommandList()));
    	
    	return rootList;
    }
    
    private PopupMenuItem convertCommandToPopupMenuItem(String command) {
        return new PopupMenuItem(command, this.mGroupSet);
    }

    private List<PopupMenuItem> convertCommandToPopupMenuItem(List<String> commands) {
        List<PopupMenuItem> list = new ArrayList<PopupMenuItem>();
        for (String command : commands) {
            list.add(this.convertCommandToPopupMenuItem(command));
        }
        return list;
    }

    private static void addCommands(JPopupMenu p, List<PopupMenuItem> itemList) {
        for (int ii = 0; ii < itemList.size(); ii++) {
        	PopupMenuItem item = itemList.get(ii);
        	ActionListener l = item.getActionListener();
        	String text = item.getText();
        	String cmd = item.getActionCommand();
        	final boolean enabled = item.isEnabled();
        	if (item.childList.size() != 0) {
                JMenu menu = SGUtility.addMenu(p, l, text, enabled);
                for (int jj = 0; jj < item.childList.size(); jj++) {
                	PopupMenuItem cItem = item.childList.get(jj);
                	ActionListener cl = cItem.getActionListener();
                	String cText = cItem.getText();
                	String cCmd = cItem.getActionCommand();
                	final boolean cEnabled = cItem.isEnabled();
                	if (cItem instanceof CheckBoxPopupMenuItem) {
                    	SGUtility.addCheckBoxItem(menu, cl, cText, cCmd, cEnabled);
                	} else {
                    	SGUtility.addItem(menu, cl, cText, cCmd, cEnabled);
                	}
                }
        	} else {
        		if (item instanceof CheckBoxPopupMenuItem) {
                	SGUtility.addCheckBoxItem(p, l, text, cmd, enabled);
        		} else {
                    SGUtility.addItem(p, l, text, cmd, enabled);
        		}
        	}
        }
    }

    /**
     * Returns the commands to move data objects.
     *
     * @return the commands
     */
    protected List<String> getMoveCommandList() {
    	List<String> list = new ArrayList<String>();
    	if (this.mInGraph) {
        	list.add(MENUCMD_BRING_TO_FRONT);
        	list.add(MENUCMD_BRING_FORWARD);
        	list.add(MENUCMD_SEND_BACKWARD);
        	list.add(MENUCMD_SEND_TO_BACK);
    	} else {
	    	list.add(MENUCMD_MOVE_TO_TOP);
	    	list.add(MENUCMD_MOVE_TO_UPPER);
	    	list.add(MENUCMD_MOVE_TO_LOWER);
	    	list.add(MENUCMD_MOVE_TO_BOTTOM);
    	}
    	return list;
    }

    /**
     * Returns the commands to edit data objects.
     *
     * @return the commands
     */
    protected List<String> getEditCommandList() {
    	List<String> list = new ArrayList<String>();
    	list.add(MENUCMD_CUT);
    	list.add(MENUCMD_COPY);
    	list.add(MENUCMD_DUPLICATE);
    	list.add(MENUCMD_DELETE);
    	return list;
    }

    /**
     * Returns the commands to export data objects.
     *
     * @return the commands
     */
    protected List<String> getExportCommandList() {
    	List<String> list = new ArrayList<String>();
    	list.add(MENUCMD_EXPORT_TO_FILE);
    	
    	SGUserProperties prop = SGUserProperties.getInstance();
    	String devMode = prop.getProperty("dev");
    	Boolean bDevMode = Boolean.valueOf(devMode);
    	if (bDevMode) {
        	list.add(MENUCMD_EXPORT_TO_TEXT_FILE);
        	list.add(MENUCMD_EXPORT_TO_NETCDF_FILE);
        	list.add(MENUCMD_EXPORT_TO_HDF5_FILE);
        	list.add(MENUCMD_EXPORT_TO_MATLAB_FILE);
    	}
    	return list;
    }

    /**
     * Returns the commands for netCDF data objects.
     *
     * @return the commands
     */
    protected List<String> getNetCDFCommandList() {
    	List<String> list = new ArrayList<String>();
    	list.add(MENUCMD_INSERT_NETCDF_LABEL);
    	list.add(MENUCMD_ANIMATION);
    	return list;
    }

    /**
     * Returns the commands for multidimensional data objects.
     *
     * @return the commands
     */
    protected List<String> getMDArrayCommandList() {
    	List<String> list = new ArrayList<String>();
    	list.add(MENUCMD_ANIMATION);
    	return list;
    }

    /**
     * Returns the optional commands for data objects.
     *
     * @return the commands
     */
    protected List<PopupMenuItem> getOperationCommandList(ActionListener l) {
    	List<PopupMenuItem> list = new ArrayList<PopupMenuItem>();
    	SGData data = this.mGroupSet.getData();

    	PopupMenuItem fitAxisItem = new PopupMenuItem(MENUCMD_FIT_AXES_TO_DATA, this.mGroupSet);
    	List<PopupMenuItem> childList = this.getFitAxisChildList(this.mGroupSet);
    	for (PopupMenuItem item : childList) {
        	fitAxisItem.addChild(item);
    	}
    	if (data.isAnimationSupported()) {
			PopupMenuItem item = new PopupMenuItem(
					MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES, l);
			item.setEnabled(data.isAnimationAvailable());
			fitAxisItem.addChild(item);
    	}
    	list.add(fitAxisItem);

    	list.add(new PopupMenuItem(MENUCMD_TRANSFORM_DATA, this.mGroupSet));

    	return list;
    }

    /**
     * Returns the list of child menu items for axis fitting.
     *
     * @return the list of child menu items for axis fitting
     */
    protected abstract List<PopupMenuItem> getFitAxisChildList(ActionListener l);

    /**
     * Returns the commands for the plugins about data objects.
     *
     * @return the commands
     */
    protected List<PopupMenuItem> getPluginsCommandList() {
        List<PopupMenuItem> list = new ArrayList<PopupMenuItem>();

//        SGPluginsQueryMessage messageObject = new SGPluginsQueryMessage(SGPluginsQueryMessage.MENUCMD_OUTPUT_TO_FILE_IS_ENABLED);
//        this.notifyToListener(SGPluginsQueryMessage.MENUCMD_OUTPUT_TO_FILE_IS_ENABLED, messageObject);
//
//        boolean isOutputToFileEnabled = false;
//        Object result = messageObject.get();
//        if (result instanceof Boolean) {
//            isOutputToFileEnabled = ((Boolean)result).booleanValue();
//        }
//
//        list.add(new PopupMenuItem(MENUCMD_OUTPUT_TO_FILE, isOutputToFileEnabled));
        return list;
    }

    /**
     * Returns the commands for the properties of data objects.
     *
     * @return the commands
     */
    protected List<String> getPropertyCommandList() {
    	List<String> list = new ArrayList<String>();
    	list.add(MENUCMD_PROPERTY);
    	return list;
    }

    /**
     * Class of displayed string and its enabled for popup menu item.
     */
    protected static class PopupMenuItem {
        private final String text;
        private final String cmd;
        private boolean enabled;
        private ActionListener listener;
        private List<PopupMenuItem> childList = new ArrayList<PopupMenuItem>();
        PopupMenuItem(final String text, String cmd, ActionListener l,
        		final boolean enabled) {
        	super();
            this.text = text;
            this.cmd = cmd;
            this.listener = l;
            this.enabled = enabled;
        }
        PopupMenuItem(final String cmd, ActionListener l) {
        	this(cmd, cmd, l, true);
        }
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
        public void addChild(PopupMenuItem c) {
        	this.childList.add(c);
        }
        public String getText() {
        	return this.text;
        }
        public String getActionCommand() {
        	return this.cmd;
        }
        public boolean isEnabled() {
        	return this.enabled;
        }
        public ActionListener getActionListener() {
        	return this.listener;
        }
    }

    static class CheckBoxPopupMenuItem extends PopupMenuItem {
    	boolean selected;
    	CheckBoxPopupMenuItem(final String text, final String cmd, ActionListener l,
    			final boolean itemEnabled, final boolean selected) {
    		super(text, cmd, l, itemEnabled);
    		this.selected = selected;
        }
    	CheckBoxPopupMenuItem(final String cmd, ActionListener l, final boolean itemEnabled,
    			final boolean selected) {
    		this(cmd, cmd, l, itemEnabled, selected);
        }
    	public boolean isSelected() {
    		return this.selected;
    	}
    	public void setSelected(final boolean b) {
    		this.selected = b;
    	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	// do nothing
    }

    // The list of data plug-in.
    private static List<SGIPlugin> mDataPluginList = null;

    private static SGIPluginManager mDataPluginManager = null;

    /**
     * Sets the data plug-in manager.
     * 
     * @param l
     *           the data plug-in manager
     */
    public static void setDataPluginManager(SGIPluginManager m) {
    	mDataPluginManager = m;
    }

    /**
     * Sets the list of data plug-in.
     * 
     * @param pluginList
     *           the list of data plug-in
     */
    public static void setDataPlugins(List<SGIPlugin> pluginList) {
    	mDataPluginList = new ArrayList<SGIPlugin>(pluginList);
    }

}
