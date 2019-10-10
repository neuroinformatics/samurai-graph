package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import ch.systemsx.cisd.hdf5.HDF5DataClass;
import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5DataTypeInformation;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 * The wrapper class for a HDF5 file.
 *
 */
public class SGHDF5File extends SGMDArrayFile {

	private static List<HDF5DataClass> dataClassList = new ArrayList<HDF5DataClass>();
	
	static {
		dataClassList.add(HDF5DataClass.FLOAT);
		dataClassList.add(HDF5DataClass.INTEGER);
		dataClassList.add(HDF5DataClass.STRING);
	}
	
	/**
	 * The reader for a HDF5 file.
	 */
	private IHDF5Reader mReader = null;
	
	/**
	 * Builds this object with given HDF5 reader.
	 * 
	 * @param reader
	 *           a HDF5 reader
	 */
	public SGHDF5File(final IHDF5Reader reader) {
		super();
		if (reader == null) {
			throw new IllegalArgumentException("reader == null");
		}
		this.mReader = reader;

		// create variables
		List<String> dataSetNames = findDataSet(reader, "/", 0);
		this.mVariables = new SGHDF5Variable[dataSetNames.size()];
		for (int ii = 0; ii < dataSetNames.size(); ii++) {
			String name = dataSetNames.get(ii);
			this.mVariables[ii] = new SGHDF5Variable(this, name);
		}
	}
	
	private static List<String> findDataSet(final IHDF5Reader reader, final String groupName,
			final int depth) {
		List<String> ret = new ArrayList<String>();
		List<String> members = reader.getAllGroupMembers(groupName);
		for (String member : members) {
			StringBuffer sb = new StringBuffer();
			if (depth > 0) {
				sb.append(groupName);
				sb.append('/');
			}
			sb.append(member);
			String path = sb.toString();
			if (reader.isDataSet(path)) {
				if (canAccept(reader, path)) {
					ret.add(path);
				}
			} else if (reader.isGroup(path)) {
				List<String> groupMemberPaths = reader.getGroupMemberPaths(path);
				for (String groupMemberPath: groupMemberPaths) {
					if (reader.isDataSet(groupMemberPath)) {
						if (canAccept(reader, groupMemberPath)) {
							ret.add(groupMemberPath);
						}
					} else if (reader.isGroup(groupMemberPath)) {
						List<String> list = findDataSet(reader, groupMemberPath, depth + 1);
						ret.addAll(list);
					}
				}
			}
		}
		return ret;
	}
	
	private static boolean canAccept(final IHDF5Reader reader, final String dataSetName) {
		HDF5DataSetInformation dataSetInfo = reader.getDataSetInformation(dataSetName);
		HDF5DataTypeInformation dataTypeInfo = dataSetInfo.getTypeInformation();
		HDF5DataClass cl = dataTypeInfo.getDataClass();
		return dataClassList.contains(cl);
	}

	/**
	 * Returns the HDF5 reader.
	 * 
	 * @return the HDF5 reader
	 */
	public IHDF5Reader getReader() {
		return this.mReader;
	}

	/**
	 * Disposes of this object.
	 */
	@Override
	public void dispose() {
    	super.dispose();
		this.mReader.close();
		this.mReader = null;
	}

    /**
     * Returns the file path.
     * 
     * @return the file path
     */
	@Override
	public String getPath() {
		return this.mReader.getFile().getPath();
	}

	/**
	 * Returns true if the reader object is equal.
	 * 
	 * @param obj
	 *          an object to be compared
	 * @return true if the reader object e is equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SGHDF5File)) {
			return false;
		}
		SGHDF5File hdf5file = (SGHDF5File) obj;
		if (!this.mReader.getFile().equals(hdf5file.mReader.getFile())) {
			return false;
		}
		return true;
	}
	
    /**
     * Returns the list of global attributes.
     * 
     * @return the list of global attributes
     */
	@Override
	public List<SGAttribute> getAttributes() {
		final String path = "/";
		List<String> names = this.mReader.getAllAttributeNames(path);
		return SGDataUtility.findHDF5Attributes(this.mReader, path, names);
	}
}
