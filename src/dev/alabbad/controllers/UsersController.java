package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Users view/controller for showing the list of all users in the system. This
 * view will only be shown to admin users.
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class UsersController extends TableView<User> {
    public UsersController() {
        this.setupView();
        this.setupControllers();
        this.populateTable();
    }

    /**
     * Setup view
     */
    private void setupView() {
        // construct columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<User, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        // add columns to table
        this.getColumns().add(usernameCol);
        this.getColumns().add(fnameCol);
        this.getColumns().add(lnameCol);
    }

    /**
     * Hook controllers to view elements
     */
    private void setupControllers() {
        // on row change --> highlight admin users in red
        this.setRowFactory(row -> new TableRow<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item instanceof AdminUser) {
                    this.setStyle("-fx-selection-bar: #F8B7B2;");
                    if (isSelected()) {
                        this.setStyle("-fx-selection-bar: #F87E75;");
                    } else {
                        this.setStyle("-fx-background-color: #F8B7B2;");
                    }
                } else {
                    this.setStyle("");
                }

            }
        });
        // on key pressed
        this.onKeyPressedProperty().set(event -> this.onKeyPressed(event));
    }

    /**
     * [INTERACTS WITH VIEW] Key press handler on the table view. Delete the
     * selected user when the delete/backspace button is pressed.
     *
     * @param event key event
     */
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DELETE) || event.getCode().equals(KeyCode.BACK_SPACE)) {
            try {
                // get the user object from the selected row
                TableViewSelectionModel<User> selectionModel = this.getSelectionModel();
                ObservableList<User> selectedItems = selectionModel.getSelectedItems();
                User selectedUser = (User) selectedItems.get(0);
                if (selectedUser instanceof AdminUser) {
                    // prevent deleting admin users
                    this.showDeletionNotAllowedDialog();
                } else if (selectedUser instanceof User && this.confirmDeletionDialog(selectedUser) == true) {
                    // repopulate table with users after confirmating the deletion
                    this.populateTable();
                }
            } catch (UserNotFoundException e) {
                System.out.println("User not found!");
                this.populateTable();
            } catch (SQLException e) {
                System.out.println("Something wrong happends!");
            } catch (UnauthorisedAction e) {
                System.out.println("Unauth action!");
            }
        }
    }

    /**
     * [INTERACTS WITH VIEW] Confirmation for user deletion
     *
     * @param selectedUser
     * @return `true` when user confirms, `false` otherwise.
     * @throws SQLException When faild to perform sql operation.
     * @throws UserNotFoundException When the current logged in user is not found in
     * the database
     * @throws UnauthorisedAction When the current logged in user is admin
     */
    public Boolean confirmDeletionDialog(User selectedUser)
                    throws SQLException, UserNotFoundException, UnauthorisedAction {
        // create and show conformation dialog
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Delete User Conformation");
        dialog.setContentText(
                        "All posts associated to this user will be deleted as well? Are you sure you want to proceed?");
        ButtonType deleteBtn = new ButtonType("Delete", ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(deleteBtn);
        dialog.getDialogPane().getButtonTypes().add(cancelBtn);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
            // delete user
            DB.deleteUser(selectedUser.getUsername(), AppState.getInstance().getUser().getUsername());
            return true;
        }
        return false;
    }

    /**
     * [INTERACTS WITH VIEW] Show a dialog when attempting to delete an admin user.
     * This dialog just shows a user friendly message that the operatino is not
     * allowed
     */
    public void showDeletionNotAllowedDialog() {
        // create and show logout conformation dialog
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Not Allowed Operation");
        dialog.setContentText("Admin user cannot be deleted!");
        ButtonType deleteBtn = new ButtonType("Ok", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(deleteBtn);
        dialog.showAndWait();
    }

    /**
     * [INTERACTS WITH VIEW] Get all users from the database then populate the
     * tables.
     */
    private void populateTable() {
        // get all users from db
        this.getItems().setAll();
        HashMap<String, User> users;
        try {
            users = DB.getAllUsers();
            for (String username : users.keySet()) {
                User user = users.get(username);
                this.getItems().add(user);
            }
        } catch (SQLException e) {
        }
    }
}
