package com.example.springboot.mapper;

import com.example.springboot.modal.Log;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface LogMapper extends BaseMapper<Log> {

    @Select("select create_time from logs where status = 0 order by create_time desc limit 1")
    String weStartTime();
}
