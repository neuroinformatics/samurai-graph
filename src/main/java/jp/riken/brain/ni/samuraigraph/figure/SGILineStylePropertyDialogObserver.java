package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;

public interface SGILineStylePropertyDialogObserver extends SGIPropertyDialogObserver, SGILineStyleDialogObserver {

	public SGData getData();
	
	public String getName();
}
