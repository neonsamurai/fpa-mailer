package de.bht.fpa.mail.s761488.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author Simone Strippgen
 *
 */

@Entity
public class Folder extends Component implements Serializable{

    @Id
    @GeneratedValue
    private Long id;
    
    private final boolean expandable;
    
    
    @Transient
    private final transient ArrayList<Component> content;
    @Transient
    private transient List<Email> emails;

    /**
     *
     */
    public Folder() {
        this.expandable = false;
        this.content = new ArrayList<>();
    }

    public Folder(File path, boolean expandable) {
        super(path);
		this.emails = new ArrayList<>();
        this.expandable = expandable;
        content = new ArrayList<>();
        emails = new ArrayList<>();
    }

	/**
	 *
	 * @return
	 */
	@Override
    public boolean isExpandable() {
        return expandable;
    }

    @Override
    public void addComponent(Component comp) {
        content.add(comp);
    }

    @Override
    public List<Component> getComponents() {
        return content;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void addEmail(Email message) {
        emails.add(message);
    }
    
    @Override
    public String toString(){
	    String s;
	    if(this.getEmails().size()> 0){
		    s = getName() + "(" + this.getEmails().size() + ")";
	    }else{
		    s = getName();
	    }
	    return s;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
 }
