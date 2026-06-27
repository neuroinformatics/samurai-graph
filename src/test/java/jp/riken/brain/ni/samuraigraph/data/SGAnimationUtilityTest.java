package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;

class SGAnimationUtilityTest {

  // -- getAllAnimationFrameBoundsX(SGISXYTypeSingleData) --

  @Test
  void getAllAnimationFrameBoundsXSingle_noAnimation() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getAllAnimationFrameBoundsXSingle_animationNotAvailable() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isAnimationSupported()).thenReturn(true);
    when(data.isAnimationAvailable()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getAllAnimationFrameBoundsY(SGISXYTypeSingleData) --

  @Test
  void getAllAnimationFrameBoundsYSingle_noAnimation() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  // -- getAllAnimationFrameBoundsX(SGISXYZTypeData) --

  @Test
  void getAllAnimationFrameBoundsXXYZ_noAnimation() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getAllAnimationFrameBoundsXXYZ_nullValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(null);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds).isNull();
  }

  // -- getAllAnimationFrameBoundsY(SGISXYZTypeData) --

  @Test
  void getAllAnimationFrameBoundsYXYZ_noAnimation() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  // -- getAllAnimationFrameBoundsZ(SGISXYZTypeData) --

  @Test
  void getAllAnimationFrameBoundsZXYZ_noAnimation() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getZValueArray(false)).thenReturn(new double[] {100.0, 200.0, 300.0});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsZ(data);
    assertThat(bounds.getMinValue()).isEqualTo(100.0);
    assertThat(bounds.getMaxValue()).isEqualTo(300.0);
  }

  @Test
  void getAllAnimationFrameBoundsZXYZ_nullValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getZValueArray(false)).thenReturn(null);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsZ(data);
    assertThat(bounds).isNull();
  }

  // -- getAllAnimationFrameBoundsX(SGIVXYTypeData) --

  @Test
  void getAllAnimationFrameBoundsXVXY_noAnimation() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getAllAnimationFrameBoundsXVXY_nullValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(null);

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds).isNull();
  }

  // -- getAllAnimationFrameBoundsY(SGIVXYTypeData) --

  @Test
  void getAllAnimationFrameBoundsYVXY_noAnimation() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  // -- getAllAnimationFrameBoundsX(SGISXYTypeMultipleData) --

  @Test
  void getAllAnimationFrameBoundsXMultiple_noAnimation() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeSingleData d2 = mock(SGISXYTypeSingleData.class);
    when(d2.getXValueArray(false)).thenReturn(new double[] {3.0, 4.0});
    when(d2.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1, d2});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(4.0);
  }

  // -- getAllAnimationFrameBoundsY(SGISXYTypeMultipleData) --

  @Test
  void getAllAnimationFrameBoundsYMultiple_noAnimation() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeSingleData d2 = mock(SGISXYTypeSingleData.class);
    when(d2.getYValueArray(false)).thenReturn(new double[] {30.0, 40.0});
    when(d2.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1, d2});

    SGValueRange bounds = SGAnimationUtility.getAllAnimationFrameBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(40.0);
  }
}
