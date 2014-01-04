/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.Folder;
import java.io.File;
import java.util.List;

/**
 *
 * @author tim
 */
public class ApplicationLogic implements ApplicationLogicIF {
    
    private final EmailManager emailManager;
    private final FileManager fileManager;
    
    public ApplicationLogic(File directory){
        emailManager = new EmailManager();
        fileManager = new FileManager(directory);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEmails(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
