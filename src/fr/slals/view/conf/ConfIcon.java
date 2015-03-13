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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
	
	// Make a link betwin an image and her name (since Image can't have this information)
	private HashMap<Image, String> imgName;
	
	public ConfIcon() {
		super();
		initStyle(StageStyle.UTILITY);
		
		imgName = new HashMap<Image, String>();
		
		content = new GridPane();
		content.setAlignment(Pos.CENTER);
		
		init();
		
		Scene scene = new Scene(content);
		
		setTitle("Files icon configuration");
		setMinHeight(300);
		setMinWidth(500);
		
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void init() {
		GridPane formPane = new GridPane();
		
		Label extLabel = new Label("Extension :");
		
		Button btnLink = new Button("Link");
		
		TextField txtExt = new TextField();
		
		formPane.add(extLabel, 0, 0);
		formPane.add(btnLink, 1, 0);
		formPane.add(txtExt, 0, 1);
		
		GridPane.setColumnSpan(txtExt, 2); // Cells merge
		
		ObservableList<Image> images = fetchImages();
		
		ComboBox<Image> imgChoice = new ComboBox<Image>();
		imgChoice.getItems().addAll(images);
		imgChoice.setButtonCell(new ImageListCell());
		imgChoice.setCellFactory(listView -> new ImageListCell());
		imgChoice.getSelectionModel().select(0);
		
		Button btnAddIcon = new Button("Add icon...");
		btnAddIcon.setMaxWidth(Double.MAX_VALUE);
		
		btnLink.setOnAction((event) -> {
			// Save the properties this way : "Extension"="img name"
			if(!txtExt.getText().isEmpty()) {
				TaskManager.PROPERTIES.setProperty(txtExt.getText().toUpperCase(), 
						imgName.get(imgChoice.getSelectionModel().getSelectedItem()));
				TaskManager.PROPERTIES.save();
			}
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
		
		imgChoice.setMaxWidth(Double.MAX_VALUE);
		imgChoice.setMinHeight(75);

		content.add(formPane, 0, 0);
		content.add(imgChoice, 1, 0);
		content.add(btnAddIcon, 1, 1);
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
