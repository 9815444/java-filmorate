package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;
    private UserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping()
    public User create(@RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping()
    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping()
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userStorage.getUser(Integer.valueOf(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Map<String, String> pathVarsMap) {
        String id = pathVarsMap.get("id");
        String friendId = pathVarsMap.get("friendId");
        if ((id != null) && (friendId != null)) {
            return userService.addFriend(Integer.valueOf(id), Integer.valueOf(friendId));
        } else {
            return null;
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Map<String, String> pathVarsMap) {
        String id = pathVarsMap.get("id");
        String friendId = pathVarsMap.get("friendId");
        if ((id != null) && (friendId != null)) {
            return userService.removeFriend(Integer.valueOf(id), Integer.valueOf(friendId));
        } else {
            return null;
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable String id) {
        return userService.getUserFriends(Integer.valueOf(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Map<String, String> pathVarsMap) {
        String id = pathVarsMap.get("id");
        String otherId = pathVarsMap.get("otherId");
        if ((id != null) && (otherId != null)) {
            return userService.getCommonFriends(Integer.valueOf(id), Integer.valueOf(otherId));
        } else {
            return null;
        }
    }


}
