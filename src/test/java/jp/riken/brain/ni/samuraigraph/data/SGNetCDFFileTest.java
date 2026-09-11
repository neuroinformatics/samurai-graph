package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import org.junit.jupiter.api.Test;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGNetCDFFile} backed by the example NetCDF files. */
class SGNetCDFFileTest {

  private static final String EXAMPLE_15 = "examples/data/Example15.nc";
  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGNetCDFFile openExample15() throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_15));
  }

  private SGNetCDFFile openExample16() throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
  }

  @Test
  void openExample15ExposesFourVariables() throws IOException {
    SGNetCDFFile file = openExample15();
    List<SGNetCDFVariable> variables = file.getVariables();
    assertEquals(4, variables.size());
    assertNotNull(file.findVariable("x"));
    assertNotNull(file.findVariable("height"));
  }

  @Test
  void coordinateVariableDetection() throws IOException {
    SGNetCDFFile file = openExample15();
    assertTrue(file.findVariable("x").isCoordinateVariable());
    assertTrue(file.findVariable("time").isCoordinateVariable());
    assertFalse(file.findVariable("height").isCoordinateVariable());
  }

  @Test
  void unlimitedDimensionDetection() throws IOException {
    SGNetCDFFile file = openExample15();
    assertTrue(file.findVariable("time").isUnlimited());
    assertFalse(file.findVariable("x").isUnlimited());
  }

  @Test
  void attributesOfCoordinateVariable() throws IOException {
    SGNetCDFFile file = openExample15();
    SGNetCDFVariable x = file.findVariable("x");
    assertEquals("X Coordinate", x.getLongName());
    assertEquals("x_coordinate", x.getStandardName());
    assertEquals("m", x.getUnitsString());
    assertEquals("X Coordinate", x.getNameInPriorityOrder());
    assertEquals("x", x.getName());
  }

  @Test
  void readCoordinateValuesOfX() throws IOException {
    SGNetCDFFile file = openExample15();
    double[] values = file.findVariable("x").getNumberArray();
    assertNotNull(values);
    assertEquals(8, values.length);
    assertEquals(1.0, values[0], 0.0);
    assertEquals(15.0, values[7], 0.0);
  }

  @Test
  void readCoordinateValuesOfY() throws IOException {
    SGNetCDFFile file = openExample15();
    double[] values = file.findVariable("y").getNumberArray();
    assertNotNull(values);
    assertEquals(12, values.length);
    assertEquals(0.5, values[0], 0.0);
    assertEquals(11.5, values[11], 0.0);
  }

  @Test
  void readTimeValues() throws IOException {
    SGNetCDFFile file = openExample15();
    double[] values = file.findVariable("time").getNumberArray();
    assertNotNull(values);
    assertEquals(4, values.length);
    assertEquals(0.0, values[0], 0.0);
    assertEquals(3.0, values[3], 0.0);
  }

  @Test
  void getNumberArrayRejectsNonCoordinateVariable() throws IOException {
    SGNetCDFFile file = openExample15();
    assertNull(file.findVariable("height").getNumberArray());
  }

  @Test
  void readSectionOfMultidimensionalVariable() throws IOException, InvalidRangeException {
    SGNetCDFFile file = openExample15();
    SGNetCDFVariable height = file.findVariable("height");
    ucar.ma2.Array array = height.read(new int[] {0, 0, 0}, new int[] {1, 8, 12});
    assertEquals(96, array.getSize());
    assertEquals(1.5, array.getDouble(0), 0.0);
    assertEquals(12.5, array.getDouble(11), 0.0);
  }

  @Test
  void findVariableIndexInDeclarationOrder() throws IOException {
    SGNetCDFFile file = openExample15();
    assertEquals(0, file.getVariableIndex("x"));
    assertEquals(2, file.getVariableIndex("time"));
    assertEquals(3, file.getVariableIndex("height"));
  }

  @Test
  void globalAttributesReadFromFile() throws IOException {
    SGNetCDFFile file = openExample15();
    List<SGAttribute> attributes = file.getAttributes();
    SGAttribute title = null;
    for (SGAttribute attribute : attributes) {
      if ("title".equals(attribute.getName())) {
        title = attribute;
        break;
      }
    }
    assertNotNull(title);
    assertEquals("Test Data 005", title.getValue(0));
  }

  @Test
  void dimensionsExposedOnFile() throws IOException {
    SGNetCDFFile file = openExample15();
    assertEquals(3, file.getDimensions().size());
    Dimension x = file.findDimension("x");
    assertNotNull(x);
    assertEquals(8, x.getLength());
    assertEquals(12, file.findDimension("y").getLength());
    assertEquals(4, file.findDimension("time").getLength());
  }

  @Test
  void example16ExposesErrorVariables() throws IOException {
    SGNetCDFFile file = openExample16();
    SGNetCDFVariable le = file.findVariable("le");
    assertNotNull(le);
    assertEquals("Lower Error", le.getLongName());
    assertEquals("km", le.getUnitsString());
    SGNetCDFVariable ue = file.findVariable("ue");
    assertNotNull(ue);
    assertEquals("Upper Error", ue.getLongName());
  }

  @Test
  void findTextVariableReturnsNullForNumericOnlyFile() throws IOException {
    SGNetCDFFile file = openExample15();
    assertNull(file.findTextVariable("x"));
  }
}
