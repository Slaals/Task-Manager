package fr.slals.view.tool;

import java.util.ArrayList;

import fr.slals.data.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class TaskTable extends TableView<TaskCell> {
	
	ObservableList<TaskCell> data;
	
	ContextMenu taskContextMenu = new ContextMenu();
	
	/**
	 * @param tasks
	 */
	public TaskTable(ArrayList<Task> tasks) {
		super();
		
		data = FXCollections.observableArrayList();
		
		setEditable(true);
		
		for(Task task : tasks) {
			data.add(new TaskCell(task));
		}
		
		init();
	}

	public TaskTable() {
		super();
		
		data = FXCollections.observableArrayList();
		
		init();
	}
	
	/**
	 * @param menu
	 */
	public void addMenu(MenuItem menu) {
		taskContextMenu.getItems().add(menu);
	}
	
	/**
	 * @param task
	 */
	public void addTaskCell(TaskCell task) {
		data.add(task);
	}
	
	@SuppressWarnings("unchecked")
	private void init() {
		TableColumn<TaskCell, String> taskColumn = new TableColumn<TaskCell, String>("Task");
		TableColumn<TaskCell, ImageView> doneColumn = new TableColumn<TaskCell, ImageView>("Done");
		TableColumn<TaskCell, String> dateColumn = new TableColumn<TaskCell, String>("Date");
		
		taskColumn.setCellValueFactory(new PropertyValueFactory<TaskCell, String>("label"));
		doneColumn.setCellValueFactory(new PropertyValueFactory<TaskCell, ImageView>("done"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<TaskCell, String>("date"));
		
		taskColumn.prefWidthProperty().bind(widthProperty().multiply(0.60));
		doneColumn.prefWidthProperty().bind(widthProperty().multiply(0.10));
		dateColumn.prefWidthProperty().bind(widthProperty().multiply(0.30));
		
		doneColumn.setEditable(false);
		dateColumn.setEditable(false);
		
		initCellFactory(taskColumn, doneColumn);
		
		setItems(data);
		
		getColumns().addAll(taskColumn, doneColumn, dateColumn);
		
		setContextMenu(taskContextMenu);
	}
	
	private void refresh() {
		getColumns().get(0).setVisible(false);
		getColumns().get(0).setVisible(true);
	}
	
	private void initCellFactory(TableColumn<TaskCell, String> taskColumn, TableColumn<TaskCell, ImageView> doneColumn) {
		// Text wrap in Task column and edit
		taskColumn.setCellFactory(new Callback<TableColumn<TaskCell, String>, TableCell<TaskCell, String>>() {
			@Override
			public TableCell<TaskCell, String> call(TableColumn<TaskCell, String> arg0) {
				TextFieldTaskCell cell = new TextFieldTaskCell() {
					private Text text;
					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if(empty) {
							setText(null);
							setGraphic(null);
						} else {
							if(isEditing()) {
								TextArea textField = getTextField();
								if(textField != null) {
									textField.setText(getString());
								}
								setText(null);
								setGraphic(textField);
							} else {
								text = new Text(item);
								text.setText(getString());
								text.setWrappingWidth(taskColumn.getWidth());
								text.wrappingWidthProperty().bind(widthProperty());
								setWrapText(true);
								
								setGraphic(text);
								
								setContextMenu(getMenu());
							}
						}
					}
				};
				return cell;
			}
		});
		
		// Set the image "check" or "cross"
		doneColumn.setCellFactory(new Callback<TableColumn<TaskCell, ImageView>, TableCell<TaskCell, ImageView>>() {
			@Override
			public TableCell<TaskCell, ImageView> call(TableColumn<TaskCell, ImageView> arg0) {
				TableCell<TaskCell, ImageView> cell = new TableCell<TaskCell, ImageView>() {
					@Override
					public void updateItem(ImageView item, boolean empty) {
						if(item != null && !empty) {
							VBox box = new VBox();
							box.setPadding(new Insets(10));

							box.setAlignment(Pos.CENTER);
							
							box.getChildren().add(item);
							
							box.setCursor(Cursor.HAND);
							
							// Refresh
							box.setOnMouseClicked((event) -> {
								data.get(getIndex()).setDone();
								
								refresh();
							});
							
							setGraphic(box);
						} else { // When the row is empty
							setGraphic(null);
						}
					}
				};
				return cell;
			}
		});
	}
	
	public class TextFieldTaskCell extends TableCell<TaskCell, String> {
		private TextArea textField;
		private ContextMenu contextMenu = new ContextMenu();
		
		public TextFieldTaskCell() {
			MenuItem delMenu = new MenuItem("Delete task");
			MenuItem editName = new MenuItem("Edit");

			contextMenu.getItems().add(delMenu);
			contextMenu.getItems().add(new SeparatorMenuItem());
			contextMenu.getItems().add(editName);
			
			delMenu.setOnAction((event) -> {
				// Remove the task from the data
				getTableView().getSelectionModel().getSelectedItem().deleteTask();
				
				data.remove(getTableRow().getIndex());
				
				getTableView().getSelectionModel().clearSelection();
			});
			
			editName.setOnAction((event) -> {
				startEdit();
			});
		}

		@Override
		public void commitEdit(String str) {
			super.commitEdit(str);
			setText(str);
			data.get(getIndex()).setLabel(str);
			updateItem(str, isEmpty());
		}
		
		@Override
		public void startEdit() {
			super.startEdit();
			
			if(textField == null) {
				createTextField();
			} else {
				textField.setText(getString());
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
			setGraphic(getGraphic());
		}
		
		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			
			if(empty) {
				// Reset the current row when it's empty
				setGraphic(null);
				setContextMenu(taskContextMenu);
			}
		}
		
		private void createTextField() {
			textField = new TextArea(getString());
			
			textField.setPrefHeight(100);
			textField.setWrapText(true);
			
			textField.setOnKeyReleased((event) -> {
				if(event.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if(event.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});

			// TODO revoir le focus
			/*textField.focusedProperty().addListener((bool, oldValue, newValue) -> {
				if(!newValue) {
					commitEdit(textField.getText());
				}
			});*/
		}
		
		public String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
		
		public TextArea getTextField() {
			return textField;
		}
		
		public ContextMenu getMenu() {
			return contextMenu;
		}
	}
}
