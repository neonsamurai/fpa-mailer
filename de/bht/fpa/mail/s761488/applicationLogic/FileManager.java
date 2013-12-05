package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.FileElement;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;


/*
 * This class manages a hierarchy of folders and their content which is loaded 
 * from a given directory path.
 * 
 * @author Simone Strippgen
 */
public class FileManager implements FolderManagerIF {

	//top Folder of the managed hierarchy
	Folder topFolder;

	/**
	 * Constructs a new FileManager object which manages a folder hierarchy,
	 * where file contains the path to the top directory. The contents of the
	 * directory file are loaded into the top folder
	 *
	 * @param file File which points to the top directory
	 */
	public FileManager(File file) {
		this.topFolder = new Folder(file, true);
	}

	/**
	 * Loads all relevant content in the directory path of a folder object into
	 * the folder.
	 *
	 * @param f the folder into which the content of the corresponding directory
	 * should be loaded
	 */
	@Override
	public void loadContent(Folder f) {
		if (f.getComponents().isEmpty()) {
			File folder = new File(f.getPath());
			for (File item : folder.listFiles()) {
				Component newNode;
				if (item.isDirectory()) {
					// TODO: only set expandable if it has subfolders!
					newNode = new Folder(item, true);
					f.addComponent(newNode);
				}
				if (item.isFile()) {
					newNode = new FileElement(item);
					f.addComponent(newNode);
				}

			}
		}
	}

	@Override
	public Folder getTopFolder() {
		return topFolder;
	}
}
