package com.example.springboot.modal;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "logs")
public class Log {

    @Id
    private int id;

    private Integer status;

    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
