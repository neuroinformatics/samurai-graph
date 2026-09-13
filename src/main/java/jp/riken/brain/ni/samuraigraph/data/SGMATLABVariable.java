package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;

/** The wrapper class of MATLAB array object. */
public class SGMATLABVariable extends SGNumberMDArrayVariable {

  /** The default constructor. */
  public SGMATLABVariable() {
    super();
  }

  /**
   * Builds an variable.
   *
   * @param file the file of MATLAB data
   * @param fqName the fully qualified name of a MLArray
   */
  public SGMATLABVariable(final SGMATLABFile file, final String fqName) {
    this();
    if (file == null) {
      throw new IllegalArgumentException("file == null");
    }
    if (fqName == null) {
      throw new IllegalArgumentException("fqName == null");
    }
    this.mFile = file;
    this.mName = fqName;

    MatFileReader reader = this.getReader();
    Map<String, MLArray> content = reader.getContent();
    MLArray mlArray = content.get(this.getName());
    if (!mlArray.isDouble()) {
      throw new Error("Unsupported array type: " + mlArray.getType());
    }
    this.mNumberArray = new MLDoubleWrapper((MLDouble) mlArray);

    this.initWithDimensions();
  }

  /**
   * Returns the reader.
   *
   * @return the reader
   */
  public MatFileReader getReader() {
    SGMATLABFile matFile = (SGMATLABFile) this.getFile();
    return matFile.getReader();
  }

  /**
   * Returns the dimensions.
   *
   * @return the dimensions
   */
  @Override
  public int[] getDimensions() {
    return this.mNumberArray.getDimensions();
  }

  /**
   * Returns the list of attributes.
   *
   * @return the list of attributes
   */
  @Override
  public List<SGAttribute> getAttributes() {
    // returns an empty list
    return new ArrayList<SGAttribute>();
  }

  /** Overrode to check the class type. */
  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof SGMATLABVariable)) {
      return false;
    }
    return true;
  }

  /** The wrapper class of a MLDouble object. */
  protected static class MLDoubleWrapper implements INumberArray {

    private MLDouble mArray;

    protected MLDoubleWrapper(MLDouble nArray) {
      super();
      this.mArray = nArray;
    }

    /**
     * Returns the number at given array index.
     *
     * @param index the array index
     * @return the number
     */
    @Override
    public Double get(final int index) {
      return this.mArray.get(index);
    }

    /**
     * Returns the array of dimensions.
     *
     * @return the array of dimensions
     */
    @Override
    public int[] getDimensions() {
      return this.mArray.getDimensions();
    }
  }

  @Override
  protected MDArrayDataType getDataType() {
    return new MATLABDataType();
  }

  protected static class MATLABDataType extends MDArrayDataType {
    protected MATLABDataType() {
      super();
    }
  }

  @Override
  protected MDArrayDataType getExportFloatingNumberDataType() {
    return this.getDataType();
  }
}
