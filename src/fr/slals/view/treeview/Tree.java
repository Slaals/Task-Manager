package fr.slals.view.treeview;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import fr.slals.core.TaskManager;
import fr.slals.data.Project;
import fr.slals.view.tool.ProjectTab;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class Tree extends TreeView<String> {
	
	private TabPane projectPane;
	
	// Contains node id as key and item name as value
	private LinkedHashMap<Integer, String> itemsName;
	
	private TreeItem<String> root;
	
	/**
	 * @param projectPane
	 */
	public Tree(TabPane projectPane) {
		super();
		
		this.projectPane = projectPane;
		
		itemsName = TaskManager.TREE_DATA.getNodesName();
		root = new TreeItem<String>();
		
		createRootItems();
		
		setRoot(root);
		
		setShowRoot(false);
		
		setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> item) {
				
				// Project item listener, open a tab in the project pane
				setOnMouseClicked((event) -> {
					if(event.getSource() instanceof TreeView<?>) {
						TreeItem<?> selectedItem = (TreeItem<?>) ((TreeView<?>) event.getSource()).getSelectionModel().getSelectedItem();
						if(event.getClickCount() == 2 && selectedItem instanceof ProjectItem) {
							
							boolean found = false;
							
							// Select the tab if it's already open
							for(Tab tab : projectPane.getTabs()) {
								ProjectTab projectTab = (ProjectTab) tab;
								
								if(projectTab.getProjectItem().equals(selectedItem)) {
									projectPane.getSelectionModel().select(projectTab);
									found = true;
									break;
								}
							}
							
							// If the tab hasn't been opened yet then open it
							if(!found) {
								ProjectTab tab = new ProjectTab((ProjectItem) selectedItem);
								
								projectPane.getTabs().add(tab);
								projectPane.getSelectionModel().select(tab);
								
								tab.setOnSelectionChanged((eventSelect) -> {
									if(tab.isSelected()) {
										getSelectionModel().select(tab.getProjectItem());
									}
								});
							}
						}
					}
				});
				
				return new TextFieldTreeCell();
			}
		});
	}
	
	/**
	 * @return Items tree created, level 0
	 */
	public void createRootItems() {
		HashMap<Integer, LinkedHashMap<Integer, Integer>> data = TaskManager.TREE_DATA.getData();
		HashMap<Integer, TreeItem<String>> createdItems = new HashMap<Integer, TreeItem<String>>();
		for(Map.Entry<Integer, Integer> entry : data.get(0).entrySet()) {
			TreeItem<String> item = new TreeItem<String>(itemsName.get(entry.getKey()));
			root.getChildren().add(item);
			createdItems.put(entry.getKey(), item);
		}
		createItems(1, createdItems);
	}
	
	/**
	 * Create all the children, ordered by level
	 * @param level current level
	 * @param createdItems items tree created
	 */
	private void createItems(int level, HashMap<Integer, TreeItem<String>> createdItems) {
		HashMap<Integer, LinkedHashMap<Integer, Integer>> data = TaskManager.TREE_DATA.getData();
		if(data.containsKey(level)) {
			for(Map.Entry<Integer, Integer> entry : data.get(level).entrySet()) {
				TreeItem<String> item;
				
				// Uses heritage to know if a TreeItem is a project or a category
				if(TaskManager.TREE_DATA.getProjects().containsKey(entry.getKey())) {
					item = new ProjectItem(TaskManager.TREE_DATA.getProjects().get(entry.getKey()));
				} else {
					item = new TreeItem<String>(itemsName.get(entry.getKey()));
				}
				
				// Avoid to create an item that has already created
				createdItems.put(entry.getKey(), item);
				if(createdItems.containsKey(entry.getValue())) {
					createdItems.get(entry.getValue()).getChildren().add(item);
				}
			}
		
			level++;
			createItems(level, createdItems);
		}
	}
	
	final Tree tree = this;
	
	private final class TextFieldTreeCell extends TreeCell<String> {
		
		private TextField textField;
		private ContextMenu contextMenu = new ContextMenu();
		
		private MenuItem newProjectMenuItem;
		private MenuItem addCatMenuItem;
		private MenuItem addSubCatMenuItem;
		private MenuItem delCatMenuItem;
		private MenuItem editName;
		
		public TextFieldTreeCell() {
			newProjectMenuItem = new MenuItem("New project");
			addCatMenuItem = new MenuItem("Add category");
			addSubCatMenuItem = new MenuItem("Add subcategory");
			delCatMenuItem = new MenuItem("Delete category");
			editName = new MenuItem("Edit");
			
			contextMenu.getItems().add(newProjectMenuItem);
			contextMenu.getItems().add(addCatMenuItem);
			contextMenu.getItems().add(addSubCatMenuItem);
			contextMenu.getItems().add(delCatMenuItem);
			contextMenu.getItems().add(new SeparatorMenuItem());
			contextMenu.getItems().add(editName);
			
			editName.setOnAction((event) -> {
				startEdit();
			});
			
			// New project tab
			newProjectMenuItem.setOnAction((event) -> {
				Project project = new Project("New project");
				ProjectItem item = new ProjectItem(project);
				ProjectTab tab = new ProjectTab(item);
				tree.getSelectionModel().getSelectedItem().getChildren().add(item);
				projectPane.getTabs().add(tab);
			});
			
			addCatMenuItem.setOnAction((event) -> {
				TreeItem<String> cat = new TreeItem<String>("New category");
				getRoot().getChildren().add(cat);
			});
			
			addSubCatMenuItem.setOnAction((event) -> {
				TreeItem<String> category = new TreeItem<String>("New subcategory");
				getTreeItem().getChildren().add(category);
			});

			delCatMenuItem.setOnAction((event) -> {
				TreeItem<String> item = (TreeItem<String>)getSelectionModel().getSelectedItem();
				
				// Delete the tab of the deleted project (if it's open)
				if(item instanceof ProjectItem) {
					for(Tab tab : projectPane.getTabs()) {
						ProjectTab projectTab = (ProjectTab) tab;
						if(projectTab.getProjectItem().equals(item)) {
							projectPane.getTabs().remove(projectTab);
							break;
						}
					}
				}
				
				item.getParent().getChildren().remove(item);
			});
		}
		
		@Override
		public void startEdit() {
			super.startEdit();
			
			getTreeView().setEditable(true);
			
			if(textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
			textField.requestFocus();
		}
		
		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText((String)getItem());
			setGraphic(getTreeItem().getGraphic());
			
			getTreeView().setEditable(false);
		}
		
		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			
			if(empty) {
				setText(null);
				setGraphic(null);
			} else {
				if(isEditing()) {
					if(textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
					if(getTreeItem() instanceof ProjectItem) {
						contextMenu.getItems().remove(newProjectMenuItem);
						contextMenu.getItems().remove(addCatMenuItem);
						contextMenu.getItems().remove(addSubCatMenuItem);
						
						delCatMenuItem.setText("Delete project");
					}
					setContextMenu(contextMenu);
				}
			}
			
			getTreeView().setEditable(false);
		}
		
		@Override
		public void commitEdit(String str) {
			super.commitEdit(str);
			if(getTreeItem() instanceof ProjectItem) {
				ProjectItem item = (ProjectItem) getTreeItem();
				item.getProject().setTitle(str);
				
				// Update the tab title is open
				for(Tab tab : projectPane.getTabs()) {
					ProjectTab projectTab = (ProjectTab) tab;
					if(projectTab.getProjectItem().equals(item)) {
						projectPane.getTabs().get(projectPane.getTabs().indexOf(projectTab)).setText(str);
						break;
					}
				}
			}
		}
		
		private void createTextField() {
			textField = new TextField(getString());
			
			textField.setOnKeyReleased((event) -> {
				if(event.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if(event.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});

			textField.focusedProperty().addListener((bool, oldValue, newValue) -> {
				if(!newValue) {
					commitEdit(textField.getText());
				}
			});
		}
		
		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}
