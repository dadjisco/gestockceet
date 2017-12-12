package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
@Table(name = "account")
public abstract class Account extends Model {

    @Required
    @OneToOne
    public Adress address;

    public Blob companyLogo;
    public String companyName;

    public boolean status;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<User> users;


    public Account() {
        this.status = true;
    }

    public Account(Adress address) {
        this.address = address;
        this.status = true;
    }

    public Account(Blob companyLogo, Adress address) {
        this.companyLogo = companyLogo;
        this.address = address;
        this.status = true;
    }

    public List<User> getUsers() {
        users = User.find("account = ?1", this).fetch();
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
