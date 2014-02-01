package de.bht.fpa.mail.s761488.applicationLogic.account;


import de.bht.fpa.mail.s761488.model.Account;
import de.bht.fpa.mail.s761488.model.Folder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the name of the directory where the testdata
 * for the accounts can be found and is able to produce some test accounts. 
 * @author Simone Strippgen
 */
class TestAccountProvider {

//  directory (relative to project) where the corresponding email folders can be found
    public static final File TESTDATA_HOME = new File("TestData");

    /**
     * @eturn a list of accounts, with top folder paths which
     * refer to the folder with the testdata.
     */
    public static List<Account> createAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        Account acc = new Account("Walter", "smtp.gmail.com", "walter@gmail.com", "walter");
        //Sets the path to the corresponding email folder
        Folder folder = new Folder(new File(TESTDATA_HOME, "Walter"), true);
        acc.setTop(folder);
        accounts.add(acc);
        acc = new Account("Erna", "smtp.gmail.com", "erna@gmail.com", "erna");
        folder = new Folder(new File(TESTDATA_HOME, "Erna"), true);
        acc.setTop(folder);
        accounts.add(acc);
        acc = new Account("Anna", "smtp.gmail.com", "anna@gmail.com", "anna");
        folder = new Folder(new File(TESTDATA_HOME, "Anna"), true);
        acc.setTop(folder);
        accounts.add(acc);
        return accounts;
    }
}
