package fr.slals.view;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import fr.slals.core.TaskManager;
import fr.slals.view.conf.ConfIcon;
import fr.slals.view.conf.ConfPath;
import fr.slals.view.treeview.Tree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class App extends Application {
	
	private BorderPane mainPane;
	private Tree tree;
	private TabPane projectPane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scn = new Scene(mainPane);
		
		mainPane.setPrefSize(1000, 800);
		
		scn.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		
		scn.setFill(Color.web("#A0C0CC"));
		
		primaryStage.setTitle("Task manager");
		primaryStage.setScene(scn);
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		try {
			TaskManager.TREE_DATA.saveData(tree);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws Exception {
		mainPane = new BorderPane();
		
		HBox contentPane = new HBox();
		contentPane.setPadding(new Insets(30, 10, 10, 10));
		contentPane.setSpacing(20);
		
		projectPane = new TabPane();
		
		tree = new Tree(projectPane);
		tree.setMinWidth(250);
		
		projectPane.setMinWidth(600);
		projectPane.setVisible(true);
		
		contentPane.getChildren().add(tree);
		contentPane.getChildren().add(projectPane);
		
		mainPane.getStyleClass().add("mainpanel");
		
		mainPane.setTop(createMenuBar());
		mainPane.setCenter(contentPane);
	}
	
	private MenuBar createMenuBar() {
		// Menu bar
		MenuBar menuBar = new MenuBar();
		
		// Menu File
		Menu menuFile = new Menu("File");

		MenuItem quit = new MenuItem("Quit");
		quit.setOnAction((event) -> {
			Platform.exit();
		});
		
		menuFile.getItems().add(quit);
		
		// Menu Preferences
		Menu menuPref = new Menu("Preferences");
		
		MenuItem paths = new MenuItem("Paths...");
		paths.setOnAction((event) -> {
			new ConfPath();
		});
		
		MenuItem icons = new MenuItem("Files icon...");
		icons.setOnAction((event) -> {
			new ConfIcon();
		});
		
		menuPref.getItems().add(paths);
		menuPref.getItems().add(icons);
		
		menuBar.getMenus().add(menuFile);
		menuBar.getMenus().add(menuPref);
		
		return menuBar;
	}
	
	/**
	 * @return project tab panel
	 */
	public TabPane getProjectPane() {
		return projectPane;
	}
}
