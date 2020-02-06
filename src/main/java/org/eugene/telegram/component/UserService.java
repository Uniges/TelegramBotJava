package org.eugene.telegram.component;

import org.eugene.telegram.dao.Subscribe;
import org.eugene.telegram.dao.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Database service, CRUD functions
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void addSubscriber(Subscribe subscribe) {
        userRepository.save(subscribe);
    }

    @Transactional
    public void deleteSubscriber(Long chatId) {
        userRepository.deleteById(chatId);
    }

    @Transactional
    public List<Subscribe> getAllSubscribes() {
        return userRepository.findAll();
    }
}
