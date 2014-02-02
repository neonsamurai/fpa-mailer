/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.applicationLogic.account.AccountFileDAO;
import de.bht.fpa.mail.s761488.applicationLogic.account.AccountManagerIF;
import de.bht.fpa.mail.s761488.model.Account;
import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.EmailManagerIF;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author tim
 */
public class ApplicationLogic implements ApplicationLogicIF {
    
    private final EmailManagerIF emailManager;
    private final FolderManagerIF fileManager;
    private final AccountManagerIF accountManager;
    
    public ApplicationLogic(File directory){
        
        fileManager = new FileManager(directory);
        emailManager = new EmailManager(fileManager.getTopFolder());
        accountManager = new AccountManager();
    }

    @Override
    public Folder getTopFolder() {
       return fileManager.getTopFolder();
    }

    @Override
    public void loadContent(Folder folder) {
        fileManager.loadContent(folder);
    }

    @Override
    public List<Email> search(String pattern) {
        emailManager.updateEmailListFiltered(pattern);
        return emailManager.getEmailListFiltered();
    }

    @Override
    public void loadEmails(Folder folder) {
        emailManager.loadEmails(folder);
    }

    @Override
    public void changeDirectory(File file) {
        fileManager.setTopFolder(file);
    }

    @Override
    public void saveEmails(File file) {
        emailManager.saveEmails(file);
    }

    public ObservableList<Email> getEmailList() {
        return emailManager.getEmailList();
    }

    public ObservableList<Email> getEmailListFiltered() {
        return emailManager.getEmailListFiltered();
    }

    public void updateEmailListFiltered(String filterString) {
        emailManager.updateEmailListFiltered(filterString);
    }

    public void updateEmailList(Folder folder) {
        emailManager.updateEmailList(folder);
    }

    @Override
    public void openAccount(String name) {
        Account account = accountManager.getAccount(name);
        
        changeDirectory(new File(account.getTop().getPath()));
    }

    @Override
    public List<String> getAllAccounts() {
        List<String> accountNames = new ArrayList<>();
        List<Account> accounts;
        accounts = accountManager.getAllAccounts();
        
        for(Account account: accounts){
            accountNames.add(account.getName());
        }
        
        return accountNames;
    }

    @Override
    public Account getAccount(String name) {
        return accountManager.getAccount(name);
    }

    @Override
    public boolean saveAccount(Account account) {
        return accountManager.saveAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        accountManager.updateAccount(account);
    }
    
}
