package dev.alabbad.controllers;

import java.util.LinkedHashMap;

import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.MainScene;
import dev.alabbad.views.PortalScene;
import dev.alabbad.views.PrimaryButton;
import dev.alabbad.views.SecondaryButton;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class LoginFormController extends FormController {
    // TextField IDs & Labels
    private final static String USERNAME = "Username";
    private final static String PASSWORD = "Password";

    public LoginFormController() {
        super(createTextFieldElements(), new PrimaryButton("Login"), new SecondaryButton("Signup"));
        // NOTE: remove this later
        this.textFieldElements.get(USERNAME).setText("xv");
        this.textFieldElements.get(PASSWORD).setText("1234567");
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put(USERNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(PASSWORD, new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, true)));
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }
        String username = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String password = (String) this.textFieldElements.get(PASSWORD).getParsedVal();

        // Get user from DB
        User user = DB.loginUser(username, password);

        if (user == null) {
            this.beforeContainer.getChildren().setAll(new AlertView("Incorrect username or password!", "error"));
            return false;
        } else {
            // navigate to dashboard scene
            AppState.getInstance().setUser(user);
            Scene dashboardScene = new Scene(new MainScene());
            AppState.getInstance().switchScene(dashboardScene, true);
        }
        return true;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        Scene portalScene = new Scene(new PortalScene(new SignupFormController()));
        AppState.getInstance().switchScene(portalScene, false);
    }
}
