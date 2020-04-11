package jp.riken.brain.ni.samuraigraph.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

public class SGApplicationProperties implements SGIApplicationConstants {
    // version string
    private String mVersion = "-1.-1.-1";

    private int mMajorVersion = -1;

    private int mMinorVersion = -1;

    private int mMicroVersion = -1;
    
    private int mUpgradeCycle = -1;

    private boolean mStatus = false;

    public SGApplicationProperties() {
        this.mStatus = loadProperties();
    }

    public boolean getStatus() {
        return this.mStatus;
    }

    /**
     * get version string
     * 
     * @return version
     */
    public String getVersionString() {
        return this.mVersion;
    }

    /**
     * get major version
     * 
     * @return major version
     */
    public int getMajorVersion() {
        return this.mMajorVersion;
    }

    /**
     * get minor version
     * 
     * @return minor version
     */
    public int getMinorVersion() {
        return this.mMinorVersion;
    }

    /**
     * get micro version
     * 
     * @return micro version
     */
    public int getMicroVersion() {
        return this.mMicroVersion;
    }
    
    public int getUpgradeCycle() {
    	return this.mUpgradeCycle;
    }

    private boolean loadProperties() {
        Properties prop = new Properties();
        InputStream is = null;
        try {
        	StringBuffer sb = new StringBuffer();
        	sb.append(APPLICATION_RESOURCE_DIRECTORY);
        	sb.append('/');
        	sb.append(APPLICATION_PROPERTY_FILENAME);
            is = ClassLoader.getSystemResourceAsStream(sb.toString());
            prop.load(is);

            // get version number
            String version = prop.getProperty(VERSION_PROPERTY_NAME);
            if (version == null) {
                return false;
            }
            int[] numbers = SGUtility.splitVersionNumber(version);
            if (numbers == null) {
            	return false;
            }
            
            // get upgrading cycle
            String upgradeCycle = prop.getProperty(UPGRADE_CYCLE_PROPERTY_NAME);
            if (upgradeCycle != null) {
            	Integer num = SGUtilityText.getInteger(upgradeCycle);
            	if (num == null) {
            		return false;
            	}
            	this.mUpgradeCycle = num.intValue();
            }

    		// set to the attributes
            this.mVersion = version;
    		this.mMajorVersion = numbers[0];
    		this.mMinorVersion = numbers[1];
    		this.mMicroVersion = numbers[2];

        } catch (IOException e) {
            return false;
        } finally {
        	if (is != null) {
        		try {
					is.close();
				} catch (IOException e) {
				}
        	}
        }
        return true;
    }
    
}