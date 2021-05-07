package org.mt.flyrafter.resolver;

import java.util.*;

/**
 * 用于实体转换的定义
 *
 * @author Moon Wu
 * @date 2021/4/23
 */
public class EntityDefinition {
    /**
     * 实体属性类型与列定义对照
     */
    public static final Map<String, String> DefaultTypeDefinition = new HashMap<>();

    /**
     * 整形类型名称
     */
    public static final Set<String> IntTypeName;

    /**
     * 双精度浮点数据类型名称
     */
    public static final Set<String> DoubleTypeName;

    /**
     * 单精度浮点数据类型名称
     */
    public static final Set<String> FloatTypeName;

    /**
     * 长整形数据类型名称
     */
    public static final Set<String> LongTypeName;

    /**
     * 短整形数据类型名称
     */
    public static final Set<String> ShortTypeName;

    /**
     * 字节数据类型名称
     */
    public static final Set<String> ByteTypeName;

    /**
     * 布尔数据类型名称
     */
    public static final Set<String> BooleanTypeName;

    /**
     * 字符数据类型名称
     */
    public static final Set<String> CharTypeName;

    /**
     * 字符串数据类型名称
     */
    public static final String StringTypeName;

    static {

        IntTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Integer");
            }

            {
                add("int");
            }
        });

        DoubleTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Double");
            }

            {
                add("double");
            }
        });

        FloatTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Float");
            }

            {
                add("float");
            }
        });

        LongTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Long");
            }

            {
                add("long");
            }
        });

        ShortTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Short");
            }

            {
                add("short");
            }
        });

        ByteTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Byte");
            }

            {
                add("byte");
            }
        });

        BooleanTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Boolean");
            }

            {
                add("boolean");
            }
        });

        CharTypeName = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("java.lang.Character");
            }

            {
                add("char");
            }
        });

        StringTypeName = "java.lang.String";

        BooleanTypeName.forEach(k -> DefaultTypeDefinition.put(k, "BIT"));
        DefaultTypeDefinition.put("java.math.BigDecimal", "DECIMAL(19,2)");
        DefaultTypeDefinition.put("java.math.BigInteger", "DECIMAL(19,2)");
        ByteTypeName.forEach(k -> DefaultTypeDefinition.put(k, "TINYINT"));
        DefaultTypeDefinition.put("java.util.Date", "DATETIME(6)");
        DoubleTypeName.forEach(k -> DefaultTypeDefinition.put(k, "DOUBLE"));
        FloatTypeName.forEach(k -> DefaultTypeDefinition.put(k, "FLOAT"));
        IntTypeName.forEach(k -> DefaultTypeDefinition.put(k, "INTEGER"));
        LongTypeName.forEach(k -> DefaultTypeDefinition.put(k, "BIGINT(20)"));
        ShortTypeName.forEach(k -> DefaultTypeDefinition.put(k, "SMALLINT"));
        DefaultTypeDefinition.put(StringTypeName, "VARCHAR(%d)");
    }
}
