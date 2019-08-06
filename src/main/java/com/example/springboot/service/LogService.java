package com.example.springboot.service;

import com.example.springboot.modal.Log;

public interface LogService {
    void insert(Integer status);

    String getLog(Integer status);
}
