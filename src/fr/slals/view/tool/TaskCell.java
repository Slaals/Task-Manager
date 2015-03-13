package fr.slals.view.tool;

import java.util.Calendar;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import fr.slals.core.TaskManager;
import fr.slals.data.Task;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class TaskCell {
	
	private Task task;
	
	private ImageView imgView;
	
	private Image imgDone = new Image("file:" + TaskManager.RESOURCE_PATH + "done.png");
	private Image imgTodo = new Image("file:" + TaskManager.RESOURCE_PATH + "todo.png");
	
	private StringProperty label = new SimpleStringProperty();
	private ObjectProperty<ImageView> done = new SimpleObjectProperty<ImageView>();
	private StringProperty date = new SimpleStringProperty();
	
	public TaskCell(Task task) {
		label.set(task.getLabel());
		date.set(task.getDate());
		
		imgView = new ImageView();
		
		if(task.isDone()) {
			imgView.setImage(imgDone);
		} else {
			imgView.setImage(imgTodo);
		}
		
		imgView.setFitHeight(25);
		imgView.setFitWidth(25);
		
		done.set(imgView);
		
		this.task = task;
	}
	
	/**
	 * @return label string
	 */
	public String getLabel() {
		return label.get();
	}

	/**
	 * @param label
	 */
	public void setLabel(String label) {
		task.setLabel(label);
		this.label.set(label);
	}
	
	/**
	 * @return label string property
	 */
	public StringProperty getLabelProperty() {
		return label;
	}

	/**
	 * @return Image view
	 */
	public ImageView isDone() {
		return done.get();
	}

	public void setDone() {
		Calendar calendar = Calendar.getInstance();
		task.setDone(!task.isDone());
		if(task.isDone()) {
			setDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" +  calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
			imgView.setImage(imgDone);
		} else {
			setDate("");
			imgView.setImage(imgTodo);
		}
	}
	
	/**
	 * @return doneView
	 */
	public ObjectProperty<ImageView> getDoneProperty() {
		return done;
	}

	/**
	 * @return date string
	 */
	public String getDate() {
		return date.get();
	}

	/**
	 * @param date
	 */
	public void setDate(String date) {
		task.setDate(date);
		this.date.set(date);
	}
	
	/**
	 * @return task
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * @return date property
	 */
	public StringProperty getDateProperty() {
		return date;
	}
	
	/**
	 * Remove the task from the data
	 */
	public void deleteTask() {
		task.delete();
	}
}
