package fr.slals.view.treeview;

import java.util.Observable;
import java.util.Observer;

import fr.slals.data.Project;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class ProjectItem extends TreeItem<String> implements Observer {

	private Project project;
	private String name;
	
	private ProgressIndicator progInd;
	
	/**
	 * @param project
	 * @param id
	 * @param name
	 */
	public ProjectItem(Project project) {
		super(project.getTitle());
		
		project.addObserver(this);
		
		progInd = new ProgressIndicator(project.getProgression());
		
		setGraphic(progInd);
		
		this.project = project;
		this.name = project.getTitle();
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
