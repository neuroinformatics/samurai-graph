package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SGSXYDataBufferPolicyTest {

  @Test
  void constructorWithAllFlagsTrue() {
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(true, true, true, true, true);
    assertTrue(policy.isAllValuesGotten());
    assertTrue(policy.isInvalidValuesRemoved());
    assertTrue(policy.isEditedValuesReflected());
    assertTrue(policy.isShiftValuesContained());
    assertTrue(policy.isTakingAllStride());
  }

  @Test
  void constructorWithAllFlagsFalse() {
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(false, false, false, false, false);
    assertFalse(policy.isAllValuesGotten());
    assertFalse(policy.isInvalidValuesRemoved());
    assertFalse(policy.isEditedValuesReflected());
    assertFalse(policy.isShiftValuesContained());
    assertFalse(policy.isTakingAllStride());
  }

  @Test
  void isShiftValuesContained() {
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(false, false, false, true, false);
    assertTrue(policy.isShiftValuesContained());
    assertFalse(policy.isTakingAllStride());
  }

  @Test
  void isTakingAllStride() {
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(false, false, false, false, true);
    assertTrue(policy.isTakingAllStride());
    assertFalse(policy.isShiftValuesContained());
  }
}
