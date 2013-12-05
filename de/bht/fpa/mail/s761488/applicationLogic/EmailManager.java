/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s761488.applicationLogic;

import de.bht.fpa.mail.s761488.model.Component;
import de.bht.fpa.mail.s761488.model.Email;
import de.bht.fpa.mail.s761488.model.EmailManagerIF;
import de.bht.fpa.mail.s761488.model.FileElement;
import de.bht.fpa.mail.s761488.model.Folder;
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
public class EmailManager implements EmailManagerIF {

	@Override
	public void loadEmails(Folder folder) {
		// Only load emails if they havn't already been loaded.
		if (folder.getEmails().isEmpty()) {
			try {
				File nodePath = new File(folder.getPath());

				FileFilter xmlFilter;
				xmlFilter = new XMLFileFilter();

				// Prepare our xml to POJO converter
				JAXBContext jc;
				jc = JAXBContext.newInstance(Email.class);
				Unmarshaller unmarshaller;
				unmarshaller = jc.createUnmarshaller();

				for (File item : nodePath.listFiles()) {
					Component newNode;
					// Only load XML files
					if (item.isFile() && xmlFilter.accept(item)) {
						newNode = new FileElement(item);
						Email email;
						email = (Email) unmarshaller.unmarshal(item);
						folder.addEmail(email);

						// This is added to show when 
						// and which xml is added to a 
						// node.
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
