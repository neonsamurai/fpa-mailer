/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.controller;

import de.bht.fpa.mail.s761488.applicationLogic.EmailManager;
import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class FXMLDocumentController implements Initializable {

	@FXML
	TreeView<Component> fileExplorer;

	@FXML
	MenuItem menuFileSelectRootDirectory;
	@FXML
	MenuItem menuFileRecentRootFolders;

	EventHandler handleTreeExpansion;

	TreeItem<Component> rootNode;
	File rootPath;
	Folder rootFolder;
	FolderManagerIF folderManager;

	final DirectoryChooser newRootChooser = new DirectoryChooser();
	private ChangeListener selectedChanged;

	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		configureFolderExplorer(new File(System.getProperty("user.home")));
		configureMenue();
	}

	private void configureFolderExplorer(File root) {
		// set root path
		rootPath = root;

		// get Manager for our folders
		folderManager = new EmailManager(rootPath);

		// register eventHandlers
		handleTreeExpansion = new HandleTreeEvents();

		// register ChangeListeners
		selectedChanged = new ChangeListener() {

			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				TreeItem treeNode = (TreeItem) t1;
				Component node = (Component) treeNode.getValue();
				// If node is an empty Folder
				if (node.isExpandable() && 
					node.getComponents().isEmpty()) {
					Folder folder = (Folder) node;
					// delete DUMMY TreeItem
					if(!treeNode.getChildren().isEmpty()){
						treeNode.getChildren().remove(0);
					}
					folderManager.loadContent(folder);
					loadSubtree(treeNode, folder);
				}
				List<Component> content = node.getComponents();

				System.out.println(node.getPath());
				System.out.println("Contains " + content.size() + " emails.");
				for (Component component : content) {
					File file = new File(component.getPath());
					if(file.isFile()) {
						System.out.println(file.getName());
					}
				}

			}
		};

		// get root tree item
		rootFolder = folderManager.getRootFolder();
		rootNode = new TreeItem(rootFolder);
		rootNode.setExpanded(true);

		// Init TreeView 
		fileExplorer.setRoot(rootNode);
		fileExplorer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		fileExplorer.getSelectionModel().selectedItemProperty().addListener(selectedChanged);
		
		// populate Folder model with root level items
		folderManager.loadContent(rootFolder);
		// ...and update TreeView  with folder children
		loadSubtree(rootNode, rootFolder);
	}

	private void loadSubtree(TreeItem insertNode, Folder folder) {
		for (Component node : folder.getComponents()) {
			TreeItem newNode;
			newNode = new TreeItem(node);

			// Only add dummy TreeItem if node id a folder and has children.
			if (node.isExpandable() && hasChildFolders(node)) {
				newNode.addEventHandler(TreeItem.branchExpandedEvent(), handleTreeExpansion);
				newNode.getChildren().add(new TreeItem("DUMMY"));
			} else {
			}
			// Only insert node if expandable.
			if (node.isExpandable()) {
				insertNode.getChildren().add(newNode);
			}
		}
	}

	private void configureMenue() {
		MenuEventHandler myMenuEventHandler = new MenuEventHandler();
		menuFileSelectRootDirectory.setOnAction(myMenuEventHandler);
	}

	private boolean hasChildFolders(Component node) {
		File folder;
		folder = new File(node.getPath());
		DirectoryFilter filter = new DirectoryFilter();
		boolean hasChildFolders = folder.listFiles(filter).length > 0;
		return hasChildFolders;
	}

	private class HandleTreeEvents implements EventHandler {

		@Override
		public void handle(Event t) {
			TreeItem item = (TreeItem) t.getSource();
			Folder folder = (Folder) item.getValue();
			if (folder.getComponents().isEmpty()) {
				item.getChildren().remove(0); // delete DUMMY TreeItem
				folderManager.loadContent(folder);
				loadSubtree(item, folder);
			}

		}
	}

	private class DirectoryFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

		public String getDescription() {
			return "Directory";

		}

	}

	private class MenuEventHandler implements EventHandler {

		@Override
		public void handle(Event t) {
			MenuItem eventSource = (MenuItem) t.getSource();
			String eventSourceId = eventSource.getId();

			switch (eventSourceId) {
				case "menuFileSelectRootDirectory":
					showRootSelectorAndChangeRoot();
					break;
				case "menuFileRecentRootDirectories":
					showRootHistory();
					break;
			}
		}

		private void showRootSelectorAndChangeRoot() {
			Stage chooseRootStage = new Stage(StageStyle.UTILITY);
			chooseRootStage.setTitle("Select new Root");
			File newRootDirectory = newRootChooser.showDialog(chooseRootStage);
			fileExplorer.getSelectionModel().selectedItemProperty().removeListener(selectedChanged);
			configureFolderExplorer(newRootDirectory);
		}

		private void showRootHistory() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}
}
