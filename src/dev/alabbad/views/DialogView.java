package dev.alabbad.views;

import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * Dialog view
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DialogView extends Dialog<ButtonType> {
    @FunctionalInterface
    public interface Function {
        void run();
    }

    // dialog without primary button handler
    public DialogView(String title, String content, String primaryBtnStr) {
        this.initDialog(title, content, primaryBtnStr, null);
        this.primaryBtnHandler(this.showAndWait(), null);
    }

    // dialog with primary button handler
    public DialogView(String title, String content, String primaryBtnStr, String secondaryBtnStr,
                    Function onPrimaryBtnClicked) {
        this.initDialog(title, content, primaryBtnStr, secondaryBtnStr);
        this.primaryBtnHandler(this.showAndWait(), onPrimaryBtnClicked);
    }

    /**
     * Inititialise dialog
     *
     * @param title
     * @param content
     * @param primaryBtnStr
     * @param secondaryBtnStr
     */
    private void initDialog(String title, String content, String primaryBtnStr, String secondaryBtnStr) {
        this.setTitle(title);
        this.setContentText(content);
        // add primary btn
        ButtonType primaryBtn = new ButtonType(primaryBtnStr, ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(primaryBtn);
        // add secondary btn
        if (secondaryBtnStr != null) {
            ButtonType secondaryBtn = new ButtonType(secondaryBtnStr, ButtonData.CANCEL_CLOSE);
            this.getDialogPane().getButtonTypes().add(secondaryBtn);
        }
    }

    /**
     * Handler runner
     *
     * @param result
     * @param handler
     */
    private void primaryBtnHandler(Optional<ButtonType> result, Function handler) {
        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE && handler != null) {
            handler.run();
        }
    }
}
