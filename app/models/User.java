package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.Codec;
import java.util.StringTokenizer;
import javax.persistence.FetchType;
import play.db.jpa.Model;

@Entity
@Table(name = "user")
public class User extends Model {

    @Required
    @MinSize(4)
    /*@Unique TODO: Remmettre cette contrainte; en modifiant elle ne doit pas etre utilisee */
    public String username;

    @Required
    @Email
    /*@Unique TODO: Remmettre cette contrainte; en modifiant elle ne doit pas etre utilisee */
    public String email;

    @Required
    @Password
    @Transient
    @CheckWith(PasswordStrengthCheck.class)
    public String password;

    public String passwordHash;

    @Required
    public String firstname;

    @Required
    public String lastname;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    public Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    public Profil profil;

    @OneToMany(mappedBy = "controleur", fetch = FetchType.LAZY)
    private List<PoseCompteurAppreciation> appreciations;

    public User() {
    }

    public User(String firstname, String lastname, String username, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
    }

    public User(String firstname, String lastname, String username, String email, Account account) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.account = account;
    }

    public User(String firstname, String lastname, String username, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String firstname, String lastname, String username, String email, String password, Account account) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.account = account;
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
            this.passwordHash = setHashPassword(password);
        }
    }

    public String getAccountType() {
        return (this.account != null) ? this.account.getClass().getSimpleName() : "null";
    }

    public List<PoseCompteurAppreciation> getAppreciations() {
        appreciations = PoseCompteurAppreciation.find("controleur = ?1", this).fetch();
        return appreciations;
    }

    public void setAppreciations(List<PoseCompteurAppreciation> appreciations) {
        this.appreciations = appreciations;
    }

    public static String setHashPassword(String password) {
        return Codec.hexSHA1(password);
    }

    static class PasswordStrengthCheck extends Check {

        @Override
        public boolean isSatisfied(Object user, Object password) {
            return true;//containsLettersAndNumbers(password);
        }
    }

    public static boolean isValidLogin(String username, String password) {
        // return TRUE if there is a single matching username/passwordHash
        return (count("username=?1 AND passwordHash=?2", username,
                setHashPassword(password)) == 1);
    }

    public static boolean isValidPwdById(Long id, String password) {
        // return TRUE if there is a single matching username/passwordHash
        return (count("id=? AND PasswordHash=?2", id,
                setHashPassword(password)) == 1);
    }

    @Override
    public boolean validateAndSave() {
        Boolean trust = false; // or truth
        Validation.current().valid(this); // Valid user fields
        if (!Validation.current().hasErrors()) { // Keep existing errors (register, newPrivate...)
            save();
            trust = true;
        }
        // Valid password complexity => done with @CheckWith
        // Valid doublon
        return trust;
    }

    /**
     * Play handle automatically the update case with the save method. However
     * we have to handle a specific behavior here : When we don't want to update
     * user password, corresponding field is null while is required...
     *
     * @return
     */
    public boolean validateAndUpdate(boolean pwd) {
        Validation.current().valid(this);
        if (!pwd) {
            System.out.println("\n boolean pwd " + pwd);
            if (Validation.current().errors().size() == 2 && Validation.current().hasError("user.password")) {
                // Only pwd required error
                Validation.current().clear();
            } else {
                // Keep Other errors
                List<play.data.validation.Error> errors = Validation.current().errors();
                Validation.current().clear();
                for (play.data.validation.Error err : errors) {

                    if (!err.getKey().equals("user.password")) {
                        Validation.current().addError(err.getKey(), err.message());
                    }
                }
            }
        }
        if (Validation.current().hasErrors()) {
            return false;
        } else {
            save();
            return true;
        }
    }

    public boolean hasAccessTo(String menu) {
        if (this.profil == null) {
            return false;
        }
        String droit = this.profil.droit;
        for (StringTokenizer stringTokenizer = new StringTokenizer(droit, ";"); stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            if (menu.toUpperCase().equals(token.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public String haveAccessTo() {
        if (this.profil == null) {
            String droit = this.profil.droit;
            for (StringTokenizer stringTokenizer = new StringTokenizer(droit, ";"); stringTokenizer.hasMoreTokens();) {
                String token = stringTokenizer.nextToken();
                return token;
            }
        }
        return "";
    }

}
