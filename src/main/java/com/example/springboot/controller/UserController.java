package com.example.springboot.controller;


import com.example.springboot.modal.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/selectUserByName")
    @CrossOrigin
    public List<User> selectUserByName (@RequestParam String username) {
        return userService.selectUserByName(username);
    }

    @RequestMapping(value = "/selectUserAll")
    public List<User> selectUserAll () {
        return userService.selectUserAll();
    }
    
    @RequestMapping(value = "/dev")
    public String dev () {
        return "测试";
    }
}
