package jp.riken.brain.ni.samuraigraph.base;

/**
 * A base interface which is set properties with the property dialog.
 * 
 */
public interface SGIPropertyDialogObserver {

    /**
     * Commit the change with the dialog.
     * 
     * @return
     */
    public boolean commit();

    /**
     * Cancel the change set with the dialog.
     * 
     * @return
     */
    public boolean cancel();

    /**
     * Preview with set properties with the dialog.
     * 
     * @return
     */
    public boolean preview();

    /**
     * Direct the observer to prepare to be set properties with the dialog.
     * 
     * @return
     */
    public boolean prepare();

    /**
     * Returns a property dialog of this object.
     * 
     * @return a property dialog
     */
    public SGPropertyDialog getPropertyDialog();

}
