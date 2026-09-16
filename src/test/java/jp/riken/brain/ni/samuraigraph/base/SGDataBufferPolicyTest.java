package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests of the data buffer policy. */
class SGDataBufferPolicyTest {

  @Test
  void gettersReflectTheConstructorArguments() {
    final SGDataBufferPolicy policy = new SGDataBufferPolicy(true, false, true);
    assertTrue(policy.isAllValuesGotten());
    assertFalse(policy.isInvalidValuesRemoved());
    assertTrue(policy.isEditedValuesReflected());
  }

  @Test
  void allFalseArgumentsProduceAllFalseGetters() {
    final SGDataBufferPolicy policy = new SGDataBufferPolicy(false, false, false);
    assertFalse(policy.isAllValuesGotten());
    assertFalse(policy.isInvalidValuesRemoved());
    assertFalse(policy.isEditedValuesReflected());
  }

  @Test
  void allTrueArgumentsProduceAllTrueGetters() {
    final SGDataBufferPolicy policy = new SGDataBufferPolicy(true, true, true);
    assertTrue(policy.isAllValuesGotten());
    assertTrue(policy.isInvalidValuesRemoved());
    assertTrue(policy.isEditedValuesReflected());
  }

  @Test
  void mixedArgumentsMapToTheMatchingGetters() {
    final SGDataBufferPolicy policy = new SGDataBufferPolicy(true, true, false);
    assertTrue(policy.isAllValuesGotten());
    assertTrue(policy.isInvalidValuesRemoved());
    assertFalse(policy.isEditedValuesReflected());
  }
}
