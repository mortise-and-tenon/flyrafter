package test.org.pack;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
@Entity
public class MyTable2 {
    @Id
    private Long id;

    @Column(columnDefinition = "varchar(20) not null")
    private String definitionString;

    @Column(length = 123)
    private String lengthString;
}
