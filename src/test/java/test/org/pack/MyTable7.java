package test.org.pack;

import fun.mortnon.flyrafter.annotation.Ignore;

import javax.persistence.Entity;

/**
 * @author Moon Wu
 * @date 2021/11/8
 */
@Entity
public class MyTable7 extends MyTable7Parent {
    private String sub1;

    @Ignore
    private String sub2;
}
