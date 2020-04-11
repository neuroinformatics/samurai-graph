package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.util.List;

public interface SGIElementGroupSetVXY extends SGIElementGroupSetForData {
    /**
     * Returns a list of arrow groups.
     * 
     * @return a list of arrow groups
     */
    public List<SGElementGroupArrow> getArrowGroups();
    
    public SGElementGroupArrow getArrowGroup();
    
    public boolean setMagnitudePerCM(final float f);
    
    public boolean setDirectionInvariant(final boolean b);

    public static final String ELEMENT_GROUP_ARROW = "Arrow";

}
