/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.controller;

import de.bht.fpa.mail.s761488.applicationLogic.ApplicationLogic;
import de.bht.fpa.mail.s761488.applicationLogic.account.AccountFileDAO;
import de.bht.fpa.mail.s761488.model.Account;
import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.Folder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
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
    Label numberOfFoundEmails,
            emailSubjectLabel,
            emailReceivedLabel,
            emailReceiverLabel,
            emailSenderLabel;

    @FXML
    TextArea emailTextArea;

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

    @FXML
    Menu menuAccountOpenAccount, menuAccountEditAccount;

    EventHandler handleTreeExpansion;

    TreeItem<Component> rootNode;

    final DirectoryChooser newRootChooser = new DirectoryChooser();
    private ChangeListener selectedChanged;

    private ApplicationLogic manager;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeManagers();
        loadRootFolder(new File(manager.getTopFolder().getPath()));
        configureFolderExplorer();
        configureMenue();
        configureEmailList(manager.getTopFolder());
    }

    private void configureEmailList(Folder folder) {

        manager.getEmailList().addListener(new FXMLDocumentController.EmailListChangeListener());
        emailFilterField.textProperty().
                addListener(new FXMLDocumentController.HandleFilterFieldEvents());
        emailListTable.setItems(manager.getEmailListFiltered());
        emailListTable.getSelectionModel().
                setSelectionMode(SelectionMode.SINGLE);
        emailListTable.getSelectionModel().selectedItemProperty().
                addListener(new FXMLDocumentController.HandleEmailListSelectionEvents());
        importanceCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("importance"));
        receivedCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("received"));
        readCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("read"));
        senderCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("sender"));
        recepientsCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("receiver"));
        subjectCol.setCellValueFactory(
                new FXMLDocumentController.ObjectPropertyValueFactory("subject"));
    }

    private void loadRootFolder(File root) {

        // populate Folder model with root level items
        manager.loadContent(manager.getTopFolder());
    }

    private void initializeManagers() {
        // get manager facade
        manager = new ApplicationLogic(new File(System.getProperty("user.home")));

    }

    private void configureFolderExplorer() {

        // register eventHandlers
        handleTreeExpansion = new HandleTreeEvents();

        // register ChangeListeners
        selectedChanged = new HandleTreeSelectionEvents();

        // get root tree item
        rootNode = new TreeItem(manager.getTopFolder());
        rootNode.setExpanded(true);

        // Init TreeView 
        fileExplorer.setRoot(rootNode);
        fileExplorer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileExplorer.getSelectionModel().selectedItemProperty()
                .addListener(selectedChanged);

        // ...and update TreeView  with folder children
        loadSubtree(rootNode, manager.getTopFolder());
    }

    /**
     * This method does not support submenus yet! Need to make recursive
     * traversal of menus to achieve that!
     */
    private void configureMenue() {
        loadAccountsToMenu();

        ObservableList<Menu> menuList = mailerMenu.getMenus();

        for (Menu menu : menuList) {
            addMenuEventHandlers(menu);
        }
    }

    private void addMenuEventHandlers(Menu menu) {
        MenuEventHandler myMenuEventHandler = new MenuEventHandler();
        ObservableList<MenuItem> menuItems = menu.getItems();
        for (MenuItem item : menuItems) {
            if (item instanceof Menu) {
                addMenuEventHandlers((Menu) item);
                System.out.println(item.toString());
            } else {
                System.out.println(item.toString());
                item.setOnAction(myMenuEventHandler);
            }
        }
    }

    private void loadAccountsToMenu() {
        System.out.println("Loading Accounts...");
        // get Accounts list
        
        List<String> accounts = manager.getAllAccounts();
//        System.out.println(accounts);
        // add MenuItems with text=name
        for (String account : accounts) {
            MenuItem accountCreateItem, accountEditItem;
            accountCreateItem = new MenuItem(account);
            accountCreateItem.setUserData(manager.getAccount(account));
            accountEditItem = new MenuItem(account);
            accountEditItem.setUserData(account);
            menuAccountOpenAccount.getItems().add(accountCreateItem);
            menuAccountEditAccount.getItems().add(accountEditItem);
        }
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
        manager.loadContent(folder);

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

    /**
     * This was heavily inspired by:
     * http://edu.makery.ch/blog/2012/12/18/javafx-tableview-filter/
     */
    private class EmailListChangeListener implements ListChangeListener {

        @Override
        public void onChanged(Change change) {
            updateEmailListFiltered();
        }

    }

    public void updateEmailListFiltered() {
        String filterString = emailFilterField.getText();
        manager.updateEmailListFiltered(filterString);
        updateFilterFieldLabel();

    }

    private void updateFilterFieldLabel() {
        String numString = "(" + manager.getEmailListFiltered().size() + ")";
        numberOfFoundEmails.setText(numString);
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
                clearEmail();
            } else {
                renderEmail(t1);
            }

        }

        private void renderEmail(Object t1) {
            Email thisMail = (Email) t1;
            emailReceivedLabel.setText(thisMail.getReceived());
            emailReceiverLabel.setText(thisMail.getReceiver());
            emailSenderLabel.setText(thisMail.getSender());
            emailSubjectLabel.setText(thisMail.getSubject());
            emailTextArea.setText(thisMail.getText());
        }

        private void clearEmail() {
            String noMail = "(No email selected)";
            emailReceivedLabel.setText(noMail);
            emailReceiverLabel.setText(noMail);
            emailSenderLabel.setText(noMail);
            emailSubjectLabel.setText(noMail);
            emailTextArea.setText(noMail);
        }
    }

    private class HandleTreeSelectionEvents implements ChangeListener {

        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            if (t1 != null) {
                TreeItem treeNode = (TreeItem) t1;
                Folder folder;
                folder = (Folder) treeNode.getValue();
                manager.loadEmails(folder);
                // Force tree node refresh, hacky :(
                fileExplorer.setShowRoot(false);
                fileExplorer.setShowRoot(true);
                System.out.println("Selected directory: " + folder.getPath());
                System.out.println("Number of Emails: " + folder.getEmails().size());
                manager.updateEmailList(folder);
                updateEmailTable();
            }
        }

        private void updateEmailTable() {
            emailListTable.getSortOrder().clear();
            emailListTable.getSortOrder().add(receivedCol);
            receivedCol.setSortType(TableColumn.SortType.DESCENDING);
            receivedCol.setSortable(true);
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

            if (eventSourceId == null) {
                eventSourceId = eventSource.getParentMenu().getId();
            }
            System.out.println(eventSourceId);
            switch (eventSourceId) {
                case "menuFileSelectRootDirectory":
                    showRootSelectorAndChangeRoot();
                    break;
                case "menuFileRecentRootDirectories":
                    showRootHistory();
                    break;
                case "menuFileSaveEmails":
                    showDirectoryChooserAndSaveEmails();
                    break;
                case "menuAccountOpenAccount":
                    Account openAccount = (Account) eventSource.getUserData();
                    changeRootByAccount(openAccount);
                    break;
                case "menuAccountEditAccount":
                    Account editAccount = (Account) eventSource.getUserData();
                    showEditAccount(editAccount);
                    break;
                case "menuAccountCreateAccount":
                    showCreateAccount();
            }
        }

        
    }
    
    private void showRootSelectorAndChangeRoot() {
            Stage chooseRootStage = new Stage(StageStyle.UTILITY);
            chooseRootStage.setTitle("Select new Root");
            File newRootDirectory = newRootChooser.showDialog(chooseRootStage);
            fileExplorer.getSelectionModel().selectedItemProperty()
                    .removeListener(selectedChanged);
            manager.changeDirectory(newRootDirectory);
            /**
             * folderManager = new FileManager(new File(folderManager.
             * getTopFolder().getPath()));*
             */
            loadRootFolder(newRootDirectory);
            configureFolderExplorer();
        }

        private void showRootHistory() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void showDirectoryChooserAndSaveEmails() {
            Stage chooseDirectoryStage = new Stage(StageStyle.UTILITY);
            chooseDirectoryStage.setTitle("Select destination directory");
            File newRootDirectory = newRootChooser.
                    showDialog(chooseDirectoryStage);
            manager.saveEmails(newRootDirectory);
        }

        private void changeRootByAccount(Account account) {

            manager.openAccount(account.getName());

            System.out.println("Changed root to ..." + manager.getTopFolder().getPath());

            fileExplorer.getSelectionModel().selectedItemProperty()
                    .removeListener(selectedChanged);

            manager.loadContent(manager.getTopFolder());
            configureFolderExplorer();
        }

        private void showEditAccount(Account account) {
            Stage editStage = new Stage(StageStyle.UTILITY);
            editStage.setTitle("FPA Mailer - Edit Account: " + account.getName());
            URL location = getClass().getResource("/de/bht/fpa/mail/s761488/view/AccountForm.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setController(new CreateAccountViewController(manager, account));
            try {
                Pane myPane = (Pane) fxmlLoader.load();
                Scene myScene = new Scene(myPane);
                editStage.setScene(myScene);
                editStage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void showCreateAccount() {
            Stage createStage = new Stage(StageStyle.UTILITY);
            createStage.setTitle("FPA Mailer - Create new Account");
            URL location = getClass().getResource("/de/bht/fpa/mail/s761488/view/AccountForm.fxml");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setController(new CreateAccountViewController(manager));
            try {
                Pane myPane = (Pane) fxmlLoader.load();
                Scene myScene = new Scene(myPane);
                createStage.setScene(myScene);
                createStage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
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
