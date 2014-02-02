/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bht.fpa.mail.s761488.applicationLogic.account;

import de.bht.fpa.mail.s761488.model.Account;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author tim
 */
public class AccountDBDAO implements AccountDAOIF{
    
    EntityManagerFactory emf;
    
    public AccountDBDAO() {
        TestDBDataProvider.createAccounts();
        emf = Persistence.createEntityManagerFactory("fpa");
    }

    @Override
    public List<Account> getAllAccounts() {
        EntityManager em = emf.createEntityManager();
        List<Account> accounts;
        
        Query q;
        q = em.createQuery("SELECT x FROM Account x");
        
        accounts = q.getResultList();
        em.close();
        
        return accounts;
    }

    @Override
    public Account saveAccount(Account acc) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trans = em.getTransaction();
        
        trans.begin();
        em.persist(acc);
        trans.commit();
        em.close();
        return acc;
    }

    @Override
    public boolean updateAccount(Account acc) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trans = em.getTransaction();
        
        trans.begin();
        em.merge(acc);
        trans.commit();
        em.close();
        
        return true;
    }
    
}
