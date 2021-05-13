package com.example.demo.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Moon Wu
 * @date 2021/5/8
 */
@MappedSuperclass

public class EntityParent {
    @Id
    private Long id;
}
