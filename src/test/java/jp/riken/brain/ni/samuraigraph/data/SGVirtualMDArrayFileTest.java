package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for the virtual mdarray file built from in-memory variables. */
class SGVirtualMDArrayFileTest {

  @Test
  void fileIsBuiltFromClonedVariables() {
    SGVirtualMDArrayVariable.D1 v1 = new SGVirtualMDArrayVariable.D1(new double[] {1.0, 2.0}, "v1");
    SGVirtualMDArrayVariable.D1 v2 = new SGVirtualMDArrayVariable.D1(new double[] {3.0, 4.0}, "w");
    SGVirtualMDArrayFile file = new SGVirtualMDArrayFile(new SGVirtualMDArrayVariable[] {v1, v2});
    assertEquals(2, file.getVariables().length);
    SGMDArrayVariable found = file.findVariable("v1");
    assertEquals("v1", found.getName());
  }

  @Test
  void nullVariablesAreRejected() {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> new SGVirtualMDArrayFile(null));
  }
}
