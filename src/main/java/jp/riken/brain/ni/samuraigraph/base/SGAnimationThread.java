package jp.riken.brain.ni.samuraigraph.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * A class of the animation.
 * 
 */
public class SGAnimationThread extends Thread implements SGIDisposable,
		SGIAnimationConstants {

    /**
     * An object which has data sources of the animation.
     */
    protected SGIAnimation[] mAnimations = null;

    /**
     * A flag whether to play the animation.
     */
    protected boolean mPlayFlag = false;

    /**
     * The array of frame indices.
     */
    protected int[] mFrameIndexArray = new int[0];

    /**
     * A flag whether to play forward or backward.
     */
    protected boolean mForwardFlag = true;
    
    /**
     * The time interval.
     */
    protected long mTimeInterval = 1000L;
    
    /**
     * The number of animation frames.
     */
    private final int mFrameNumber;

    private static final long MIN_SLEEP_TIME = 10L;

    /**
     * Frame rate.
     */
    private double mFrameRate = FRAME_RATE_INIT;

    /**
     * A flag for loop playback .
     */
    private boolean mLoopPlaybackFlag = false;
    
    private SGIntegerSeriesSet mFrameIndices = null;

    /**
     * Current frame index.
     */
    private int mCurrentFrameIndex = 0;    

    /**
     * Builds an animation thread.
     * 
     * @param animation
     *           an animation data source
     */
    public SGAnimationThread(SGIAnimation[] animations) {
        super();
        if (animations == null) {
            throw new IllegalArgumentException("animations == null");
        }
        if (animations.length == 0) {
            throw new IllegalArgumentException("animations.length == 0");
        }
        this.mAnimations = animations.clone();
        final int num = animations.length;

        // checks the equality of frame number
        int[] frameNumberArray = new int[num];
        for (int ii = 0; ii < num; ii++) {
        	frameNumberArray[ii] = animations[ii].getFrameNumber();
        }
        Integer frameNumber = SGUtility.checkEquality(frameNumberArray);
        if (frameNumber == null) {
            throw new IllegalArgumentException("frameNumber == null");
        }
        this.mFrameNumber = frameNumber;
        
        int[] curFrameIndexArray = new int[num];
        for (int ii = 0; ii < num; ii++) {
        	curFrameIndexArray[ii] = animations[ii].getCurrentFrameIndex();
        }
        Integer curFrameIndex = SGUtility.checkEquality(curFrameIndexArray);
        final int curFrameIndexNew = (curFrameIndex != null) ? curFrameIndex : 0;
        this.setCurrentFrameIndex(curFrameIndexNew);
        
        // frame rate
        double[] frameRateArray = new double[num];
        for (int ii = 0; ii < num; ii++) {
        	frameRateArray[ii] = animations[ii].getFrameRate();
        }
        Double frameRateCommon = SGUtility.checkEquality(frameRateArray);
        final double frameRate = (frameRateCommon != null) ? frameRateCommon : 1.0;
        final long timeInterval = getTimeInterval(frameRate);
        this.mFrameRate = frameRate;
        this.mTimeInterval = timeInterval;
        
        // loop play back
        boolean[] loopPlaybackArray = new boolean[num];
        for (int ii = 0; ii < num; ii++) {
        	loopPlaybackArray[ii] = animations[ii].isLoopPlaybackAvailable();
        }
        Boolean loopPlaybackCommon = SGUtility.checkEquality(loopPlaybackArray);
        final boolean loopPlayBack = (loopPlaybackCommon != null) ? loopPlaybackCommon : false;
        this.mLoopPlaybackFlag = loopPlayBack;

        // frame indices
        SGIntegerSeriesSet[] indicesArray = new SGIntegerSeriesSet[num];
        for (int ii = 0; ii < num; ii++) {
        	indicesArray[ii] = animations[ii].getAnimationArraySection();
        }
        SGIntegerSeriesSet indicesCommon = (SGIntegerSeriesSet) SGUtility.checkEquality(indicesArray);
        SGIntegerSeriesSet indices = (indicesCommon != null) ? indicesCommon 
        		: SGIntegerSeriesSet.createInstance(frameNumber);
        this.mFrameIndices = indices;
    }

    /**
     * Builds an animation thread with the same as given animation thread.
     * 
     * @param th
     *          an animation thread to copy
     */
    public SGAnimationThread(SGAnimationThread th) {
        super();
        this.mAnimations = th.mAnimations.clone();
        this.mCurrentFrameIndex = th.mCurrentFrameIndex;
        this.mFrameNumber = th.mFrameNumber;
        this.mTimeInterval = th.mTimeInterval;
        this.mPlayFlag = th.mPlayFlag;
        this.mObserverList = new ArrayList<AnimationThreadObserver>(th.mObserverList);
        this.mFrameIndexArray = th.mFrameIndexArray.clone();
        this.mForwardFlag = th.mForwardFlag;
        this.mFrameIndices = (SGIntegerSeriesSet) th.mFrameIndices.clone();
        this.setLoopPlaybackEnabled(th.isLoopPlaybackEnabled());
        this.setFrameRate(th.getFrameRate());
    }

    /**
     * Returns the animation data source.
     * 
     * @return the animation data source
     */
    public SGIAnimation[] getAnimations() {
        return this.mAnimations.clone();
    }
    
    /**
     * Run this thread.
     */
    public void run() {

        // get the number of frame
        final int fSize = this.mFrameNumber;

        PlayInfo info = this.getPlayInfo();
        
        // endless loop for the animation
        while (this.mPlayFlag) {

            // current index
            final int curIndex = this.mCurrentFrameIndex;

            // increment the counter
            int nextIndex = curIndex;
            if (this.mForwardFlag) {
            	nextIndex++;
            } else {
            	nextIndex--;
            }

            final int vIndex = getValidFrameIndex(nextIndex, fSize, 
            		this.mFrameIndexArray, this.mForwardFlag, this.isLoopPlaybackEnabled());
            if (vIndex == -1) {
            	this.mPlayFlag = false;
            	continue;
            }

            // set the frame index
            final int nIndex = vIndex;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                    	mCurrentFrameIndex = nIndex;
                    	for (SGIAnimation animation : mAnimations) {
                            animation.setCurrentFrameIndex(nIndex);
                    	}
                    }
                });
            } catch (InterruptedException e1) {
            } catch (InvocationTargetException e1) {
            }

            // notify to the observers
            for (AnimationThreadObserver obs : this.mObserverList) {
                obs.updated(this);
            }
            
            // sleep seconds for time step
            try {
                final long initTime = System.currentTimeMillis();
                
                // loops of minimum sleeping time
                boolean endFlag = false;
                for (int ii = 0; ii < info.repeatNum; ii++) {
                    info = this.getPlayInfo();
                    if (!this.mPlayFlag) {
                        break;
                    }
                    Thread.sleep(MIN_SLEEP_TIME);
                    final long curTime = System.currentTimeMillis();
                    final long spentTime = curTime - initTime;
                    if (spentTime > this.mTimeInterval) {
                        endFlag = true;
                        break;
                    }
                }

                if (endFlag) {
                    continue;
                }

                if (!this.mPlayFlag) {
                    break;
                }
                
                info = this.getPlayInfo();
                
                // sleep remained time
                Thread.sleep(info.remainedTime);
                
            } catch (InterruptedException e) {
            }
        }
    }

    // a class for the information of playing
    private static class PlayInfo {
        long repeatNum;
        long remainedTime;
    }
    
    private PlayInfo getPlayInfo() {
        long repeatNum;
        long rTime;
        if (this.mTimeInterval < MIN_SLEEP_TIME) {
            repeatNum = 0L;
            rTime = this.mTimeInterval;
        } else {
            repeatNum = this.mTimeInterval / MIN_SLEEP_TIME;
            rTime = this.mTimeInterval - repeatNum * MIN_SLEEP_TIME;
        }
        PlayInfo info = new PlayInfo();
        info.repeatNum = repeatNum;
        info.remainedTime = rTime;
        return info;
    }

    /**
     * Returns whether the animation is playing.
     */
    public boolean isPlaying() {
        return this.mPlayFlag;
    }

    /**
     * Returns whether loop play back is available.
     * 
     * @return true the loop play back is available
     */
    public boolean isLoopPlaybackEnabled() {
    	return this.mLoopPlaybackFlag;
    }

    /**
     * Sets whether loop play back is available.
     * 
     * @param b
     *           true to set enabled the loop play back
     */
    public void setLoopPlaybackEnabled(final boolean b) {
    	this.mLoopPlaybackFlag = b;
    	for (SGIAnimation animation : this.mAnimations) {
    		animation.setLoopPlaybackAvailable(b);
    	}
    }

    /**
     * Returns the frame rate.
     * 
     * @return the frame rate
     */
    public double getFrameRate() {
    	return this.mFrameRate;
    }
    
    /**
     * Sets the frame rate.
     * 
     * @param rate
     *           the frame rate to set
     */
    public void setFrameRate(final double rate) {
    	this.mFrameRate = rate;
    	for (SGIAnimation animation : this.mAnimations) {
    		animation.setFrameRate(rate);
    	}
    }
    
    /**
     * Returns the time interval.
     * 
     * @return the sleeping time
     */
    public long getTimeInterval() {
        return this.mTimeInterval;
    }

    /**
     * Sets the sleeping time.
     * 
     * @param t
     *          sleeping time in units of milliseconds
     */
    public void setTimeInterval(final long t) {
        if (t < 0L) {
            throw new IllegalArgumentException("t < 0L");
        }
        this.mTimeInterval = t;
    }
    
    /**
     * Returns the flag whether playing forward.
     * 
     * @return true if playing forward
     */
    public boolean isPlayingForward() {
    	return this.mForwardFlag;
    }

    /**
     * Sets the flag whether playing forward.
     * 
     * @param b
     *          true to set playing forward
     */
    public void setPlayingForward(final boolean b) {
    	this.mForwardFlag = b;
    }

    /**
     * Overrode to set the flag.
     */
    @Override
    public void start() {
    	this.mPlayFlag = true;
    	super.start();
    }
    
    /**
     * Stops the animation.
     * 
     */
    public void stopAnimation() {
    	this.mPlayFlag = false;
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mObserverList.clear();
    }
    
    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }
    
    /**
     * Returns the number of frames.
     * 
     * @return the number of frames
     */
    public int getFrameNumber() {
    	return this.mFrameNumber;
    }

    /**
     * Returns the current frame index.
     * 
     * @return the current frame index
     */
    public int getCurrentFrameIndex() {
    	return this.mCurrentFrameIndex;
    }

    /**
     * Sets the current frame index.
     * 
     * @param index the frame index to be set
     */
    public void setCurrentFrameIndex(final int index) {
    	this.mCurrentFrameIndex = index;
    	for (SGIAnimation animation : this.mAnimations) {
    		animation.setCurrentFrameIndex(index);
    	}
    }

    /**
     * An interface of the observer of this thread.
     *
     */
    public static interface AnimationThreadObserver {
        /**
         * Called when a thread cycle is looped.
         * 
         * @param th
         *          an animation thread
         */
        public void updated(SGAnimationThread th);
    }

    /**
     * List of observers of this thread.
     */
    private List<AnimationThreadObserver> mObserverList = new ArrayList<AnimationThreadObserver>();
    
    /**
     * Add an observer of this thread.
     * 
     * @param obs
     *           an observer
     */
    public void addAnimationThreadObserver(AnimationThreadObserver obs) {
        this.mObserverList.add(obs);
    }
    
    /**
     * Remove an observer of this thread.
     * 
     * @param obs
     *           an observer
     */
    public void removeAnimationThreadObserver(AnimationThreadObserver obs) {
        this.mObserverList.remove(obs);
    }
    
    /**
     * Returns the list of available frame indices.
     * 
     * @return the list of available frame indices
     */
    public int[] getAvailableFrameIndices() {
    	return this.mFrameIndexArray.clone();
    }

    /**
     * Sets available frame indices.
     * 
     * @param indices
     *           the array of available frame indices
     */
    public void setAvailableFrameIndices(int[] indices) {
    	final int len = this.getFrameNumber();
    	for (Integer num : indices) {
    		if (num < 0 || num >= len) {
    			throw new IllegalArgumentException("Index out of bounds: " + num);
    		}
    	}
    	this.mFrameIndexArray = indices.clone();
    }
    
	/**
	 * Returns the valid index for the animation.
	 * 
	 * @param index
	 *           the frame index tried to be set
	 * @param frameNumber
	 *           the total number of frames
	 * @param indices
	 *           the array of available frame indices
	 * @param forward
	 *           true for forward and false for backward
	 * @param loopPlayback
	 *           true for loop play back
	 * @return valid index if found, otherwise -1
	 */
	public static int getValidFrameIndex(final int index, final int frameNumber, 
			final int[] indices, final boolean forward, final boolean loopPlayback) {
		if (indices.length == 0) {
			return -1;
		}
		int indexNew = index;
    	if (forward) {
    		final int lastIndex = indices[indices.length - 1];
    		if (indexNew > lastIndex) {
                if (loopPlayback) {
                	indexNew -= frameNumber;
                } else {
                	return -1;
                }
    		}
    		int bIndex = Arrays.binarySearch(indices, indexNew);
    		if (bIndex < 0) {
    			bIndex = - bIndex - 1;
    		}
    		indexNew = indices[bIndex];
    	} else {
    		final int firstIndex = indices[0];
            if (indexNew < firstIndex) {
            	if (loopPlayback) {
                	indexNew += frameNumber;
            	} else {
            		return -1;
            	}
            }
    		int bIndex = Arrays.binarySearch(indices, indexNew);
    		if (bIndex < 0) {
    			bIndex = - bIndex - 1;
    			bIndex--;
    		}
    		indexNew = indices[bIndex];
    	}
    	return indexNew;
	}

    public static long getTimeInterval(final double frameRate) {
    	if (frameRate == 0.0) {
    		return Long.MAX_VALUE;
    	} else {
        	return (long) (1000L / frameRate);
    	}
    }

    public SGIntegerSeriesSet getFrameIndices() {
    	return (SGIntegerSeriesSet) this.mFrameIndices.clone();
    }
    
    public void setFrameIndices(final SGIntegerSeriesSet indices) {
    	this.mFrameIndices = (SGIntegerSeriesSet) indices.clone();
    	for (SGIAnimation animation : this.mAnimations) {
    		animation.setFrameIndices(
    				(SGIntegerSeriesSet) indices.clone());
    	}
    }

}
