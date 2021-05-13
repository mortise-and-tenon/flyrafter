package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Moon Wu
 * @date 2021/5/8
 */
@Entity
@Data
public class Entity1 {
    @Id
    private Long id;

    private boolean bool1;

    private Boolean bool2;

    private BigDecimal bigDecimal;

    private BigInteger bigInteger;

    private byte byte1;

    private Byte byte2;

    private Date date;

    private double double1;

    private Double double2;

    private float float1;

    private Float float2;

    private int int1;

    private Integer int2;

    private long long1;

    private Long long2;

    private short short1;

    private Short short2;

    private String string1;

    @Column(length = 123)
    private String string2;
}
