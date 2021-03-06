package pl.dbjllmjk.Controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pl.dbjllmjk.Model.AccountData;
import pl.dbjllmjk.Exceptions.BadPasswordException;
import pl.dbjllmjk.Exceptions.NoSuchUserException;
import pl.dbjllmjk.Model.UserData;
import pl.dbjllmjk.Utils.PasswordHashConverter;
import pl.dbjllmjk.View.LoginView;

/**
 * Controls the Logging Module.
 */
public class LoginController {

    private Controller controller;

    public LoginController(Controller controller) {
        this.controller = controller;
        new LoginView(this);
    }

    public LoginController(Controller c, int k) {
        this.controller = c;

    }

    /**
     * Perform logging.
     *
     * @param login user/admin login
     * @param password user/admin password
     * @throws NoSuchUserException
     * @throws BadPasswordException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public void tryToLog(String login, String password) throws NoSuchUserException, BadPasswordException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Optional<AccountData> selectedAccount = Stream
                .concat(this.controller.getDataRepository().getAdmins().stream(),
                        this.controller.getDataRepository().getUsers().stream())
                .collect(Collectors.toList())
                .stream().
                filter(l -> l.getLogin().equals(login))
                .findFirst();
        if (!selectedAccount.isPresent()) {
            throw new NoSuchUserException();
        }
        if (!PasswordHashConverter.checkPassword(selectedAccount.get().getLogin(), password, selectedAccount.get().getPassword())){
            throw new BadPasswordException();
        }
        
        this.controller.afterLogin(selectedAccount.get());
    }
    public void tryToLogT(String login, String password) throws NoSuchUserException, BadPasswordException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Optional<AccountData> selectedAccount = Stream
                .concat(this.controller.getDataRepository().getAdmins().stream(),
                        this.controller.getDataRepository().getUsers().stream())
                .collect(Collectors.toList())
                .stream().
                        filter(l -> l.getLogin().equals(login))
                .findFirst();
        if (!selectedAccount.isPresent()) {
            throw new NoSuchUserException();
        }
        if (!PasswordHashConverter.checkPassword(selectedAccount.get().getLogin(), password, selectedAccount.get().getPassword())){
            throw new BadPasswordException();
        }
        this.controller.afterLoginT(selectedAccount.get());
    }

    /**
     * Process of registration.
     *
     * @param login new account login
     * @param password new account password
     * @param name new account name
     * @param surname new account surname
     * @throws NoSuchUserException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public void addAccount(String login, String password, String name, String surname) throws NoSuchUserException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (login.trim().length() < 3 || password.trim().length() < 3 || name.trim().length() < 3 || surname.trim().length() < 3) {
            throw new NoSuchUserException("To short fields!");
        }
        Optional<UserData> oud = this.controller.getDataRepository().getUsers()
                .stream()
                .filter(l -> l.getLogin().equals(login))
                .findFirst();
        if (oud.isPresent()) {
            throw new NoSuchUserException("User " + login + " already exists");
        }
        UserData newUser = new UserData(login, PasswordHashConverter.hashPassword(login, password), name, surname);
        this.controller.getDataRepository().addData(newUser);
    }
}
