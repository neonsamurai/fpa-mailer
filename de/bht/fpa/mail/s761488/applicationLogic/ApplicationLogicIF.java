package de.bht.fpa.mail.s761488.applicationLogic;


import de.bht.fpa.mail.s761488.model.Account;
import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.Folder;
import java.io.File;
import java.util.List;

/**
 * This Interface defines the methods which are needed 
 * to manage emails, folders and accounts
 * .
 * @author Simone Strippgen
 */
public interface ApplicationLogicIF {

    /**
     * Get current root folder.
     * @return current root folder.
     */
    Folder getTopFolder();

    /**
     * Loads all relevant content in the directory path of a folder
     * into the folder.
     * @param f the folder into which the content of the corresponding 
     *          directory should be loaded
     */
    void loadContent(Folder folder);

    /**
     * Searches for all emails in the selected folder that contain the 
     * given pattern.
     * @return a list of all emails that contain the pattern
     * @param pattern contains a string for comparison with email content
     */
    List<Email> search(String pattern);

    /**
     * Loads all emails in the directory path of the given folder
     * as objects of the class Email into the folder.
     * @param folder    the folder into which the emails of the corresponding 
     *                  directory should be loaded
     */
    void loadEmails(Folder folder);

    /**
     * Changes the root directory of the application and initializes the
     * folder manager with the new root directory.
     * @param file  the path to the directory which was selected as 
     *              the new root directory of the application.
     */
    void changeDirectory(File file);

    /**
     * Saves the email objects of the selected folder into the given
     * directory.
     * @param file  the path to the directory in which the email objects
     *              should be saved.
     */
    void saveEmails(File file);
    
    
    // NEU in Uebung 8
    
    /**
     * Sets a selected account as the new working account, and initializes
     * the folder manager with the top Folder of the account.
     * @param name  name of the account which should be set as
     *              the current working account.
     */
    void openAccount(String name);

    /**
     * @return a list of all account names.
     */
    List<String> getAllAccounts();

    /**
     * @return account with the given name.
     * If no account with this name exists, it returns null.
     * @param name  name of the account 
     */
    Account getAccount(String name);
    
    /**
     * Saves the given Account in the datastore.
     * @param account  the account that should be saved
     * @return true if an account with this name did not exist.
     */
    boolean saveAccount(Account account);
    
    /**
     * Updates the given Account in the datastore.
     * @param account  the account that should be updated
     */
    void updateAccount(Account account);
}
