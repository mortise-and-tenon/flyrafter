package test.org.pack;

import fun.mortnon.flyrafter.annotation.Ignore;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Moon Wu
 * @date 2021/5/10
 */
@Entity
public class MyTable5 {
    @Id
    private Long id;

    @Ignore
    private String name;
}
