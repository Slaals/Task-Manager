package fr.slals.view.tool;

import java.awt.Toolkit;

import fr.slals.data.Task;
import fr.slals.view.treeview.ProjectItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ProjectTab extends Tab {
	
	private ProjectItem project;

	private VBox mainPane;
	private TextArea txtDesc;
	private FileList fileList;
	private TaskTable taskTable;

	/**
	 * @param name
	 */
	public ProjectTab(ProjectItem project) {
		super(project.getProject().getTitle());
		
		this.project = project;
		
		fileList = new FileList(project.getProject().getFiles());
		taskTable = new TaskTable(project.getProject().getTasks());
		
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
		mainPane.getChildren().add(fileList.getFileListView());
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
