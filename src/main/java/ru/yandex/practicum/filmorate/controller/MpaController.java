package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class MpaController {

    private MpaStorage mpaStorage;

    @Autowired
    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping(value = "/mpa")
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }

    @GetMapping(value = "/mpa/{id}")
    public Mpa getMpa(@PathVariable String id) {
        return mpaStorage.getMpa(Integer.valueOf(id));
    }
}
