package de.sbtab.view;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import org.sbml.jsbml.SBMLDocument;

import de.sbtab.controller.*;
import de.sbtab.main.SBTabMain;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.control.Menu;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.prefs.Preferences;
public class SBTabMenuController extends SBTabMainView implements Initializable {

  public SBTabMenuController() {
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    if (!fileLoaded){
      ViewMenu.setDisable(true);
      EditMenu.setDisable(true);//Disable unnecessary Menus while no file is loaded
      SaveItem.setDisable(true);
      ValidateItem.setDisable(true);
      ExportItem.setDisable(true);
    }

  }
  // View and Edit Menu as objects:
  @FXML
  private Menu ViewMenu;

  @FXML
  private Menu EditMenu;


  //file MenuItems:

  @FXML
  private MenuItem NewItem;

  @FXML
  private MenuItem OpenItem;

  @FXML
  private MenuItem SaveItem;

  @FXML
  private MenuItem QuitItem;

  @FXML
  private MenuItem ImportItem;

  @FXML
  private MenuItem ExportItem;

  @FXML
  private MenuItem ValidateItem;


  // edit MenuItems:
  @FXML
  private MenuItem UndoItem;

  @FXML
  private MenuItem RedoItem;

  @FXML
  private MenuItem CopyItem;

  @FXML
  private MenuItem PasteItem;

  @FXML
  private MenuItem CutItem;

  // view MenuItems:
  @FXML
  private MenuItem FieldSizeItem;

  @FXML
  private MenuItem HideColumnsItem;

  @FXML
  private MenuItem ShowHiddenColumnsItem;

  @FXML
  private MenuItem ZoomInItem;

  @FXML
  private MenuItem ZoomOutItem;

  @FXML
  private MenuItem SetToItem;

  // Help MenuItems:
  @FXML
  private MenuItem DocumentationItem;

  @FXML
  private MenuItem WebSearchItem;

  // file menu action Methods:

  @FXML
  void doNew(ActionEvent event) {
    handleNew();
  }

  @FXML
  void doOpen(ActionEvent event) {
	if (!fileLoaded){
    doc = handleOpen();
    // TODO: change when tree and more views are implemented
    if (doc!=null) {
    	fileLoaded=true;
        reInit();
        //Enable full Menu
        ViewMenu.setDisable(false);
        EditMenu.setDisable(false);
        SaveItem.setDisable(false);
        ValidateItem.setDisable(false);
        ExportItem.setDisable(false);
    }
        
    }
	else{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		  Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
	      stage.getIcons().add(new Image(this.getClass().getResourceAsStream("Icon_32.png")));
	      alert.setTitle("Open another file");
	      alert.setHeaderText("To open another file a new Session of TabMod must be started");//TODO: Add appropriate text/ Implement abstract dialogs
	      alert.setContentText("Do you want to start a new Session to open another file?");
	      

	      ButtonType buttonTypeNew = new ButtonType("new Session");
	      ButtonType buttonTypeCancel = new ButtonType("Cancel");

	      alert.getButtonTypes().setAll(buttonTypeNew, buttonTypeCancel);

	      Optional<ButtonType> result = alert.showAndWait();
	      if (result.get() == buttonTypeNew){
	    	  SBMLDocument newDoc=handleOpen(); 
	         //TODO: Implement opening a new window in a new thread.
	      }
	      else {
	      }
		
	}
  }

  @FXML
  void doSave(ActionEvent event) {
    handleSave();
  }

  @FXML
  void doImport(ActionEvent event) {
  }

  @FXML
  void doExport(ActionEvent event) {

  }

  @FXML
  void doValidate(ActionEvent event) {
	  boolean valid=SBTabController.validate(doc);//TODO: Implement validate
	  if (valid){
		  Alert alert = new Alert(AlertType.CONFIRMATION);
		  Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
	      stage.getIcons().add(new Image(this.getClass().getResourceAsStream("Icon_32.png")));
		  alert.setTitle("Validator");
		  alert.setHeaderText(null);
		  alert.setContentText("Your file is a valid .sbml file");
		  alert.showAndWait();
		  
	  }
	  else{
		  Alert alert = new Alert(AlertType.CONFIRMATION);
		  Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
	      stage.getIcons().add(new Image(this.getClass().getResourceAsStream("Icon_32.png")));
		  alert.setTitle("Validator");
		  alert.setHeaderText(null);
		  alert.setContentText("Your Document has " + SBTabController.numErrors(doc) + " Errors");
		  alert.showAndWait();
	  }

  }

