package hr.algebra.iisusers.users.exception;

// Unchecked exception — thrown by UserService when a requested ID does not exist in the DB
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User with id " + id + " was not found.");
    }
}
