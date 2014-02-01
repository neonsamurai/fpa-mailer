package de.bht.fpa.mail.s761488.applicationLogic.account;


import de.bht.fpa.mail.s761488.model.Account;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class defines a dao class, that stores Account objects as
 * serialized data in a given file.
 * @author Simone Strippgen
 */
public class AccountFileDAO implements AccountDAOIF {

    public static final File ACCOUNT_FILE = new File(TestAccountProvider.TESTDATA_HOME, "Account.ser");

    public AccountFileDAO() {
        //for testing: serializes test accounts
        if (!ACCOUNT_FILE.exists()) {
            saveAccounts(TestAccountProvider.createAccounts());
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<Account>();
        Account acc = null;
        try {
            FileInputStream fileInput = new FileInputStream(ACCOUNT_FILE);
            ObjectInputStream is = new ObjectInputStream(fileInput);
            acc = (Account) is.readObject();
            while (acc != null) {
                list.add(acc);
                acc = (Account) is.readObject();
            }
            is.close();
        } catch (Exception ex) {
//            do nothing
        }
        return list;
    }

    @Override
    public Account saveAccount(Account acc) {
        if (acc != null) {
            List<Account> accounts = getAllAccounts();
            accounts.add(acc);
            saveAccounts(accounts);
        }
        return acc;
    }

    @Override
    public boolean updateAccount(Account acc) {
        if (acc != null) {
            List<Account> accounts = getAllAccounts();
            remove(acc.getName(), accounts);
            accounts.add(acc);
            saveAccounts(accounts);
        }
        return true;
    }

    private void saveAccounts(List<Account> accList) {
        try {
            File accountFile = ACCOUNT_FILE;
            System.out.println(accountFile);
            boolean deleted = accountFile.delete();
            FileOutputStream fileOutput = new FileOutputStream(accountFile);
            ObjectOutputStream os = new ObjectOutputStream(fileOutput);
            for (Account a : accList) {
                os.writeObject(a);
            }
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void remove(String name, List<Account> list) {
        Iterator<Account> it = list.iterator();
        while (it.hasNext()) {
            Account acc = it.next();
            if (acc.getName().equals(name)) {
                it.remove();
                return;
            }
        }
    }
}
