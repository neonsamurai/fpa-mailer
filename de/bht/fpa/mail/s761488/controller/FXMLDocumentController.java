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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

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

	@FXML
	TextField emailFilterField;

	@FXML
	Label numberOfFoundEmails;

	private ObservableList<Email> emailList;
	private ObservableList<Email> emailListFiltered;

	@FXML
	TableColumn<Email, String> importanceCol,
	    receivedCol,
	    readCol,
	    senderCol,
	    recepientsCol,
	    subjectCol;

	@FXML
	MenuItem menuFileSelectRootDirectory,
	    menuFileRecentRootFolders;

	@FXML
	MenuBar mailerMenu;

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
		setRootPath(new File(System.getProperty("user.home")));
		initializeManagers();
		loadRootFolder(rootPath);
		configureFolderExplorer();
		configureMenue();
		configureEmailList(rootFolder);
	}

	private void loadRootFolder(File root) {
		// set root folder
		rootFolder = folderManager.getTopFolder();
		// populate Folder model with root level items
		folderManager.loadContent(rootFolder);
	}

	private void initializeManagers() {
		// get Manager for our folders
		folderManager = new FileManager(rootPath);
		// get Manager for our emails
		emailManager = new EmailManager();

	}

	private void configureFolderExplorer() {

		// register eventHandlers
		handleTreeExpansion = new HandleTreeEvents();

		// register ChangeListeners
		selectedChanged = new HandleTreeSelectionEvents();

		// get root tree item
		rootNode = new TreeItem(rootFolder);
		rootNode.setExpanded(true);

		// Init TreeView 
		fileExplorer.setRoot(rootNode);
		fileExplorer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		fileExplorer.getSelectionModel().selectedItemProperty()
		    .addListener(selectedChanged);

		// ...and update TreeView  with folder children
		loadSubtree(rootNode, rootFolder);
	}

	/**
	 * This method does not support submenus yet! Need to make recursive
	 * traversal of menus to achieve that!
	 */
	private void configureMenue() {
		MenuEventHandler myMenuEventHandler = new MenuEventHandler();

		ObservableList<Menu> menuList = mailerMenu.getMenus();

		for (Menu menu : menuList) {
			ObservableList<MenuItem> menuItems = menu.getItems();
			for (MenuItem item : menuItems) {
				item.setOnAction(myMenuEventHandler);
			}
		}
	}

	private void configureEmailList(Folder folder) {
		emailList = FXCollections.observableArrayList();
		emailListFiltered = FXCollections.observableArrayList();
		emailList.addAll(folder.getEmails());
		emailList.addListener(new EmailListChangeListener());
		emailListFiltered.addAll(folder.getEmails());
		emailFilterField.textProperty().
		    addListener(new HandleFilterFieldEvents());
		emailListTable.setItems(emailListFiltered);
		emailListTable.getSelectionModel().
		    setSelectionMode(SelectionMode.SINGLE);
		emailListTable.getSelectionModel().selectedItemProperty().
		    addListener(new HandleEmailListSelectionEvents());
		importanceCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("importance"));
		receivedCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("received"));
		receivedCol.setSortType(TableColumn.SortType.DESCENDING);
		readCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("read"));
		senderCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("sender"));
		recepientsCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("receiver"));
		subjectCol.setCellValueFactory(
		    new ObjectPropertyValueFactory("subject"));

	}

	/**
	 * Matches emails by filter string from emailFilterField. Matches
	 * against these properties: subject, text, received, sent, receiver,
	 * sender.
	 *
	 * @param email
	 * @return boolean
	 */
	private boolean matchesEmailFilter(Email email) {
		String filterString = emailFilterField.getText();
		if (filterString == null || filterString.isEmpty()) {
			// No filter --> Add all.
			return true;
		}

		String lowerCaseFilterString = filterString.toLowerCase();

		if (email.getSubject().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (email.getText().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (email.getReceived().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (email.getSent().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (email.getReceiver().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		} else if (email.getSender().toLowerCase().
		    indexOf(lowerCaseFilterString) != -1) {
			return true;
		}
		return false; // Does not match
	}


	private void updateEmailListFiltered() {
		emailListFiltered.clear();
		for (Email email : emailList) {
			if (matchesEmailFilter(email)) {
				emailListFiltered.add(email);
			}
		}
		updateFilterFieldLable();
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
		folderManager.loadContent(folder);

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
		receivedCol.setSortType(TableColumn.SortType.DESCENDING);
	}

	private void setRootPath(File file) {
		rootPath = file;
	}

	private void updateFilterFieldLable() {
		String numString = "(" + emailListFiltered.size() + ")";
		numberOfFoundEmails.setText(numString);
	}

	/**
	 * This was heavily inspired of:
	 * http://edu.makery.ch/blog/2012/12/18/javafx-tableview-filter/
	 */
	private class EmailListChangeListener implements ListChangeListener {

		@Override
		public void onChanged(Change change) {
			updateEmailListFiltered();
		}

	}

	private class HandleFilterFieldEvents implements ChangeListener {

		@Override
		public void changed(ObservableValue ov, Object t, Object t1) {
			updateEmailListFiltered();
		}

	}

	private class HandleEmailListSelectionEvents implements ChangeListener {

		@Override
		public void changed(ObservableValue ov, Object t, Object t1) {

			if (t1 == null) {
				System.out.println("No mail selected.");
			} else {
				System.out.println(t1 + " selected.");
			}

		}

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
			setRootPath(newRootDirectory);
			folderManager = new FileManager(rootPath);
			loadRootFolder(newRootDirectory);
			configureFolderExplorer();
		}

		private void showRootHistory() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

	private class ObjectPropertyValueFactory implements
	    Callback<TableColumn.CellDataFeatures<Email, String>, ObservableValue<String>> {

		private final String propertyName;
		private Method method;

		ObjectPropertyValueFactory(String propertyName) {
			char[] propertyNameArr = propertyName.toCharArray();
			propertyNameArr[0] = Character.
			    toUpperCase(propertyNameArr[0]);

			this.propertyName = new String(propertyNameArr);
		}

		@Override
		public ObservableValue<String>
		    call(TableColumn.CellDataFeatures<Email, String> p) {

			try {
				method = p.getValue().getClass().
				    getMethod("get" + propertyName);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(FXMLDocumentController.class.
				    getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(FXMLDocumentController.class.
				    getName()).log(Level.SEVERE, null, ex);
			}
			if (p.getValue() != null) {
				try {
					Object propertyObject;
					propertyObject = method.invoke(
					    p.getValue());
					return new SimpleStringProperty(
					    propertyObject.toString());
				} catch (IllegalAccessException ex) {
					Logger.getLogger(FXMLDocumentController.class.getName()).
					    log(Level.SEVERE, null, ex);
				} catch (IllegalArgumentException ex) {
					Logger.getLogger(FXMLDocumentController.class.getName()).
					    log(Level.SEVERE, null, ex);
				} catch (InvocationTargetException ex) {
					Logger.getLogger(FXMLDocumentController.class.getName()).
					    log(Level.SEVERE, null, ex);
				}
			}
			return new SimpleStringProperty("<No value>");
		}
	}
}
