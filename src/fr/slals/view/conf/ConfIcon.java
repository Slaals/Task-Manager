package fr.slals.view.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import fr.slals.core.TaskManager;
import fr.slals.view.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ConfIcon extends Stage {
	
	private GridPane content;
	
	// Make a link between an image and her name (since Image can't have this information)
	private HashMap<Image, String> imgName;
	
	private ListView<String> extList;
	private TextField extField;
	private Button btnAdd;
	
	private ObservableList<String> ext = FXCollections.observableArrayList();
	
	public ConfIcon() {
		super();
		initStyle(StageStyle.UTILITY);
		
		imgName = new HashMap<Image, String>();
		
		content = new GridPane();
		content.setAlignment(Pos.CENTER);
		
		content.getStyleClass().add("background");
		
		init();
		
		Scene scene = new Scene(content);
		
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		
		setTitle("Files icon configuration");
		setMinHeight(300);
		setMinWidth(500);
		
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void init() {
		ObservableList<Image> images = fetchImages();
		
		ComboBox<Image> imgChoice = new ComboBox<Image>();
		imgChoice.getItems().addAll(images);
		imgChoice.setButtonCell(new ImageListCell());
		imgChoice.setCellFactory(listView -> new ImageListCell());
		
		Button btnAddIcon = new Button("Add icon...");
		btnAddIcon.setMaxWidth(Double.MAX_VALUE);
		btnAddIcon.getStyleClass().add("custom-button");
		
		extField = new TextField();
		extField.setMaxWidth(100);
		extField.getStyleClass().add("field-ext");
		
		btnAdd = new Button("+");
		btnAdd.setDisable(true);
		btnAdd.setMaxHeight(Double.MAX_VALUE);
		
		extList = new ListView<String>(ext);
		extList.setMaxWidth(100);
		extList.setMaxHeight(100);
		
		extField.setOnKeyReleased(event -> {
			if(controlValidity()) {
				if(event.getCode() == KeyCode.ENTER) {
					String extText = extField.getText();
					
					// Save the properties this way : "Extension"="img name"
					TaskManager.PROPERTIES.setProperty("EXT_" + extText.toUpperCase(), 
								imgName.get(imgChoice.getSelectionModel().getSelectedItem()));
					TaskManager.PROPERTIES.setExt(extText);
					
					ext.add(extText.toUpperCase());
						
					TaskManager.PROPERTIES.save();
					
					extField.setText("");
					extField.requestFocus();
					
					extField.getStyleClass().remove("valide");
					
					btnAdd.setDisable(true);
				}
			}
		});
		
		imgChoice.setOnAction(event -> {
			ext.clear();
			Image selectedImg = imgChoice.getSelectionModel().getSelectedItem();
			
			for(String imgExt : TaskManager.PROPERTIES.getExts(imgName.get(selectedImg))) {
				ext.add(imgExt);
			}
		});
		
		btnAdd.setOnAction((event) -> {
			String extText = extField.getText();
			
			// Save the properties this way : "Extension"="img name"
			TaskManager.PROPERTIES.setProperty("EXT_" + extText.toUpperCase(), 
						imgName.get(imgChoice.getSelectionModel().getSelectedItem()));
			TaskManager.PROPERTIES.setExt(extText);
			
			ext.add(extText.toUpperCase());
				
			TaskManager.PROPERTIES.save();
			
			extField.setText("");
			extField.requestFocus();
			
			extField.getStyleClass().remove("valide");
			
			btnAdd.setDisable(true);
		});
		
		btnAddIcon.setOnAction((event) -> {
			FileChooser fChooser = new FileChooser();
			fChooser.setTitle("Pick the icon you want to add");
			fChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (png)", "*.png"));
			List<File> filesChosen = fChooser.showOpenMultipleDialog(null);
			
			if(filesChosen != null) {
				for(File file : filesChosen) {
					Image img = new Image("file:" + file.getAbsolutePath());
					
					// Only .png 64x64
					if(img.getWidth() == 64 && img.getHeight() == 64) {
						String newName = loadImage(file);
						images.add(img);
						
						// Refresh
						imgChoice.getItems().clear();
						imgChoice.getItems().addAll(images);
						imgChoice.getSelectionModel().selectLast();
						
						imgName.put(img, newName);
					}
				}
			}
		});
		
		extList.setCellFactory((lv) -> {
			ListCell<String> cell = new ListCell<>();
			
			ContextMenu contextMenu = new ContextMenu();
				
			MenuItem deleteMenu = new MenuItem("Delete");
			deleteMenu.setOnAction((event) -> {
				TaskManager.PROPERTIES.deleteExt(extList.getSelectionModel().getSelectedItem());
				ext.remove(extList.getSelectionModel().getSelectedItem());
			});
			
			cell.itemProperty().addListener((obs, oldItem, newItem) -> {
				if(newItem != null) {
					Text txt = new Text(newItem);
					txt.setFill(Color.web("#FFFFFF"));
					cell.setGraphic(txt);
				}
			});
			
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if(isNowEmpty) {
					cell.setContextMenu(null);
					cell.setGraphic(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			
			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			
			contextMenu.getItems().add(deleteMenu);
			
			return cell;
		});
		
		imgChoice.setMaxWidth(Double.MAX_VALUE);
		imgChoice.setMinHeight(75);
		
		content.setHgap(10);
		content.setVgap(10);
		
		Separator sep = new Separator();
		sep.setOrientation(Orientation.VERTICAL);
		
		content.add(imgChoice, 0, 1);
		content.add(btnAddIcon, 0, 2);
		content.add(sep, 1, 0);
		content.add(extField, 2, 0);
		content.add(btnAdd, 3, 0);
		content.add(extList, 2, 1);
		
		GridPane.setRowSpan(sep, 3);
		GridPane.setRowSpan(extList, 2);
	}
	
	private boolean controlValidity() {
		if(!TaskManager.PROPERTIES.extExists(extField.getText()) && !extField.getText().isEmpty()) {
			extField.getStyleClass().remove("valide");
			extField.getStyleClass().add("valide");
			
			btnAdd.setDisable(false);
			
			return true;
		} else {
			extField.getStyleClass().remove("valide");
			
			btnAdd.setDisable(true);
			
			return false;
		}
	}
	
	/**
	 * Load the file to a proper location
	 * @param file
	 * @return file name
	 */
	private String loadImage(File file) {
		InputStream inStream = null;
		OutputStream outStream = null;
		
		String src = TaskManager.RESOURCE_PATH + "\\icons\\";
		String name = "";
		
		int indName = 0;
		
		try {
			name = file.getName();
			File srcFile = new File(src + name);
			
			// Try to find a unique name
			while(srcFile.exists()) {
				srcFile = new File(src + name + indName);
			}
			
			inStream = new FileInputStream(file);
			outStream = new FileOutputStream(srcFile);
			
			byte[] buffer = new byte[1024];
			
			int length;
			
			while((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			
			inStream.close();
			outStream.close();
			
		} catch(IOException io) {
			io.printStackTrace();
		}
		
		return name;
	}
	
	/**
	 * @return data
	 */
	private ObservableList<Image> fetchImages() {
		final ObservableList<Image> images = FXCollections.observableArrayList();
		File dirIcons = new File(TaskManager.RESOURCE_PATH + "\\icons");
		
		for(File file : dirIcons.listFiles()) {
			Image img = new Image("file:" + file.getAbsolutePath());
			images.add(img);
			
			imgName.put(img, file.getName().substring(0, file.getName().indexOf('.')));
		}
		
		return images;
	}
	
	class ImageListCell extends ListCell<Image> {
		private final ImageView view;
		
		public ImageListCell() {
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setCursor(Cursor.HAND);
			view = new ImageView();
		}
		
		@Override
		protected void updateItem(Image item, boolean empty) {
			super.updateItem(item,  empty);
			
			if(item == null || empty) {
				setGraphic(null);
			} else {
				view.setImage(item);
				setGraphic(view);
			}
		}
	}

}
