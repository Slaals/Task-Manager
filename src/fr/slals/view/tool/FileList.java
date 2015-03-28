package fr.slals.view.tool;

import java.io.File;
import java.util.List;


import javafx.scene.control.ContentDisplay;
import fr.slals.core.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class FileList extends ListView<File> {

	private ObservableList<File> files;
	
	private StackPane listPane;
	
	/**
	 * @param files
	 */
	public FileList(ObservableList<File> files) {
		super();
		
		init();
		
		setFileList(files);
		
		setItems(files);
	}

	public FileList() {
		super();
		
		init();
		
		files = FXCollections.observableArrayList();
		
		setItems(files);
	}
	
	private void init()	{
		listPane = new StackPane();
		
		setMinHeight(100);
		setOrientation(Orientation.HORIZONTAL);
		
		Button btnAddFile = new Button("+");
		btnAddFile.getStyleClass().add("add-file-button");
		btnAddFile.setOnAction((btnEvent) -> {
			FileChooser fChooser = new FileChooser();
			fChooser.setTitle("Pick the file you want to link to the project");
			List<File> filesChosen = fChooser.showOpenMultipleDialog(null);
			
			if(filesChosen != null) {
				for(File file : filesChosen) {
					files.add(file);
				}
			}
		});
		
		setCellFactory((lv) -> {
			ListCell<File> cell = new ListCell<>();
			
			ContextMenu contextMenu = new ContextMenu();
				
			MenuItem deleteMenu = new MenuItem("Delete");
			deleteMenu.setOnAction((event) -> {
				files.remove(getSelectionModel().getSelectedItem());
			});
			
			contextMenu.getItems().add(deleteMenu);
			
			cell.setOnMouseClicked(event -> {
				if(event.getButton() == MouseButton.PRIMARY) {
					try {
						Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", 
								cell.getItem().getAbsolutePath()});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			cell.itemProperty().addListener((obs, oldItem, newItem) -> {
				if(newItem != null) {
					ImageView img = createView(files.get(files.indexOf(newItem)));
					if(img != null) {
						cell.setGraphic(img);
					}
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
			
			return cell;
		});
		
		listPane.getChildren().add(this);
		listPane.getChildren().add(btnAddFile);
		
		StackPane.setAlignment(btnAddFile, Pos.TOP_LEFT);
	}
	
	public ImageView createView(File file) {
		ImageView img = new ImageView();
		
		// Find the good icon the use configured
		String propKey = file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase();
		if(file.exists()) {
			if(TaskManager.PROPERTIES.containsKey("EXT_" + propKey)) {
				img.setImage(new Image(getClass().getClassLoader().getResource("resources/src/icons/").toString() + 
								TaskManager.PROPERTIES.getProperty("EXT_" + propKey) + ".png"));
			} else {
				img.setImage(new Image(getClass().getClassLoader().getResource("resources/src/icons/default.png").toString()));
			}
			
			return img;
		}
		
		return null;
	}
	
	/**
	 * @param files
	 */
	public void setFileList(ObservableList<File> files) {
		this.files = files;
	}
	
	/**
	 * @return files
	 */
	public ObservableList<File> getFiles() {
		return files;
	}
	
	/**
	 * @return
	 */
	public StackPane getFileListView() {
		return listPane;
	}
}
