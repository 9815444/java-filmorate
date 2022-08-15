package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getAll();

    User getUser(Integer id);

    void addFriend(Integer id, Integer friendId);

    void removeFriend(Integer id, Integer friendId);

    List<User> getUserFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);

}
