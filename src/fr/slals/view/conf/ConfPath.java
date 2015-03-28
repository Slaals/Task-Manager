package fr.slals.view.conf;

import java.io.File;

import fr.slals.core.TaskManager;
import fr.slals.view.App;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ConfPath extends Stage {
	
	private GridPane content;

	public ConfPath() {
		super();
		initStyle(StageStyle.UTILITY);
		
		content = new GridPane();
		content.setAlignment(Pos.CENTER);
		
		content.getStyleClass().add("background");
		
		init();
		
		Scene scene = new Scene(content);
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		
		setTitle("Paths configuration");
		setMinHeight(300);
		setMinWidth(500);
		
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void init() {
		TextField pathField = new TextField();
		pathField.setEditable(false);
		pathField.setPrefWidth(300);
		
		pathField.setText(TaskManager.PROPERTIES.getProperty("DATA_PATH"));
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose the path for your data");
		dirChooser.setInitialDirectory(new File(pathField.getText()));
		
		Button btnFileChooser = new Button("...");
		btnFileChooser.setOnAction((event) -> {
			File directory = dirChooser.showDialog(new Stage());
			
			if(directory != null) {
				pathField.setText(directory.getAbsolutePath());
				TaskManager.PROPERTIES.setProperty("DATA_PATH", pathField.getText());
				
				TaskManager.PROPERTIES.save();
			}
		});
		
		content.setHgap(15);
		
		content.add(new Label("Data file path : "), 0, 0);
		content.add(pathField, 1, 0);
		content.add(btnFileChooser, 2, 0);
	}
}
