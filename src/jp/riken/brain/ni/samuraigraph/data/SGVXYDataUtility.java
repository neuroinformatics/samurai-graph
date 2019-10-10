package jp.riken.brain.ni.samuraigraph.data;

/**
 * An utility class for vector type data.
 * 
 */
public class SGVXYDataUtility {

    /**
     * Returns an array of x components of vectors.
     * 
     * @param magArray
     *            an array of magnification of vectors
     * @param angleArray
     *            an array of angle of vectors
     * @return an array of x components of vectors
     */
    static double[] getXComponentArray(final double[] magArray,
            final double[] angleArray) {
        final int len = magArray.length;
        double[] array = new double[len];
        for (int ii = 0; ii < len; ii++) {
        	if (Double.isNaN(magArray[ii]) || Double.isNaN(angleArray[ii]) 
        			|| magArray[ii] < 0) {
        		array[ii] = Double.NaN;
        	} else {
                // r*cos(theta)
                array[ii] = magArray[ii] * Math.cos(angleArray[ii]);
        	}
        }
        return array;
    }

    /**
     * Returns an array of y components of vectors.
     * 
     * @param magArray
     *            an array of magnification of vectors
     * @param angleArray
     *            an array of angle of vectors
     * @return an array of y components of vectors
     */
    static double[] getYComponentArray(final double[] magArray,
            final double[] angleArray) {
        final int len = magArray.length;
        double[] array = new double[len];
        for (int ii = 0; ii < len; ii++) {
        	if (Double.isNaN(magArray[ii]) || Double.isNaN(angleArray[ii]) 
        			|| magArray[ii] < 0) {
        		array[ii] = Double.NaN;
        	} else {
                // r*sin(theta)
                array[ii] = magArray[ii] * Math.sin(angleArray[ii]);
        	}
        }
        return array;
    }

    /**
     * Returns an array of magnitude of vectors.
     * 
     * @param xComArray
     *            an array of x component of vectors
     * @param yComArray
     *            an array of y component of vectors
     * @return an array of magnitude of vectors
     */
    static double[] getMagnitudeArray(final double[] xComArray,
            final double[] yComArray) {
        final int len = xComArray.length;
        double[] array = new double[len];
        for (int ii = 0; ii < len; ii++) {
            final double x = xComArray[ii];
            final double y = yComArray[ii];
            array[ii] = Math.sqrt(x * x + y * y);
        }
        return array;
    }

    /**
     * Returns an array of angle of vectors.
     * 
     * @param xComArray
     *            an array of x component of vectors
     * @param yComArray
     *            an array of y component of vectors
     * @return an array of angle of vectors
     */
    static double[] getAngleArray(final double[] xComArray,
            final double[] yComArray) {
        final int len = xComArray.length;
        double[] array = new double[len];
        for (int ii = 0; ii < len; ii++) {
            final double x = xComArray[ii];
            if (x == 0.0) {
                array[ii] = Math.PI / 2.0;
            } else {
                final double y = yComArray[ii];
                array[ii] = Math.atan(y / x);
            }
        }
        return array;
    }

}
