/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.controller;

import de.bht.fpa.mail.s761488.applicationLogic.EmailManager;
import de.bht.fpa.mail.s761488.applicationLogic.FileManager;
import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.Email;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	TableView<Email> emailListTable;
	
	private ObservableList<Email> emailList;
	private TableColumn<Email, String> importanceCol,
	    receivedCol,
	    readCol,
	    senderCol,
	    recepientsCol,
	    subjectCol;

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
	private EmailManager emailManager;

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
		configureEmailList(rootFolder);
	}

	private void configureFolderExplorer(File root) {
		// set root path
		rootPath = root;

		// get Manager for our folders
		folderManager = new FileManager(rootPath);
		// get Manager for our emails
		emailManager = new EmailManager();
		// register eventHandlers
		handleTreeExpansion = new HandleTreeEvents();

		// register ChangeListeners
		selectedChanged = new HandleTreeSelectionEvents();

		// get root tree item
		rootFolder = folderManager.getTopFolder();
		rootNode = new TreeItem(rootFolder);
		rootNode.setExpanded(true);

		// Init TreeView 
		fileExplorer.setRoot(rootNode);
		fileExplorer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		fileExplorer.getSelectionModel().selectedItemProperty()
		    .addListener(selectedChanged);

		// populate Folder model with root level items
		folderManager.loadContent(rootFolder);
		// ...and update TreeView  with folder children
		loadSubtree(rootNode, rootFolder);
	}

	private void configureMenue() {
		MenuEventHandler myMenuEventHandler = new MenuEventHandler();
		menuFileSelectRootDirectory.setOnAction(myMenuEventHandler);
	}

	private void configureEmailList(Folder folder) {
		emailList = FXCollections.observableArrayList();
		emailList.addAll(folder.getEmails());
		emailListTable.setItems(emailList);
		importanceCol = new TableColumn<>("IMPORTANCE");
		importanceCol.setCellValueFactory(new PropertyValueFactory("importance"));
		receivedCol = new TableColumn<>("Received");
		receivedCol.setCellFactory(new PropertyValueFactory("received"));
		readCol = new TableColumn<>("Read");
		readCol.setCellValueFactory(new PropertyValueFactory("read"));
		senderCol = new TableColumn<>("Sender");
		senderCol.setCellValueFactory(new PropertyValueFactory("sender"));
		recepientsCol = new TableColumn<>("Recepients");
		recepientsCol.setCellValueFactory(new PropertyValueFactory("recepients"));
		subjectCol = new TableColumn<>("subject");
		subjectCol.setCellValueFactory(new PropertyValueFactory("subject"));
		emailListTable.getColumns().setAll(
		    importanceCol,
		    receivedCol,
		    readCol,
		    senderCol,
		    recepientsCol,
		    subjectCol);

	}

	private void loadSubtree(TreeItem insertNode, Folder folder) {
		// Only load subtree if it hasn't already been loaded!
		if (insertNode.getChildren().size() == 0) {
			for (Component node : folder.getComponents()) {
				TreeItem newNode;
				newNode = new TreeItem(node);

				// Only add dummy TreeItem and event handler 
				// if node is a folder and has subfolders.
				if (node.isExpandable()) {
					newNode.addEventHandler(
					    TreeItem.branchExpandedEvent(), 
					    handleTreeExpansion);
					newNode.getChildren().
					    add(new TreeItem("DUMMY"));
				}
				insertNode.getChildren().add(newNode);
			}
		}
	}

	private void updateTreeNode(TreeItem item) {
		Folder folder = (Folder) item.getValue();
		if (folder.getComponents().isEmpty()) {
			folderManager.loadContent(folder);
		}
		
		// Only remove the DUMMY TreeItem, if it is really there.
		if (folder.isExpandable()
		    && item.getChildren().size() == 1) {
			TreeItem thisItem;
			thisItem = (TreeItem) item.getChildren().get(0);
			if (thisItem.getValue().toString().equals("DUMMY")) {
				// delete DUMMY TreeItem
				item.getChildren().remove(0); 
			}
		}

		loadSubtree(item, folder);
	}

	private void updateEmailList(Folder folder) {
		emailList.clear();
		emailList.addAll(folder.getEmails());
	}

	private class HandleTreeSelectionEvents implements ChangeListener {

		@Override
		public void changed(ObservableValue ov, Object t, Object t1) {
			if (t1 != null) {
				TreeItem treeNode = (TreeItem) t1;
				Folder folder;
				folder = (Folder) treeNode.getValue();
				emailManager.loadEmails(folder);
				System.out.println("Selected directory: " + folder.getPath());
				System.out.println("Number of Emails: " + folder.getEmails().size());
				updateEmailList(folder);
			}
		}
	}

	private class HandleTreeEvents implements EventHandler {

		@Override
		public void handle(Event t) {
			TreeItem item = (TreeItem) t.getSource();
			updateTreeNode(item);

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
			fileExplorer.getSelectionModel().selectedItemProperty()
			    .removeListener(selectedChanged);
			configureFolderExplorer(newRootDirectory);
		}

		private void showRootHistory() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}
}
