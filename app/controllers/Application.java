package controllers;

import java.io.IOException;
import play.mvc.*;
import models.*;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.libs.Images;

public class Application extends Controller {

    public static void index() {
        flash.put("slider", "true");
        render();
    }
        
    public static void unAuthaurizedTransaction() {
        render();
    }
        
    public static void ads() {
//        flash.put("slider", "true");
        render();
    }

    public static void services() {
        render();
    }

    public static void faq() {
        render();
    }

    public static void contact() {
        render();
    }

    public static void image(Blob image) throws IOException {
        //ne fonctionne pas
        response.setContentTypeIfNotSet(image.type());
        renderBinary(image.get());
    }

    /**
     *
     * @param id http://www.playframework.com/documentation/1.2.7/guide5 See
     * associated route in routes.conf
     */
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#006738");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }


    /**
     * TODO set HTTP error Codes Application.error(errorCode) One page for all
     * error codes send http error status code (or custom status code) and
     * display corresponding error in template
     *
     * TODO render using error views folder 401.html / 403.html Code :
     * response.status = Http.StatusCode.UNAUTHORIZED; error()
     */
    public static void unAuthorized() {
        // Ask forBrowser Authentication
        // unauthorized();
        response.status = Http.StatusCode.UNAUTHORIZED;
        render();

    }

    public static void init() throws IOException {
        Adress adress1 = new Adress("Domicilié à Lomé", "LOME", "TOGO", "04", "963").save();
        Administrator admin1 = new Administrator(adress1).save();
        User user = new User("admin", "admin", "ceetadmin", "email@yahoo.fr", admin1);
        user.setPassword("ceetadmin");
        user.save();
        renderHtml("C'est fini");
    }


}