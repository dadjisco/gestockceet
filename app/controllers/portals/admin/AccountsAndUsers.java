package controllers.portals.admin;

import java.util.List;

import controllers.Secure;
import controllers.SessionHandler;
import models.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.mvc.*;
import utils.config.AccountType;
import play.libs.Codec;
import play.data.validation.Validation;

@With({SessionHandler.class, Secure.class})
public class AccountsAndUsers extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "acc_user");
    }

    public static void index() {
        List<User> users = User.findAll();
//        List<User> usersAdmin = User.find("account.dtype=?1", "Administrator").fetch();
//        List<User> usersMagasinier = User.find("account.dtype=?1", "Magasinier").fetch();
//        List<User> usersSoustraitant = User.find("account.dtype=?1", "Soustraitant").fetch();
        render(users);
    }

    public static void newAccount(String type) {
        String randomID = Codec.UUID();
        List<Profil> profils = Profil.findAll();
        render(type, randomID, profils);
    }

    public static void editAccount(Long id, String type) {
        Account account = null;
        if (type.equals(AccountType.MAGASINIER.getName())) {
            account = Magasin.findById(id);
        }
        if (type.equals(AccountType.AGENCE.getName())) {
            account = Agence.findById(id);
        }
        if (type.equals(AccountType.ADMINISTRATOR.getName())) {
            account = Administrator.findById(id);
        }
        if (type.equals(AccountType.SOUSTRAITANT.getName())) {
            account = models.Soustraitant.findById(id);
        }
        render(type, account);

    }
        
    public static void saveNewAccount(User user, Adress address,String companyName, @Required String code, 
            String randomID, String accountType, String passwordCheck, Long profil) {
        // Check Captcha
        validation.equals(code, Cache.get(randomID)).message("Invalid code. Please try again");
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            //JPA.setRollbackOnly();
            newAccount(accountType);
        }
        validation.required(user.firstname);
        validation.required(user.lastname);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            //JPA.setRollbackOnly();
            newAccount(accountType);
        }
        
        if (accountType.equals(AccountType.AGENCE.getName())) {
            validation.required(profil);
            if (validation.hasErrors()) {
                params.flash();
                validation.keep();
                //JPA.setRollbackOnly();
                newAccount(accountType);
            }
            user.profil = Profil.findById(profil);
            address.save();
//            Agence agence = new Agence(companyName).save();
//            user.account = agence;
            user.save();
            flash.put("success", true);
        }
//==> Save Adresse
            address.save();

//==> Save Account
            if(profil!=null){
                user.profil = Profil.findById(profil);
            }else{
                user.profil = null;
            }
//            if(accountType.compareToIgnoreCase("Agence")==1){
//                Agence agence = new Agence(companyName).save();
//                user.account = agence;
//            }
            if(accountType.compareToIgnoreCase("Magasinier")==0){
                Magasinier magasinier = new Magasinier(address).save();
                user.account = magasinier;
            }
            if(accountType.equalsIgnoreCase("Soustraitant")){
                models.Soustraitant sousTraitant = new models.Soustraitant(address);
                sousTraitant.raisonSociale = companyName; 
                sousTraitant.save();
                user.account = sousTraitant;
            }
            if(accountType.compareToIgnoreCase("Administrator")==0){
                Administrator administrator = new Administrator(address).save();
                user.account = administrator;
            }
//                        else if(accountType.compareToIgnoreCase("ChefAgence")==1){
//                ChefAgence chefAgence = new ChefAgence(companyName, address);
//                user.account = chefAgence;
//            }
            
//==> Save User
            user.setPassword(passwordCheck);
            user.save();
            newAccount(accountType);
            flash.put("success", true);
        }
    
        public static void saveEditAccount(String type, Long id, Adress address,
            String companyName) {

            Account account = null;
            if (type.equals(AccountType.AGENCE.getName())) {
                account = Agence.findById(id);
            }
            if (type.equals(AccountType.MAGASINIER.getName())) {
                account = Magasinier.findById(id);
            }
            if (type.equals(AccountType.SOUSTRAITANT.getName())) {
                account = models.Soustraitant.findById(id);
            }
            address.save();
            account.address = address;
            account.save();
            flash.put("success", true);
        }
    

    public static void deleteAccount(long id) {
        Account acc = Account.findById(id);
        for (User u : acc.getUsers()) {
            u.refresh();
            u.delete();
        }
        //acc = acc.findById(acc.id);
        //acc.delete();
        index();
    }

    public static void deleteUser(Long id) {
        // code here
        index();
    }

    public static void editUser(Long id) {
        User user = User.findById(id);
        List<Profil> profils = Profil.findAll();
        render(user, profils);
    }

    public static void addUser(Long id) {
        Account account = Account.findById(id);
        List<Profil> profils = Profil.findAll();

        if (Cache.get("profil", Profil.class) != null) {
            renderArgs.put("profil", Cache.get("profil"));
            Cache.delete("profil");
        } else {
            renderArgs.put("profil", new Profil());
        }
        render(account, profils);
    }

    public static void doAddUser(long accountId, User user, @Required(message = "Un profil est requis pour l'utilisateur") Long p) {
        if (Validation.hasErrors()) {
            if (p != null) {
                Profil profil = Profil.findById(p);
                Cache.set("profil", profil, "2mn");
            }
            params.flash();
            Validation.keep();
            addUser(accountId);
        }
        Profil profil = Profil.findById(p);
        user.profil = profil;
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            //JPA.setRollbackOnly();
            addUser(accountId);
        }
        Account account = Account.findById(accountId);
        user.account = account;
        user.save();
        flash.put("success", "Un nouvel utilisateur a été ajouté avec succès");
        index();
    }

    public static void doEditUser(@Required User user, Boolean editPassword,
            String currentPassword,
            String passwordCheck, Long p) {
        /*String nomInitial = user.firstname;
         String nomFinal = nomInitial + "1";
         user.firstname = nomFinal;
         */
        if (!(user.account instanceof Agence)) {
            validation.required(p);
            if (validation.hasErrors()) {
                params.flash();
                validation.keep();
            }
            Profil profil = Profil.findById(p);
            user.profil = profil;
        }
        if (editPassword != null) {
            validation.required(currentPassword);
            validation.required(passwordCheck);
            validation.valid(user);

            if (!validation.hasErrors() && user.isValidPwdById(user.id, currentPassword)) {

                user.validateAndSave();
            }
        } else {
            user.validateAndUpdate(false);
        }
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        } else {
            flash.put("success", true);
        }
        index();
    }

    public static void detailsAccount(long id) {
        Account account = Account.findById(id);
        render(account);
    }
    }


