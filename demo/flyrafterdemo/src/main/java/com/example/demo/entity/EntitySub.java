package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Moon Wu
 * @date 2021/5/8
 */
@Entity
@Data
public class EntitySub extends EntityParent{

    @Column(length = 123)
    private String str;

    @Column(columnDefinition = "varchar(321) not null")
    private String name;
}
