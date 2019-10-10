package jp.riken.brain.ni.samuraigraph.figure.java2d;


public interface SGIElementGroupInGraph {
	
    public void setFocused(final boolean b);

    public boolean isFocused();

    public boolean setElementGroupSet(SGElementGroupSetInGraph gs);
    
//    public List<SGTuple2d> getFocusedShapeList(SGTuple2f[] points, int[] pointIndexArray);
}
