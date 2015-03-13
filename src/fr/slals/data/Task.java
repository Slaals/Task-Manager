package fr.slals.data;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class Task {
	
	private Project project;

	private String label;
	private boolean done;
	private String date;
	
	/**
	 * @param label task name
	 * @param validated if it's been finished (true) or not (false)
	 * @param date validated date, null if it hasn't been validated
	 */
	public Task(String label, boolean done, String date, Project project) {
		this.label = label;
		this.done = done;
		this.date = date;
		this.project = project;
	}
	
	/**
	 * @param label
	 */
	public Task(String label, Project project) {
		this.label = label;
		this.done = false;
		this.date = "";
		this.project = project;
	}
	
	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return done boolean as String
	 */
	public String getDoneValue() {
		if(done) {
			return "true";
		}
		return "false";
	}
	
	/**
	 * @return done
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * @param done
	 */
	public void setDone(boolean done) {
		this.done = done;
		updateProject();
	}
	
	/**
	 * @return date
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Remove the task from the project
	 */
	public void delete() {
		project.getTasks().remove(this);
		updateProject();
	}
	
	/**
	 * Notify the project
	 */
	public void updateProject() {
		project.setChanged();
		project.notifyObservers();
	}
}
