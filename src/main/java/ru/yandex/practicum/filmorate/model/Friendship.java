package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Friendship {
    private User user;
    private User friend;
    private boolean confirmed;
}
