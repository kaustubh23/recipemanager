package recipe.manager.recipemanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipe.manager.recipemanager.entity.User;
import recipe.manager.recipemanager.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        logger.info("Adding user: {}", user);
        User savedUser = userRepository.save(user);
        logger.info("User added with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    public User getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("User not found with ID: {}", userId);
        } else {
            logger.info("User found with ID: {}", userId);
        }
        return user;
    }

    public User getUserByUserName(String userName) {
        logger.info("Fetching user with username: {}", userName);
        User user = userRepository.findByName(userName);
        if (user == null) {
            logger.warn("User not found with username: {}", userName);
        } else {
            logger.info("User found with username: {}", userName);
        }
        return user;
    }

    public boolean deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        try {
            userRepository.deleteById(userId);
            logger.info("User deleted with ID: {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", userId, e);
            return false;
        }
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Found {} users", users.size());
        return users;
    }
}
