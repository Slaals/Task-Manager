package fr.slals.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.slals.core.TaskManager;
import fr.slals.view.treeview.ProjectItem;
import fr.slals.view.treeview.Tree;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Task Manager
 * @author Slals - 2015
 *
 */
public class Data {
	
	private static Data instance = null;
	
	// The hashmap contains level as key, the linkedhashmap contains parent as key and child as value
	private HashMap<Integer, LinkedHashMap<Integer, Integer>> data;
	
	// Contains node id as key and node name as value
	private LinkedHashMap<Integer, String> nodesName;
	
	private LinkedHashMap<Integer, Project> projects;

	private Data() {
		data = new HashMap<Integer, LinkedHashMap<Integer, Integer>>();
		nodesName = new LinkedHashMap<Integer, String>();
		projects = new LinkedHashMap<Integer, Project>();
		
		try {
			retrieveData();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return instance if it's aleardy been executed
	 */
	public static Data getTreeData() {
		if(instance == null) {
			instance = new Data();
		}
		return instance;
	}
	
	/**
	 * @return the projects stored from the xml
	 */
	public LinkedHashMap<Integer, Project> getProjects() {
		return projects;
	}
	
	/**
	 * @return the nodes name, <data>
	 */
	public LinkedHashMap<Integer, String> getNodesName() {
		return nodesName;
	}
	
	/**
	 * @return the data stored from the xml
	 */
	public HashMap<Integer, LinkedHashMap<Integer, Integer>> getData() {
		return data;
	}
	
	/**
	 * Get all the data from the XML file data.xml
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void retrieveData() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		File file;
		if(TaskManager.PROPERTIES.getProperty("DATA_PATH") == null) {
			file = new File(TaskManager.RESOURCE_PATH + "\\data.xml");
		} else {
			file = new File(TaskManager.PROPERTIES.getProperty("DATA_PATH") + "\\data.xml");
		}
		
		if(file.exists()) {
			Document document = builder.parse(file);
			
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			for(int i = 0; i < nodeList.getLength(); i++) {
				Node currentNode = nodeList.item(i);
				Element element = null;
				
				int id = 0;
				String name = "";
				boolean project = false;
				
				if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) currentNode;
					
					// Recup attributes
					id = Integer.valueOf(currentNode.getAttributes().getNamedItem("id").getNodeValue());
					
					// Recup child nodes
					name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
					int parent = Integer.valueOf(element.getElementsByTagName("parent").item(0).getChildNodes().item(0).getNodeValue());
					int level = Integer.valueOf(currentNode.getAttributes().getNamedItem("level").getNodeValue());
					
					project = Boolean.valueOf(currentNode.getAttributes().getNamedItem("project").getNodeValue());
					
					feedLists(id, name, parent, level);
				}
				
				if(project) {
					createProject(name, id, element.getElementsByTagName("project"));
				}
			}
		} else {
			System.out.println("Can't find data file");
		}
	}
	
	/**
	 * Create and store a project
	 * @param name
	 * @param id
	 * @param nodeList <project></project>
	 */
	private void createProject(String title, int id, NodeList nodeList) {
		Project project = null;
		
		// Iteration of the nodes in <project>
		for(int i = 0; i < nodeList.getLength(); i++) {
			
			Node currentNode = nodeList.item(i);
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				
				// <project> as Element
				Element element = (Element) currentNode;
				
				String desc = "";
				
				if(element.getElementsByTagName("description").item(0).getChildNodes().item(0) != null) {
					desc = element.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();
				} 
				
				// Create object's project
				project = new Project(title, desc);
				
				// Iteration of the nodes in <files>
				NodeList filesList = element.getElementsByTagName("files").item(0).getChildNodes();
				for(int j = 0; j < filesList.getLength(); j++) {
					Node fileNode = filesList.item(j);
					if(fileNode.getNodeType() == Node.ELEMENT_NODE) {
						// Add the file name into the project
						project.addFile(new File(fileNode.getChildNodes().item(0).getNodeValue()));
					}
				}
				
				// Iteration of the nodes in <tasks>
				NodeList tasksList = element.getElementsByTagName("tasks").item(0).getChildNodes();
				for(int j = 0; j < tasksList.getLength(); j++) {
					Node taskNode = tasksList.item(j);
					if(taskNode.getNodeType() == Node.ELEMENT_NODE) {
						Element taskElement = (Element) taskNode;
						
						String label = taskNode.getChildNodes().item(0).getNodeValue();
						boolean validated = Boolean.valueOf(taskElement.getAttributes().getNamedItem("validated").getNodeValue());
						String date = taskElement.getAttributes().getNamedItem("date").getNodeValue();
						
						// Add the new task in the project
						project.addTask(new Task(label, validated, date, project));
					}
				}
			}
			
			// Store the project
			projects.put(id, project);
		}
	}
	
	/**
	 * Add the values to the different lists
	 * @param id
	 * @param name
	 * @param parent
	 * @param level
	 */
	private void feedLists(int id, String name, int parent, int level) {
		nodesName.put(id, name);
		if(!data.containsKey(level)) {
			LinkedHashMap<Integer, Integer> parentList = new LinkedHashMap<Integer, Integer>();
			parentList.put(id, parent);
			data.put(level, parentList);
		} else {
			data.get(level).put(id, parent);
		}
	}
	
	/**
	 * Save the data into data.xml
	 * @param tree
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void saveData(Tree tree) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("root");
		doc.appendChild(rootElement);
		
		doc = createElements(0, tree.getRoot().getChildren().get(0), tree.getRoot().getChildren().get(0), 
				new HashMap<TreeItem<String>, Integer>(), 1, rootElement, doc);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		File file = null;
		if(TaskManager.PROPERTIES.getProperty("DATA_PATH") == null) {
			file = new File(TaskManager.RESOURCE_PATH + "\\data.xml");
		} else {
			file = new File(TaskManager.PROPERTIES.getProperty("DATA_PATH") + "\\data.xml");
		}
		StreamResult result = new StreamResult(file);
		
		transformer.transform(source, result);
	}
	
	/**
	 * Depth search
	 * @param currentLevel target node level, start at 0
	 * @param targetedNode current node
	 * @param parentNode parent of the last node
	 * @param parentId list that contains the treeitem as key and his id as key
	 * @param id id of the current node
	 * @param root 
	 * @param doc
	 * @return document
	 */
	private Document createElements(int currentLevel, TreeItem<String> targetedNode, TreeItem<String> parentNode, 
			HashMap<TreeItem<String>, Integer> parentId, int id, Element root, Document doc) {
		// If out of the tree, it's finish
		if(parentNode != null && currentLevel >= 0) {
			// If the nextSibling is non existant, go to the parent's sibling <=== ICI PROB JE CROIS
			if(targetedNode == null) {
				if(parentNode.getParent().nextSibling() != null) {
					int levelIt = currentLevel - 1;
					createElements(levelIt, parentNode.getParent().nextSibling(), parentNode.getParent(), parentId, id, root, doc);
				} else { // Parent has no sibling, targetnode = null and go find the next parent
					int levelIt = currentLevel - 1;
					createElements(levelIt, null, parentNode.getParent(), parentId, id, root, doc);
				}
				return doc;
			}
			
			int valIdParent = 0;
			// If the targeted node has parent, set the parent's id
			if(targetedNode.getParent() != null && parentId.containsKey(targetedNode.getParent())) {
				valIdParent = parentId.get(targetedNode.getParent());
			} 
			
			Element data = doc.createElement("data");
			data.setAttribute("id", String.valueOf(id));
			data.setAttribute("level", String.valueOf(currentLevel));
			
			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(targetedNode.getValue()));
			data.appendChild(name);
			
			Element parent = doc.createElement("parent");
			parent.appendChild(doc.createTextNode(String.valueOf(valIdParent)));
			data.appendChild(parent);
			
			Element level = doc.createElement("level");
			level.appendChild(doc.createTextNode(String.valueOf(currentLevel)));
			data.appendChild(level);
			
			// Create project node
			if(targetedNode instanceof ProjectItem) {
				data.setAttribute("project", "true");
				
				Project p = ((ProjectItem) targetedNode).getProject();
				
				Element project = doc.createElement("project");
				
				Element desc = doc.createElement("description");
				desc.appendChild(doc.createTextNode(p.getDescription()));
				project.appendChild(desc);
				
				// files node
				Element files = doc.createElement("files");
				
				for(File f : p.getFiles()) {
					Element fname = doc.createElement("name");
					fname.appendChild(doc.createTextNode(f.getAbsolutePath()));
					files.appendChild(fname);
				}
					
				// tasks node
				Element tasks = doc.createElement("tasks");
				
				for(Task t : p.getTasks()) {
					Element tlabel = doc.createElement("label");
					tlabel.setAttribute("validated", t.getDoneValue());
					tlabel.setAttribute("date", t.getDate());
					tlabel.appendChild(doc.createTextNode(t.getLabel()));
					tasks.appendChild(tlabel);
				}
				
				project.appendChild(files);
				project.appendChild(tasks);
				
				data.appendChild(project);
			} else {
				data.setAttribute("project", "false");
			}
			
			root.appendChild(data);
			
			ObservableList<TreeItem<String>> children = targetedNode.getChildren();
			// If there is any child
			if(children.size() > 0) {
				// Add parent id to the parent list
				parentId.put(targetedNode, id);
				int idIt = id + 1;
				int levelIt = currentLevel + 1;
				createElements(levelIt, targetedNode.getChildren().get(0), targetedNode.getParent(), parentId, idIt, root, doc);
			} else {
				// If the targeted node has sibling, go to this sibling
				if(targetedNode.nextSibling() != null) {
					int idIt = id + 1;
					createElements(currentLevel, targetedNode.nextSibling(), targetedNode.getParent(), parentId, idIt, root, doc);
				} else { // Else go to the targeted node's parent next sibling
					int idIt = id + 1;
					int levelIt = currentLevel - 1;
					createElements(levelIt, targetedNode.getParent().nextSibling(), targetedNode.getParent(), parentId, idIt, root, doc);
				}
			}
		}
		
		return doc;
	}
}
