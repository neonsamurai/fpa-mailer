/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.FileElement;
import de.bht.fpa.mail.s761488.model.Folder;
import de.bht.fpa.mail.s761488.model.FolderManagerIF;
import java.io.File;
import java.io.FileFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author tim
 */
public class EmailManager implements FolderManagerIF {

	private final Folder rootFolder;
	private final File rootPath;

	public EmailManager(File path) {
		this.rootPath = path;
		this.rootFolder = new Folder(path, true);
	}

	@Override
	public Folder getRootFolder() {
		return rootFolder;
	}

	@Override
	public void loadContent(Folder node) {
		// Only load content if it hasn't already been loaded.
		if (node.getComponents().isEmpty()) {
			try {
				File nodePath = new File(node.getPath());

				FileFilter xmlFilter;
				xmlFilter = new XMLFileFilter();

				JAXBContext jc;
				jc = JAXBContext.newInstance(Email.class);
				Unmarshaller unmarshaller;
				unmarshaller = jc.createUnmarshaller();

				for (File item : nodePath.listFiles()) {
					Component newNode;
					if (item.isDirectory()) {
						newNode = new Folder(item, true);
						node.addComponent(newNode);
					}
					// Only load XML files
					if (item.isFile() && xmlFilter.accept(item)) {
						newNode = new FileElement(item);
						node.addComponent(newNode);
						Email email;
						email = (Email) unmarshaller.unmarshal(item);
						node.addEmail(email);
						
						final String s = " | ";
						System.out.println(
							"(Adding) [Email: "
							+ email.getSender() + s
							+ email.getReceived() + s
							+ email.getSubject()
							+ "]");
					}

				}
			} catch (JAXBException ex) {
				Logger.getLogger(EmailManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static class XMLFileFilter implements FileFilter {

		public XMLFileFilter() {
		}

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith("xml");
		}
	}

}
