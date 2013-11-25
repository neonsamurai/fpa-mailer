/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bht.fpa.mail.s761488.model;

/**
 *
 * @author tim
 */
public interface FolderManagerIF {
	
	public Folder getRootFolder();
	public void loadContent(Folder folder);
	
}
