package com.example.springboot.controller;

import com.example.springboot.service.LogService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public void addLog(@Param(value = "status") Integer status) {
        logService.insert(status);
    }
}
