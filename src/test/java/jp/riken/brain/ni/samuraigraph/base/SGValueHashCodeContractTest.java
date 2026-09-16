package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import org.junit.jupiter.api.Test;

/** The equal value-type instances hash equally. */
class SGValueHashCodeContractTest {

  @Test
  void arrayIndex() {
    assertEquals(new SGArrayIndex(3), new SGArrayIndex(3));
    assertEquals(new SGArrayIndex(3).hashCode(), new SGArrayIndex(3).hashCode());
  }

  @Test
  void doubleAxisValue() {
    final SGAxisDoubleValue first = new SGAxisDoubleValue(2.5);
    final SGAxisDoubleValue second = new SGAxisDoubleValue(2.5);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void dateAxisValue() {
    final SGAxisDateValue first = new SGAxisDateValue(new SGDate(0L));
    final SGAxisDateValue second = new SGAxisDateValue(new SGDate(0L));
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void doubleStepValue() {
    final SGAxisDoubleStepValue first = new SGAxisDoubleStepValue(1.5);
    final SGAxisDoubleStepValue second = new SGAxisDoubleStepValue(1.5);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void dateStepValue() {
    final SGAxisDateStepValue first = new SGAxisDateStepValue(SGPeriod.days(2));
    final SGAxisDateStepValue second = new SGAxisDateStepValue(SGPeriod.days(2));
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void dataColumnInfoSubclassesHashEqually() {
    final SGSDArrayDataColumnInfo first = new SGSDArrayDataColumnInfo("x", "number", 3);
    final SGSDArrayDataColumnInfo second = new SGSDArrayDataColumnInfo("x", "number", 3);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
}
