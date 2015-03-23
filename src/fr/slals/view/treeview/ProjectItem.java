package fr.slals.view.treeview;

import java.util.Observable;
import java.util.Observer;

import fr.slals.data.Project;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ProjectItem extends TreeItem<String> implements Observer {
	
	private final String[] progBarClass = {"red-bar", "orange-bar", "yellow-bar", "blue-bar"};

	private Project project;
	private String name;
	
	private ProgressBar progInd;
	
	/**
	 * @param project
	 * @param id
	 * @param name
	 */
	public ProjectItem(Project project) {
		super(project.getTitle());
		
		double progVal = project.getProgression();
		
		project.addObserver(this);
		
		progInd = new ProgressBar(progVal);
		setStyleByValue(progVal);
		progInd.setMaxWidth(50);
		
		progInd.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obsable,
					Number oldValue, Number newValue) {
				double progress = newValue == null ? 0 : newValue.doubleValue();
				setStyleByValue(progress);
			}
		});
		
		setGraphic(progInd);
		
		this.project = project;
		this.name = project.getTitle();
	}
	
	private void setStyleByValue(double progress) {
		if(progress < 0.3) {
			setBarStyleClass(progInd, progBarClass[0]);
		} else if(progress < 0.5) {
			setBarStyleClass(progInd, progBarClass[1]);
		} else if(progress < 0.7) {
			setBarStyleClass(progInd, progBarClass[2]);
		} else {
			setBarStyleClass(progInd, progBarClass[3]);
		}
	}
	
	private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
		bar.getStyleClass().removeAll(progBarClass);
		bar.getStyleClass().add(barStyleClass);
	}
	
	/**
	 * @returnm project linked to this item
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * @return project name
	 */
	public String getName() {
		return name;
	}

	@Override
	public void update(Observable o, Object arg) {
		progInd.setProgress(project.getProgression());
	}
}
