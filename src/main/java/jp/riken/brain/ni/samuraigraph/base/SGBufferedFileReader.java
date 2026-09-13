package jp.riken.brain.ni.samuraigraph.base;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The wrapper class of file writer using buffered writer. */
public class SGBufferedFileReader {

  private static final Logger logger = LoggerFactory.getLogger(SGBufferedFileReader.class);

  private FileInputStream fis = null;
  private InputStreamReader isr = null;
  private BufferedReader br = null;

  /**
   * Builds a file reader for a file of given path.
   *
   * @param path the file path to read
   * @throws IOException
   */
  public SGBufferedFileReader(final String path) throws IOException {
    this(path, SGUtilityText.detectCharacterSet(path));
  }

  /**
   * Builds a file reader for a file of given path with given character set.
   *
   * @param charsetName the charsetName parameter
   * @param path the file path to read
   * @throws IOException
   */
  public SGBufferedFileReader(final String path, final String charsetName) throws IOException {
    super();
    String cName = (charsetName == null) ? CHAR_SET_NAME_UTF8 : charsetName;
    fis = new FileInputStream(new File(path));
    isr = new InputStreamReader(fis, cName);
    br = new BufferedReader(isr);
  }

  /** Returns the buffered reader. */
  public BufferedReader getBufferedReader() {
    return this.br;
  }

  /** Closes this reader. */
  public void close() {
    if (br != null) {
      try {
        br.close();
      } catch (IOException e) {
        logger.debug("Failed to close buffered reader", e);
      }
    }
    if (isr != null) {
      try {
        isr.close();
      } catch (IOException e) {
        logger.debug("Failed to close input stream reader", e);
      }
    }
    if (fis != null) {
      try {
        fis.close();
      } catch (IOException e) {
        logger.debug("Failed to close file input stream", e);
      }
    }
  }
}
