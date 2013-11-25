/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.FileElement;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;

/**
 *
 * @author tim
 */
public class FileManager implements FolderManagerIF {

	private final File rootPath;
	private final Folder rootFolder;

	public FileManager(File path) {
		this.rootPath = path;
		this.rootFolder = new Folder(path, true);
	}

	@Override
	public Folder getRootFolder() {
		return rootFolder;
	}

	@Override
	public void loadContent(Folder node) {
		if (node.getComponents().isEmpty()) {
			File nodePath = new File(node.getPath());
			for (File item : nodePath.listFiles()) {
				Component newNode;
				if (item.isDirectory()) {
					newNode = new Folder(item, true);
					node.addComponent(newNode);
				} 
				if(item.isFile()) {
					newNode = new FileElement(item);
					node.addComponent(newNode);
				}

			}
		}
	}

}
