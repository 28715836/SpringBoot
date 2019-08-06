package com.example.springboot.service.Impl;

import com.example.springboot.mapper.LogMapper;
import com.example.springboot.modal.Log;
import com.example.springboot.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;

    @Override
    public void insert(Integer status) {
        Log log = new Log();
        log.setCreateTime(new Date());
        log.setStatus(status);
        logMapper.insert(log);
    }

    @Override
    public String getLog(Integer status) {
       return logMapper.weStartTime();
    }
}
