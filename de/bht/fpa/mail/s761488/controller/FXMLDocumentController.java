/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.controller;

import de.bht.fpa.mail.s761488.applicationLogic.EmailManager;
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
	private TableColumn<Email,String> 
		importanceCol, 
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
		//configureEmailList(rootFolder);
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
				if (t1 != null) {
					TreeItem treeNode = (TreeItem) t1;
					updateTreeNode(treeNode);
					showEmailsInNode(treeNode);
				}
			}

			private void showEmailsInNode(TreeItem treeNode) {
				Folder folder = (Folder) treeNode.getValue();
				List emails = folder.getEmails();
				System.out.println("Selected directory: " + folder.getPath());
				System.out.println("Number of Emails: " + emails.size());
				for (Iterator it = emails.iterator(); it.hasNext();) {
					Email email = (Email) it.next();
					final String s = " | ";
					System.out.println(
						"[Email: "
							+ email.getSender() + s
							+ email.getReceived() + s
							+ email.getSubject()
							+ "]");
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
		fileExplorer.getSelectionModel().selectedItemProperty()
			.addListener(selectedChanged);

		// populate Folder model with root level items
		folderManager.loadContent(rootFolder);
		// ...and update TreeView  with folder children
		loadSubtree(rootNode, rootFolder);
	}

	private void loadSubtree(TreeItem insertNode, Folder folder) {
		for (Component node : folder.getComponents()) {
			TreeItem newNode;
			newNode = new TreeItem(node);

			// Only add dummy TreeItem if node is a folder and has children.
			if (node.isExpandable() && hasChildFolders(node)) {
				newNode.addEventHandler(
					TreeItem.branchExpandedEvent(), handleTreeExpansion);
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

	private void updateTreeNode(TreeItem item) {
		Folder folder = (Folder) item.getValue();
		if (folder.getComponents().isEmpty()) {
			if (hasChildFolders(folder)) {
				item.getChildren().remove(0); // delete DUMMY TreeItem
			}
			folderManager.loadContent(folder);
			loadSubtree(item, folder);
		}
	}

	private void configureEmailList(Folder folder) {
		emailList = getEmailList(folder);
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
		
	}

	private ObservableList<Email> getEmailList(Folder rootFolder) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private class HandleTreeEvents implements EventHandler {

		@Override
		public void handle(Event t) {
			TreeItem item = (TreeItem) t.getSource();
			updateTreeNode(item);

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
			fileExplorer.getSelectionModel().selectedItemProperty()
				.removeListener(selectedChanged);
			configureFolderExplorer(newRootDirectory);
		}

		private void showRootHistory() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}
}
