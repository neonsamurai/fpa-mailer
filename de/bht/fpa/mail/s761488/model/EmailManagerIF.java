/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.model;

import java.io.File;
import javafx.collections.ObservableList;

/**
 * This interface should be used to implement classes which manage Email
 * objects.
 *
 * @author tim.jagodzinski@gmail.com
 */
public interface EmailManagerIF {

    /**
     * Reads emails from a folder object and makes them available as Email
     * objects.
     *
     * @param folder The source folder from which emails should be loaded.
     */
    void loadEmails(Folder folder);

    public ObservableList<Email> getEmailList();

    public ObservableList<Email> getEmailListFiltered();

    public void updateEmailListFiltered(String filterString);

    public void updateEmailList(Folder folder);

    public void saveEmails(File destination);

}
