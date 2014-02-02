package de.bht.fpa.mail.s761488.applicationLogic.account;



import de.bht.fpa.mail.s761488.model.Account;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * This class gets test accounts from TestAccountProvider and saves them
 * into the DB which is specified in the PersistenceUnit. 
 * It can be used to fill a local DB with the test accounts matching the 
 * corresponding account folders.
 * @author Simone Strippgen
 */

public class TestDBDataProvider {

    private static final String TESTDATA_PU = "fpa";
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(TESTDATA_PU);


    public static void createAccounts() {
        EntityManager em = emf.createEntityManager();
        List<Account> accs = TestAccountProvider.createAccounts();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        for (Account a : accs) {
            em.persist(a);
        }
        trans.commit();
        em.close();
    }
}
