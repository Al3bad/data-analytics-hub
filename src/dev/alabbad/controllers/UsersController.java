package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class UsersController extends TableView<User> {
    public UsersController() {
        // construct columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<User, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.getColumns().add(usernameCol);
        this.getColumns().add(fnameCol);
        this.getColumns().add(lnameCol);
        // High light Admin users
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
                }

            }
        });
        this.onKeyPressedProperty().set(event -> this.onKeyPressed(event));
        this.populateTable();
    }

    private void deleteUser() throws SQLException, UserNotFoundException, UnauthorisedAction {
        TableViewSelectionModel<User> selectionModel = this.getSelectionModel();
        ObservableList<User> selectedItems = selectionModel.getSelectedItems();
        User selectedUser = (User) selectedItems.get(0);
        if (selectedUser instanceof AdminUser) {
            // TODO: show alert windows
            System.out.println("You're not allowed to delete admin user!");
        } else {
            DB.deleteUser(selectedUser.getUsername(), AppState.getInstance().getUser().getUsername());
        }
    }

    private void populateTable() {
        // get all users from db
        this.getItems().setAll();
        HashMap<String, User> users = DB.getAllUsers();
        for (String username : users.keySet()) {
            User user = users.get(username);
            this.getItems().add(user);
        }
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DELETE) || event.getCode().equals(KeyCode.BACK_SPACE)) {
            try {
                this.deleteUser();
                this.populateTable();
            } catch (UserNotFoundException e) {
                System.out.println("User not found!");
            } catch (SQLException e) {
                System.out.println("Something wrong happends!");
            } catch (UnauthorisedAction e) {
                System.out.println("Unauth action!");
            }
        }
    }
}
