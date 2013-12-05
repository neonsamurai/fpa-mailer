package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.controller.FXMLDocumentController;
import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.FileElement;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;
import java.io.FileFilter;


/*
 * This class manages a hierarchy of folders and their content which is loaded 
 * from a given directory path.
 * 
 * @author Simone Strippgen
 */
public class FileManager implements FolderManagerIF {

	//top Folder of the managed hierarchy
	Folder topFolder;
	DirectoryFilter filter;

	/**
	 * Constructs a new FileManager object which manages a folder hierarchy,
	 * where file contains the path to the top directory. The contents of
	 * the directory file are loaded into the top folder
	 *
	 * @param file File which points to the top directory
	 */
	public FileManager(File file) {
		this.topFolder = new Folder(file, true);
		this.filter = new DirectoryFilter();
	}

	/**
	 * Loads all relevant content in the directory path of a folder object
	 * into the folder.
	 *
	 * @param f the folder into which the content of the corresponding
	 * directory should be loaded
	 */
	@Override
	public void loadContent(Folder f) {
		if (f.getComponents().isEmpty()) {
			File folder = new File(f.getPath());
			for (File childFolder : folder.listFiles(filter)) {
				Component newNode;
				newNode = new Folder(childFolder, hasChildFolders(childFolder));
				f.addComponent(newNode);
			}
		}
	}

	private boolean hasChildFolders(File folder) {
		return folder.listFiles(filter).length > 0;
	}

	@Override
	public Folder getTopFolder() {
		return topFolder;
	}

	private class DirectoryFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

		public String getDescription() {
			return "Directory";

		}

	}
}
