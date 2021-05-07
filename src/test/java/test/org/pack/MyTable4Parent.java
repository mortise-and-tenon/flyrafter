package test.org.pack;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
@MappedSuperclass
public class MyTable4Parent {
    @Id
    private Long parentId;
}
