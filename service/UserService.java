package service;

import dao.UserDAO;
import model.User;
import util.PasswordHasher;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public User login(String email, String password) {

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        String passwordHash = PasswordHasher.hashPassword(password);

        if (!user.getPasswordHash().equals(passwordHash)) {
            return null;
        }

        return user;
    }
public boolean registerUser(
        String fullName,
        String email,
        String password,
        String phone,
        String gender) {

    if (userDAO.findByEmail(email) != null) {
        return false;
    }

    String passwordHash =
            PasswordHasher.hashPassword(password);

    User user = new User();

    user.setFullName(fullName);
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setPhone(phone);
    user.setGender(gender);
    user.setRole("CUSTOMER");

    return userDAO.registerUser(user);
}

}