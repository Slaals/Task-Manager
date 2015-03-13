package fr.slals.view.tool;

import java.io.File;
import java.util.ArrayList;

import fr.slals.core.TaskManager;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class FileList extends ScrollPane {
	
	private HBox content;
	
	private ArrayList<File> files;
	
	/**
	 * @param files
	 */
	public FileList(ArrayList<File> files) {
		super();
		
		setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		setMinHeight(100);
		setPadding(new Insets(5));
		
		init();
		
		addFileList(files);
		
		setContent(content);
	}

	public FileList() {
		super();
		
		setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		setMinHeight(100);
		setPadding(new Insets(5));
		
		init();
		
		setContent(content);
	}
	
	private void init()	{
		content = new HBox();
		content.setSpacing(25);
	}
	
	/**
	 * @param files
	 */
	public void addFileList(ArrayList<File> files) {
		this.files = files;
		for(File file : files) {
			ImageView img = new ImageView();
			
			// Find the good icon the use configured
			String propKey = file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase();
			if(TaskManager.PROPERTIES.containsKey(propKey)) {
				img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\" + 
								TaskManager.PROPERTIES.getProperty(propKey) + ".png"));
			} else {
				img.setImage(new Image("file:" + TaskManager.RESOURCE_PATH + "\\icons\\default.png"));
			}
			
			img.setCursor(Cursor.HAND);
			
			img.setOnMouseClicked((event) -> {
				try {
					Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			Tooltip fileName = new Tooltip(file.getName());
			Tooltip.install(img, fileName);
			
			content.getChildren().add(img);
		}
	}
	
	/**
	 * @param files
	 */
	public void refreshFileList(ArrayList<File> files) {
		content.getChildren().clear();
		addFileList(files);
	}
	
	/**
	 * @return the HBox pane
	 */
	public HBox getPane() {
		return content;
	}
	
	/**
	 * @return files
	 */
	public ArrayList<File> getFiles() {
		return files;
	}
}
