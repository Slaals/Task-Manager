package fr.slals.view.tool;

import java.io.File;
import java.util.ArrayList;

import fr.slals.core.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class FileList extends ListView<ImageView> {
	
	ObservableList<ImageView> data;
	
	private ArrayList<File> files;
	
	/**
	 * @param files
	 */
	public FileList(ArrayList<File> files) {
		super();
		
		init();
		
		addFileList(files);
	}

	public FileList() {
		super();
		
		init();
	}
	
	private void init()	{
		setMinHeight(100);
		setOrientation(Orientation.HORIZONTAL);
		
		data = FXCollections.observableArrayList();
	}
	
	/**
	 * @param files
	 */
	public void addFileList(ArrayList<File> files) {
		this.files = files;
		for(File file : files) {
			addImageView(file);
		}
		setItems(data);
		
		setOnMousePressed((event) -> {
			Event.fireEvent(getSelectionModel().getSelectedItem(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                    0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                    true, true, true, true, true, true, null));
		});
	}
	
	public void addImageView(File file) {
		ImageView img = new ImageView();
		
		// Find the good icon the use configured
		String propKey = file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase();
		if(TaskManager.PROPERTIES.containsKey(propKey)) {
			img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\" + 
							TaskManager.PROPERTIES.getProperty(propKey) + ".png"));
		} else {
			img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\default.png"));
		}
		
		img.setOnMouseClicked((event) -> {
			try {
				Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		data.add(img);
	}
	
	/**
	 * @param files
	 */
	public void refreshFileList(ArrayList<File> files) {
		getItems().clear();
		addFileList(files);
	}
	
	/**
	 * @return files
	 */
	public ArrayList<File> getFiles() {
		return files;
	}
}
