package fr.slals.view.tool;

import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import fr.slals.data.Task;
import fr.slals.view.treeview.ProjectItem;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ProjectTab extends Tab {
	
	private ProjectItem project;

	private VBox mainPane;
	private TextArea txtDesc;
	private StackPane fileListPane;
	private FileList fileList;
	private TaskTable taskTable;

	/**
	 * @param name
	 */
	public ProjectTab(ProjectItem project) {
		super(project.getProject().getTitle());
		
		fileListPane = new StackPane();
		
		this.project = project;
		
		fileList = new FileList(project.getProject().getFiles());
		taskTable = new TaskTable(project.getProject().getTasks());
		
		Button btnAddFile = new Button("+");
		btnAddFile.getStyleClass().add("add-file-button");
		btnAddFile.setOnAction((btnEvent) -> {
			FileChooser fChooser = new FileChooser();
			fChooser.setTitle("Pick the file you want to link to the project : " + project.getProject().getTitle());
			List<File> filesChosen = fChooser.showOpenMultipleDialog(null);
			
			if(filesChosen != null) {
				for(File file : filesChosen) {
					project.getProject().addFile(file);
				}
			}
			
			fileList.setFileList(project.getProject().getFiles());
		});
		
		fileListPane.getChildren().add(fileList);
		fileListPane.getChildren().add(btnAddFile);
		
		StackPane.setAlignment(btnAddFile, Pos.TOP_LEFT);
		
		initProjectTab();
		
		MenuItem addTaskMenu = new MenuItem("New task");
		addTaskMenu.setOnAction((event) -> {
			Task task = new Task("New task", project.getProject());
			TaskCell taskCell = new TaskCell(task);
			
			project.getProject().getTasks().add(task);
			task.updateProject();
			
			taskTable.addTaskCell(taskCell);
			taskTable.getSelectionModel().select(taskCell);
		});
		taskTable.addMenu(addTaskMenu);
	}
	
	private void initProjectTab() {
		mainPane = new VBox();
		mainPane.setSpacing(20);
		mainPane.getStyleClass().add("project-tab");
		
		txtDesc = new TextArea();
		txtDesc.setText(project.getProject().getDescription());
		txtDesc.setWrapText(true);
		txtDesc.setMinHeight(150);
		txtDesc.setOnKeyReleased((event) -> {
			project.getProject().setDescription(txtDesc.getText());
		});
		
		taskTable.setPrefHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		taskTable.setPrefWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		
		mainPane.getChildren().add(txtDesc);
		mainPane.getChildren().add(fileListPane);
		mainPane.getChildren().add(taskTable);
		
		setContent(mainPane);
	}
	
	/**
	 * @return project item
	 */
	public ProjectItem getProjectItem() {
		return project;
	}
	
	/**
	 * @return main panel
	 */
	public VBox getMainPane() {
		return mainPane;
	}
	
	/**
	 * @return project's description
	 */
	public String getDescription() {
		return txtDesc.getText();
	}
	
	/**
	 * @return files list
	 */
	public FileList getFileList() {
		return fileList;
	}
	
	/**
	 * @param project
	 */
	public void setProject(ProjectItem project) {
		this.project = project;
	}
	
}
