package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;
import play.data.validation.Required;

/**
 *
 * @author birkhoff
 */
@Entity
@Table(name = "profil", uniqueConstraints = @UniqueConstraint(columnNames = {"libProfil"}))

public class Profil extends Model {

    @Required(message = "Le libellé du profil est obligatoire")
    public String libProfil;
    @Required
    public String droit;

    @OneToMany(mappedBy = "profil", fetch = FetchType.LAZY)
    protected List<User> users;

    public Profil() {

    }

    public Profil(String libProfil, String droit) {
        this.libProfil = libProfil;
        this.droit = droit;
    }

    public List<User> getUsers() {
        this.users = User.find("profil = ?", this).fetch();
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
