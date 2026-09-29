package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGConstants.OPERATION;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.nc2.NetcdfFiles;

/** Unit tests of the property I/O of the multiple NetCDF data. */
class SGSXYNetCDFMultipleDataPropertyIOTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGSXYNetCDFMultipleData data;

  @BeforeEach
  void setUp() throws IOException {
    final SGNetCDFFile ncfile = new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
    this.data =
        new SGSXYNetCDFMultipleData(
            ncfile,
            new SGDataSourceObserver(),
            new SGNetCDFDataColumnInfo[] {
              this.info(ncfile, "x", SGDataColumnTypeConstants.X_VALUE)
            },
            new SGNetCDFDataColumnInfo[] {
              this.info(ncfile, "height", SGDataColumnTypeConstants.Y_VALUE)
            },
            null,
            null,
            null,
            null,
            true);
  }

  private SGNetCDFDataColumnInfo info(
      final SGNetCDFFile ncfile, final String name, final String type) {
    return SGDataFileUtility.createDataColumnInfo(ncfile.findVariable(name), type);
  }

  private SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties newProperties() {
    return new SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties();
  }

  @Test
  void getPropertiesCollectsTheVariableNames() {
    final SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties p = this.newProperties();
    assertTrue(this.data.getProperties(p));
    assertArrayEquals(new String[] {"x"}, p.xNames);
    assertArrayEquals(new String[] {"height"}, p.yNames);
    assertNull(p.lNames);
    assertNull(p.uNames);
    assertNull(p.mPickUpInfo);
  }

  @Test
  void getPropertiesRejectsTheWrongType() {
    assertFalse(this.data.getProperties(new SGSXYSDArrayMultipleData.SXYMultipleDataProperties()));
  }

  @Test
  void setPropertiesRejectsTheWrongType() {
    assertFalse(this.data.setProperties(new SGSXYSDArrayMultipleData.SXYMultipleDataProperties()));
  }

  @Test
  void setPropertiesAppliesTheRoundTrippedValues() {
    final SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties p = this.newProperties();
    assertTrue(this.data.getProperties(p));
    p.mDecimalPlaces = 3;
    p.mExponent = 1;
    assertTrue(this.data.setProperties(p));
    assertEquals(3, this.data.getDecimalPlaces());
    assertEquals(1, this.data.getExponent());
    assertEquals("x", this.data.getXVariable().getName());
    assertEquals("height", this.data.getYVariable().getName());
  }

  @Test
  void writePropertyWritesTheVariableNameAttributes() throws Exception {
    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element el = doc.createElement("data");
    final SGExportParameter param = new SGExportParameter(OPERATION.SAVE_TO_PROPERTY_FILE);
    assertTrue(this.data.writeProperty(el, param));
    assertEquals(
        SGDataTextUtility.bindVariableNamesInBracket(new String[] {"x"}),
        el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES));
    assertEquals(
        SGDataTextUtility.bindVariableNamesInBracket(new String[] {"height"}),
        el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES));
  }
}
