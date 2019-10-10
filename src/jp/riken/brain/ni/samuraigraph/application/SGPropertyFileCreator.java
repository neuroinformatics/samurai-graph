package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * Create a property file.
 */
public class SGPropertyFileCreator extends SGFileHandler
		implements SGIConstants, SGIPropertyFileConstants {

    /**
     * Default name of the property file with the extension.
     */
	public static final String DEFAULT_PROPERTY_FILE_NAME_WITH_EXTENSION
			= SGApplicationUtility.appendExtension(
					DEFAULT_PROPERTY_FILE_NAME, PROPERTY_FILE_EXTENSION);

    /**
     * Constructs an object.
     * 
     */
    public SGPropertyFileCreator() {
    	super();
        this.initFilePath(DEFAULT_PROPERTY_FILE_NAME, PROPERTY_FILE_EXTENSION);
    }

    /**
     * Create a property file.
     */
    public int create(SGDrawingWindow wnd, File file, SGExportParameter params, 
            final String versionString) {
        // export to the XML file
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            return this._create(wnd, os, params, versionString);
        } catch (Exception ex) {
            return -1;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    //
    private int _create(SGDrawingWindow wnd, OutputStream os, SGExportParameter params,
            final String versionString) throws Exception {
        DOMImplementation domImpl = SGApplicationUtility.getDOMImplementation();
        if (domImpl == null) {
            return -1;
        }

        // create a DocumentType object
        DocumentType docType = domImpl.createDocumentType(ROOT_TAG_NAME,
                PROPERTY_FILE_PUBLIC_ID, PROPERTY_FILE_SYSTEM_ID);

        // create a Document object
        Document document = domImpl.createDocument("", ROOT_TAG_NAME, docType);

        // get the root element
        Element property = document.getDocumentElement();
        property.setAttribute(KEY_VERSION_NUMBER, versionString);
        
        // create a DOM tree
        if (wnd.createDOMTree(document, params) == false) {
            return -1;
        }

        // export to the XML file
        StreamResult result = new StreamResult(os);
        DOMSource source = new DOMSource(document);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();

        // set the output properties
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                PROPERTY_FILE_SYSTEM_ID);
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                PROPERTY_FILE_PUBLIC_ID);

        transformer.transform(source, result);

        return OK_OPTION;
    }

    /**
     * Create a property file.
     */
    public int create(SGDrawingWindow wnd, SGExportParameter params, String versionString) throws IOException {
        // show a file chooser and get selected file
        File file = this.selectOutputFile(wnd, PROPERTY_FILE_EXTENSION, PROPERTY_FILE_DESCRIPTION, 
    			DEFAULT_PROPERTY_FILE_NAME_WITH_EXTENSION);
        if (file == null) {
            return CANCEL_OPTION;
        }
        return this.create(wnd, file, params, versionString);
    }
    
    /**
     * Create a property string into output stream.
     * @param wnd
     * @param os
     * @param type
     * @param versionString
     * @return
     * @throws Exception
     */
    public int create(SGDrawingWindow wnd, OutputStream os, SGExportParameter params,
            final String versionString) throws Exception {
        return this._create(wnd, os, params, versionString);
    }
}
