package fr.slals.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class Project extends Observable {

	private ObservableList<File> files;
	private ArrayList<Task> tasks;
	private String title;
	private String description;
	
	/**
	 * @param title
	 * @param description
	 */
	public Project(String title, String description) {
		this.title = title;
		this.description = description;
		
		files = FXCollections.observableArrayList();
		tasks = new ArrayList<Task>();
	}
	
	public Project(String title) {
		this.title = title;
		this.description = "";
		
		files = FXCollections.observableArrayList();
		tasks = new ArrayList<Task>();
	}
	
	/**
	 * @param title
	 * @param description
	 * @param files
	 * @param tasks
	 */
	public Project(String title, String description, ObservableList<File> files, ArrayList<Task> tasks) {
		this.title = title;
		this.description = description;
		
		if(files == null) {
			this.files = files = FXCollections.observableArrayList();
		} else {
			this.files = files;
		}
		
		if(tasks == null) {
			this.tasks = new ArrayList<Task>();
		} else {
			this.tasks = tasks;
		}
	}
	
	/**
	 * @param file name
	 */
	public void addFile(File file) {
		files.add(file);
	}
	
	/**
	 * @param task
	 */
	public void addTask(Task task) {
		tasks.add(task);
	}

	/**
	 * @return description of the project
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return files linked to the project
	 */
	public ObservableList<File> getFiles() {
		return files;
	}

	/**
	 * @return tasks linked to the project
	 */
	public ArrayList<Task> getTasks() {
		return tasks;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setChanged() {
		super.setChanged();
	}
	
	public double getProgression() {
		double nbTaskDone = 0;
		for(Task task : tasks) {
			if(task.isDone()) {
				nbTaskDone++;
			}
		}
		
		if(nbTaskDone > 0) {
			return nbTaskDone / (double)tasks.size();
		}
		
		return 0;
	}
}
