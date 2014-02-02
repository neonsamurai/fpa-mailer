/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.controller;

import de.bht.fpa.mail.s761488.applicationLogic.ApplicationLogic;
import de.bht.fpa.mail.s761488.model.Account;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class CreateAccountViewController implements Initializable {

    @FXML
    private TextField accountNameField, accountHostField, accountUsernameField;
    @FXML
    private PasswordField accountPasswordField, accountRepeatPasswordField;
    @FXML
    private Label accountMessage, accountFormHeadingLabel;
    @FXML
    private Button accountButtonCancel, accountButtonSubmit;

    private final ApplicationLogic mainController;
    private Account editAccount;
    private boolean createMode;

    CreateAccountViewController(ApplicationLogic manager) {
        mainController = manager;
        createMode = editAccount == null;
    }

    CreateAccountViewController(ApplicationLogic manager, Account account) {
        mainController = manager;
        editAccount = account;
        createMode = editAccount == null;

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accountButtonCancel.setOnAction(new ButtonEventHandler());
        accountButtonSubmit.setOnAction(new ButtonEventHandler());
        accountMessage.setVisible(false);
        accountFormHeadingLabel.setText("Create a new account");
        if (editAccount != null) {
            accountNameField.setText(editAccount.getName());
            accountHostField.setText(editAccount.getHost());
            accountUsernameField.setText(editAccount.getUsername());
            accountPasswordField.setText(editAccount.getPassword());
            accountRepeatPasswordField.setText(editAccount.getPassword());
            accountFormHeadingLabel.setText("Edit account: " + editAccount.getName());
        }
    }

    private void cancelCreateAccount() {
        closeWindow();
    }

    private void submitCreateAccount() {

        if (evaluateForm()) {
            submitForm();
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage window = (Stage) accountFormHeadingLabel.getScene().getWindow();
        window.close();
    }

    private boolean evaluateForm() {
        StringBuilder message = new StringBuilder();
        accountMessage.setVisible(false);

        if (accountNameField.getText().length() <= 0) {
            message.append("Account name missing.\n\n");
        }
        if (createMode && mainController.getAccount(accountNameField.getText()) != null) {
            message.append("Account with this name already present.\n\n");
        }
        if (accountHostField.getText().length() <= 0) {
            message.append("Account host name missing.\n\n");
        }
        if (accountUsernameField.getText().length() <= 0) {
            message.append("Account user name missing.\n\n");
        }
        if (accountPasswordField.getText().length() <= 0) {
            message.append("Account password missing.\n\n");
        }
        if (message.length() > 0) {
            message.append("Please provide missing information.\n\n");
        }
        if (!accountPasswordField.getText().equals(accountRepeatPasswordField.getText())) {
            message.append("Passwords don't match. Please double check your input.\n\n");
        }
        if (message.length() > 0) {
            accountMessage.setText(message.toString());
            accountMessage.setVisible(true);
            return false;
        }
        return true;
    }

    private void submitForm() {
        String name, host, username, password;
        Account newAccount;

        name = accountNameField.getText();
        host = accountHostField.getText();
        username = accountUsernameField.getText();
        password = accountPasswordField.getText();

        newAccount = new Account(name, host, username, password);
        
        if(createMode){
            mainController.saveAccount(newAccount);
        }else{
            mainController.updateAccount(newAccount);
        }
    }

    private class ButtonEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            Button eventSource;
            String sourceId;

            eventSource = (Button) t.getSource();
            sourceId = eventSource.getId();

            System.out.println(sourceId);

            switch (sourceId) {
                case "accountButtonCancel":
                    cancelCreateAccount();
                    break;
                case "accountButtonSubmit":
                    submitCreateAccount();
                    break;
            }
        }
    }

}
