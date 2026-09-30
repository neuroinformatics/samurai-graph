package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests of the MD array node tree built by the MD array data. */
class SGMDArrayDataMDArrayNodeTest {

  private final SGSXYZMDArrayData host = new SGSXYZMDArrayData();

  @Test
  void theConstructorStoresNameAndDepth() {
    final SGMDArrayData.MDArrayNode node = this.host.new MDArrayNode("name", 3);
    assertEquals("name", node.name);
    assertEquals(3, node.depth);
    assertTrue(node.childList.isEmpty());
  }

  @Test
  void addChildBuildsTheGroupTree() {
    final SGMDArrayData.MDArrayNode root = this.host.new MDArrayNode(null, 0);
    root.addChild(new String[] {"a", "b", "c"});
    assertEquals(1, root.childList.size());
    final SGMDArrayData.MDArrayNode a = root.childList.get(0);
    assertEquals("a", a.name);
    assertEquals(1, a.depth);
    assertEquals(1, a.childList.size());
    final SGMDArrayData.MDArrayNode b = a.childList.get(0);
    assertEquals("b", b.name);
    assertEquals(2, b.depth);
    final SGMDArrayData.MDArrayNode c = b.childList.get(0);
    assertEquals("c", c.name);
    assertEquals(3, c.depth);
    assertTrue(c.childList.isEmpty());
    assertEquals("[a:[b:[c:[]]]]", root.toString());
  }

  @Test
  void addChildSharesExistingBranches() {
    final SGMDArrayData.MDArrayNode root = this.host.new MDArrayNode(null, 0);
    root.addChild(new String[] {"a", "b"});
    root.addChild(new String[] {"a", "c"});
    root.addChild(new String[] {"a", "b"});
    assertEquals(1, root.childList.size());
    final SGMDArrayData.MDArrayNode a = root.childList.get(0);
    final List<SGMDArrayData.MDArrayNode> children = a.childList;
    assertEquals(2, children.size());
    assertEquals("b", children.get(0).name);
    assertEquals("c", children.get(1).name);
    // the second add of the same path did not create a new node
    assertTrue(children.get(0).childList.isEmpty());
    assertSame(children.get(0), a.childList.get(0));
    assertEquals("[a:[b:[], c:[]]]", root.toString());
  }

  @Test
  void addChildIgnoresPathsAlreadyAtOrBelowTheDepth() {
    final SGMDArrayData.MDArrayNode root = this.host.new MDArrayNode(null, 0);
    root.addChild(new String[] {"a", "b"});
    final SGMDArrayData.MDArrayNode leaf = root.childList.get(0).childList.get(0);
    // the leaf depth equals the path length, so nothing is added
    leaf.addChild(new String[] {"a", "b"});
    assertTrue(leaf.childList.isEmpty());
    // a longer path is extended from the leaf
    leaf.addChild(new String[] {"a", "b", "c"});
    assertEquals(1, leaf.childList.size());
    assertEquals("c", leaf.childList.get(0).name);
  }
}
