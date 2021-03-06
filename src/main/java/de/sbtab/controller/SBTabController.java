package de.sbtab.controller;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.TidySBMLWriter;

import javafx.concurrent.Task;

public class SBTabController {

  private static final transient Logger LOGGER = LogManager.getLogger(SBTabController.class);
  private SBTabDocument<SBMLDocument> doc =null;
  // declare my variable at the top of my Java class
  private static Preferences prefs;

  public String getFilePath() {
    return doc.getFile().getAbsolutePath();
  }

  public SBMLDocument getDoc() {
    return doc.getTempDoc();
  }
  
  public SBTabDocument<SBMLDocument> getDocument() {
	    return doc;
	  }
  
  public void setDocument(SBTabDocument<SBMLDocument> document) {
	  doc=document;
  }

  public void setDoc(SBMLDocument doc) {
    this.doc.setTempDoc(doc);
  }

  /**
   * Set Preferences for the programm, at the moment only the file path is saved.
   * @param filePath 
   * 				Absolute Path of the last open dialog.
   */
  public void setPreferences(String filePath) {
    // create a Preferences instance (somewhere later in the code)
    prefs = Preferences.userNodeForPackage(SBTabController.class);
    if (filePath != null) {
      String folderPath = (new File(filePath)).getParent();
      prefs.put("last_output_dir", folderPath);
    } else {
      prefs.put("last_output_dir", System.getProperty("user.home"));
    }
  }

  /**
   * Save SBML document to a {@link File}.
   * 
   * @param doc
   *            the {@link SBMLDocument} to be written
   * @param path
   *            absolute path to {@link SBMLDocument}
   * @param name
   *            name of {@link SBMLDocument}
   * @param version
   *            version of {@link SBMLDocument}
   * 
   */
  public void save(SBMLDocument doc, File path, String name, String version) {
    Task<Void> task = new Task<Void>() {
      @Override
      public Void call() {
        try {
          TidySBMLWriter.write(doc, path, name, version);
        } catch (Exception e) {
          LOGGER.error("Unable to write sbml file", e);
        }
        return null;
      }
    };
    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();
  }

  /**
   * Read SBML document from a {@link File}.
   * 
   * @param absolute
   *            path to {@link SBMLDocument}
   * @return {@link SBMLDocument}
   */
  public SBTabDocument<SBMLDocument> read(String filePath) {
    File theSBMLFile = new File(filePath);
    boolean isFile = theSBMLFile.isFile();
    if (isFile) {
      if (Objects.equals(getFileExtension(theSBMLFile), ".xml")) {
        try {
          doc= new SBTabDocument<SBMLDocument>(
          SBMLReader.read(theSBMLFile),
          theSBMLFile);
        } catch (Exception e) {
          LOGGER.error("Unable to read xml file.", e);
        }
      }
      if (Objects.equals(getFileExtension(theSBMLFile), ".gz")) {
        try {
          doc= new SBTabDocument<SBMLDocument>(
          SBMLReader.read(new GZIPInputStream(new FileInputStream(filePath))),
          theSBMLFile);
        } catch (Exception e) {
          LOGGER.error("Unable to read gz file.", e);
        }
      }
      setPreferences(filePath);
    }
    return doc;
  }

  /**
   * Validator of SBML-Files
   * 
   * @param doc
   *            is the input SBML-File
   * @return a String of Errors
   */
  public static String stringValidator(SBMLDocument doc) {
    // the number of Errors of a SBMLFile
    int numErrors = doc.checkConsistency();
    String s = "";
    if (numErrors == 0) {
      return null;
    } else {
      // get each error and show it
      for (int i = 0; i < numErrors; i++) {
        SBMLError e = doc.getError(i);
        Level l;
        if(e.isError()) {
          l = Level.ERROR;
        }else if(e.isFatal()) {
          l = Level.FATAL;
        }else if(e.isWarning()) {
          l = Level.WARN;
          s += " WARNING: ";
        } else {
          l = Level.INFO;
        }
        s += e.getMessage().toString();
      }
      return s;
    }
  }

  /**
   * Number of Errors in Document
   * 
   * @param doc
   * @return Number of Errors
   */
  public int numErrors(SBMLDocument doc) {
    return doc.checkConsistency();
  }

  /**
   * File extension of a given file.
   * @param File is the given file {@link File}
   * @return File Extension is the corresponding file extension. {@link String}
   */
  public String getFileExtension(File file) {
    String theFileExtension = "";
    try {
      if (file != null && file.exists()) {
        String theFileName = file.getName();
        theFileExtension = theFileName.substring(theFileName.lastIndexOf("."));
      }
    } catch (Exception e) {
      theFileExtension = "";
    }

    return theFileExtension;

  }
  /**
   * Opens Documentation according to specified documentation URL.
   * if no Internet connection is present a local documentation index.html in the directory docs
   * @param theDocumentationURL 
   * is the online URL for the Documentation{@link String}
   * @param theLocalDocumentationPath 
   * is the absolute path for the documentation if theres no internet connection {@link String}.
   * @return url {@link URL);
   */
  public URL getDocumentation(String theDocumentationURL, String theLocalDocumentationPath){
    URL url;
    try
    {
      url = new URL(theDocumentationURL);
      URLConnection connection = url.openConnection();
      connection.connect();
      System.out.println("Internet Connected");
    }catch (Exception e){
      System.out.println("Sorry, No Internet Connection");
      String theDocumentationName = (theLocalDocumentationPath);
      try {
        url = Paths.get(theDocumentationName).toUri().toURL();
      } catch (MalformedURLException e1) {
        e1.printStackTrace();
        return null;
      }

    }
    return url;
  }
}