  @FXML
  void doQuit(ActionEvent event) {
    //TODO: check for unsaved changes
    boolean unsavedChanges= true;
    if (unsavedChanges){
      Alert alert = new Alert(AlertType.CONFIRMATION);
      Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
      stage.getIcons().add(new Image(this.getClass().getResourceAsStream("Icon_32.png")));
      alert.setTitle("Unsaved Changes");
      alert.setHeaderText("Your file has unsaved changes");
      alert.setContentText("Do you want to save your changes?");

      ButtonType buttonTypeSave = new ButtonType("Save Changes");
      ButtonType buttonTypeDontSave = new ButtonType("Don't Save Changes");
      ButtonType buttonTypeCancel = new ButtonType("Cancel");

      alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDontSave, buttonTypeCancel);

      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == buttonTypeSave){

        //TODO: implement save.
        System.exit(0);
      } else if (result.get() == buttonTypeDontSave) {
        System.exit(0);
      } else {
      }
    }
  }

  // edit menu action methods
  @FXML
  void doUndo(ActionEvent event) {

  }

  @FXML
  void doRedo(ActionEvent event) {

  }

  @FXML
  void doCopy(ActionEvent event) {

  }

  @FXML
  void doPaste(ActionEvent event) {
  }

  @FXML
  void doCut(ActionEvent event) {
  }

  // View menu action methods:
  @FXML
  void doZoomIn(ActionEvent event) {

  }
  @FXML
  void doZoomOut(ActionEvent event) {

  }
  @FXML
  void doSetTo(ActionEvent event) {

  }

  @FXML
  void doHideColumns(ActionEvent event) {
  }

  @FXML
  void doShowHiddenColumns(ActionEvent event) {
  }
  // Help menu action methods:

  @FXML
  void doDocumentation(ActionEvent event) {
  }

  @FXML
  void doWebSearch(ActionEvent event) {
  }

  // Handler methods:

  private void handleNew() {
    Task<Void> task = new Task<Void>() {
      @Override
      public Void call() {
        try {
          System.out.println("new");
          // TODO new
        } catch (Exception e) {
          e.printStackTrace();
        }

        return null;
      }
    };
    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();
  }

  public static SBMLDocument handleOpen() {
    String filePath = chooseFile();
    if (filePath!=null) {
      SBMLDocument doc = SBTabController.read(filePath);
      return doc;
    }
    return null;
  }

  private void handleSave() {
    SBMLDocument doc = SBTabController.getDoc();
    File filePath = new File(SBTabController.getFilePath());
    String theProjectName = SBTabMenuController.getTheProjectName();
    String theVersion = SBTabMenuController.getTheVersion();
    SBTabController.save(doc, filePath, theProjectName, theVersion);
  }
  /*
   * Choose file from file dialog and get the file path.
   */
  private static String chooseFile(){
    Reader reader = null;
    Properties theProperties = new Properties();
    try
    {
      if(!(new File (".properties").exists())){
        SBTabController.setProperties();
      }
        reader = new FileReader( ".properties" );
        theProperties.load( reader );
        theProperties.list( System.out );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    final FileChooser fileChooser = new FileChooser();
    String filePath = "";
    fileChooser.getExtensionFilters().addAll(
      new ExtensionFilter("XML Files", "*.xml"),
      new ExtensionFilter("SBML Files", "*.SBML"));
    fileChooser.setTitle("Choose SBML or XML File.");
    fileChooser.setInitialDirectory(new File(theProperties.getProperty("FilePath"))) ;
    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      filePath = file.getAbsolutePath();
      return filePath;
    }
    return null;
  }
}
