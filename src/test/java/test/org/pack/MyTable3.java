package test.org.pack;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
@Entity(name = "Table3")
public class MyTable3 {
    @Id
    private Long id;
}
