package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGMATLABFile} backed by a generated MATLAB file. */
class SGMATLABFileTest {

  private File createMatFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble data = new MLDouble("x", new double[][] {{1.0}, {2.0}, {3.0}});
    MLChar label = new MLChar("label", "sample");
    new MatFileWriter(path.toFile(), Arrays.asList(data, label));
    return path.toFile();
  }

  @Test
  void opensGeneratedMatFile() throws Exception {
    File file = createMatFile();
    MatFileReader reader = new MatFileReader(file);
    SGMATLABFile matFile = new SGMATLABFile(file.getPath(), reader);
    assertEquals(file.getPath(), matFile.getPath());
    assertEquals(reader, matFile.getReader());
    assertNotNull(matFile.getAttributes());
    assertFalse(matFile.getAttributes().isEmpty());
  }
}
