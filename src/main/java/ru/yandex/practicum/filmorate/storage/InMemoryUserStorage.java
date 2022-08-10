package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.Validator.checkUser;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;

    @Override
    public User create(User user) {
        checkUser(user);
        user.setId(++lastId);
        users.put(user.getId(), user);
        log.info("Added user {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFound();
        }
        checkUser(user);
        log.info("Update user. New data {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFound();
        }
        return user;
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        User user = getUser(id);
        user.addFriend(friendId);
        User friend = getUser(friendId);
        friend.addFriend(id);
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        User user = getUser(id);
        User friend = getUser(friendId); //проверим наличие друга по id
        user.removeFriend(friendId);
        friend.removeFriend(id);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        return getUser(id).getFriends().stream().map((s) -> getUser(s)).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> userFriends = getUser(id).getFriends();
        Set<Integer> friendFriends = getUser(otherId).getFriends();

        return userFriends.stream()
                .filter(friendFriends::contains)
                .map((p) ->getUser(p)).collect(Collectors.toList());
    }

}
