/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.applicationLogic.account.AccountDAOIF;
import de.bht.fpa.mail.s761488.applicationLogic.account.AccountDBDAO;
import de.bht.fpa.mail.s761488.applicationLogic.account.AccountFileDAO;
import de.bht.fpa.mail.s761488.applicationLogic.account.AccountManagerIF;
import de.bht.fpa.mail.s761488.model.Account;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author tim
 */
public class AccountManager implements AccountManagerIF{
    
    AccountDAOIF dao = new AccountDBDAO();

    @Override
    public Account getAccount(String name) {
        Account acc = null;
        ListIterator allAccounts = getAllAccounts().listIterator();
        
        while(allAccounts.hasNext()){
            acc = (Account) allAccounts.next();
            if(name.equals(acc.getName())){
                return acc;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        return dao.getAllAccounts();
    }

    @Override
    public boolean saveAccount(Account acc) {
        Account savedAccount;
        savedAccount = dao.saveAccount(acc);
        return savedAccount.equals(acc);
    }

    @Override
    public boolean updateAccount(Account account) {
        return dao.updateAccount(account);
    }
}
