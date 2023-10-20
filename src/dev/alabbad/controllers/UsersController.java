package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.Model;
import dev.alabbad.models.User;
import dev.alabbad.views.DialogView;
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
     * Key press handler on the table view. Delete the selected user when the
     * delete/backspace button is pressed.
     *
     * @param event key event
     */
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DELETE) || event.getCode().equals(KeyCode.BACK_SPACE)) {
            // get the user object from the selected row
            User selectedUser = (User) this.getSelectionModel().getSelectedItems().get(0);
            // Conformation dialog
            if (selectedUser instanceof AdminUser) {
                new DialogView("Not Allowed Operation", "Admin user cannot be deleted!", "OK");
            } else {
                new DialogView("Delete User Conformation",
                                "All posts associated to this user will be deleted as well? Are you sure you want to proceed?",
                                "Delete", "Cancel", () -> {
                                    try {
                                        // delete user
                                        Model.getUserDao().delete(selectedUser, AppState.getInstance().getUser());
                                        Model.getPostDao().deleteAll(selectedUser, AppState.getInstance().getUser());
                                    } catch (EntityNotFoundException e) {
                                        new DialogView("User Not Found", "User is not found in the database!", "OK");
                                        this.populateTable();
                                    } catch (UnauthorisedAction e) {
                                        new DialogView("Not Allowed Operation", "Admin user cannot be deleted!", "OK");
                                    } catch (SQLException e) {
                                        new DialogView("DB Error",
                                                        "Something wrong happend! Please contact the developer", "OK");
                                    }
                                });
                this.populateTable();
            }
        }
    }

    /**
     * Get all users from the database then populate the tables.
     */
    private void populateTable() {
        // get all users from db
        this.getItems().setAll();
        HashMap<String, User> users;
        try {
            users = Model.getUserDao().getAll();
            for (String username : users.keySet()) {
                User user = users.get(username);
                this.getItems().add(user);
            }
        } catch (SQLException e) {
        }
    }
}
