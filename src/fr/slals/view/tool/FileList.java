package fr.slals.view.tool;

import java.io.File;

import javafx.scene.control.ContentDisplay;
import fr.slals.core.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class FileList extends ListView<File> {

	private ObservableList<File> files;
	
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
		setMinHeight(100);
		setOrientation(Orientation.HORIZONTAL);
		
		setCellFactory((lv) -> {
			ListCell<File> cell = new ListCell<>();
			
			ContextMenu contextMenu = new ContextMenu();
				
			MenuItem deleteMenu = new MenuItem("Delete");
			deleteMenu.setOnAction((event) -> {
				files.remove(getSelectionModel().getSelectedItem());
			});
			
			contextMenu.getItems().add(deleteMenu);
			
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
	}
	
	public ImageView createView(File file) {
		ImageView img = new ImageView();
		
		// Find the good icon the use configured
		String propKey = file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase();
		if(file.exists()) {
			if(TaskManager.PROPERTIES.containsKey(propKey)) {
				img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\" + 
								TaskManager.PROPERTIES.getProperty(propKey) + ".png"));
			} else {
				img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\default.png"));
			}
			
			img.setOnMouseClicked((event) -> {
				if(event.getButton() == MouseButton.PRIMARY) {
					try {
						Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
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
}
