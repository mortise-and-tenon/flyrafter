package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.entity.DbTable;
import fun.mortnon.flyrafter.enums.ActionEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Moon Wu
 * @date 2021/4/21
 */
class AnnotationProcessorTest {
    static List<DbTable> process;

    DbTable getTable(String name){
        return process.stream().filter(k->k.getName().equals(name)).findAny().orElse(null);
    }

    @BeforeAll
    static void init(){
        AnnotationProcessor annotationProcessor = new AnnotationProcessor();
        process = annotationProcessor.process();
    }

    /**
     * 测试所有类型都能转换
     */
    @Test
    void processTest1() {
        DbTable myTable1 = getTable("MyTable1");
        assertNotNull(myTable1);

        assertEquals(19, myTable1.getColumnSet().size());
        assertTrue(myTable1.getColumnSet().stream().allMatch(k -> StringUtils.isNotBlank(k.getDefinition())));
        assertTrue(myTable1.getColumnSet().stream().filter(k -> k.getName().equals("id")).allMatch(j -> j.getPrimaryKey()));
    }

    /**
     * 测试能自定义字段描述及自定义字符串长度
     */
    @Test
    void processTest2(){
        DbTable myTable2 = getTable("MyTable2");
        assertNotNull(myTable2);
        assertEquals(3,myTable2.getColumnSet().size());
        assertTrue(myTable2.getColumnSet().stream().anyMatch(k->k.getDefinition().contains("not null")));
        assertTrue(myTable2.getColumnSet().stream().anyMatch(k->k.getDefinition().contains("123")));
    }

    /**
     * 测试表名自定义
     */
    @Test
    void processTest3(){
        DbTable myTable3 = getTable("MyTable3");
        assertNull(myTable3);
        DbTable table3 = getTable("Table3");
        assertNotNull(table3);
    }

    /**
     * 测试获取实体的父类属性
     */
    @Test
    void processTest4(){
        DbTable myTable4 = getTable("MyTable4");
        assertEquals(2,myTable4.getColumnSet().size());
        assertTrue(myTable4.getColumnSet().stream().anyMatch(k->k.getName().equals("parentId")));
    }

    /**
     * 测试默认的操作类型为 ADD
     */
    @Test
    void processTest5(){
        DbTable myTable1 = getTable("MyTable1");
        assertNotNull(myTable1);
        assertNotNull(myTable1.getColumnSet());
        assertTrue(myTable1.getColumnSet().size() > 0);
        assertEquals(ActionEnum.ADD,myTable1.getAction());
        assertEquals(ActionEnum.ADD,myTable1.getColumnSet().iterator().next().getAction());
    }

    @Test
    void processTest6(){
        DbTable myTable6 = getTable("MyTable6");
        assertEquals(2,myTable6.getColumnSet().size());
    }

    @Test
    void processTest7(){
        DbTable myTable5 = getTable("MyTable5");
        assertEquals(1,myTable5.getColumnSet().size());
    }
}