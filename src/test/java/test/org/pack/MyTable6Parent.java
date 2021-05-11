package test.org.pack;

import fun.mortnon.flyrafter.annotation.Ignore;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Moon Wu
 * @date 2021/5/10
 */
@MappedSuperclass
public class MyTable6Parent {
    @Id
    private Long id;

    @Ignore
    private String parent;
}
