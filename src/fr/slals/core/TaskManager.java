package fr.slals.core;

import fr.slals.data.Data;
import fr.slals.view.App;
import javafx.application.Application;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class TaskManager {
	
	public static TaskManagerProperties PROPERTIES = new TaskManagerProperties();
	
	public static Data TREE_DATA = Data.getTreeData();
	
	public static void main(String[] args) {
		Application.launch(App.class);
	}
}
