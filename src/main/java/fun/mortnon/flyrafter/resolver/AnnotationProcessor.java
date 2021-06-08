package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.annotation.Ignore;
import fun.mortnon.flyrafter.entity.DbColumn;
import fun.mortnon.flyrafter.entity.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 注解解析器
 *
 * @author Moon Wu
 * @date 2021/4/21
 */
@Slf4j
public class AnnotationProcessor implements Constants {
    private URLClassLoader specifyLoader;
    private List<String> includePackages;

    public AnnotationProcessor() {

    }

    public AnnotationProcessor(URLClassLoader classLoader) {
        this.specifyLoader = classLoader;
    }

    public AnnotationProcessor(URLClassLoader classLoader, List<String> includePackages) {
        this.specifyLoader = classLoader;
        this.includePackages = includePackages;
    }

    public List<DbTable> process() {
        log.info("start to process entity annotation.");

        List<Class<?>> classList = getAllClasses();
        List<Class<?>> annotationClassList = classList.stream()
                .filter(cls -> cls.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());

        return annotationClassList.stream().map(cls -> {
            DbTable table = new DbTable();
            //读取注解的表名
            String annotationName = cls.getAnnotation(Entity.class).name();
            if (StringUtils.isNotBlank(annotationName)) {
                table.setName(annotationName);
            } else {
                table.setName(cls.getSimpleName());
            }

            LinkedHashSet<DbColumn> columnSet = new LinkedHashSet<>();
            table.setColumnSet(columnSet);

            //处理自身的属性
            Field[] fields = cls.getDeclaredFields();
            parseColumn(fields, columnSet);

            //处理父类的属性
            Class<?> superclass = cls.getSuperclass();
            if (null != superclass) {
                MappedSuperclass annotation = superclass.getAnnotation(MappedSuperclass.class);
                if (null != annotation) {
                    Field[] parentFields = superclass.getDeclaredFields();
                    parseColumn(parentFields, columnSet);
                }
            }

            return table;
        }).collect(Collectors.toList());

    }

    private void parseColumn(Field[] fields, LinkedHashSet<DbColumn> columnSet) {
        //排除 static 的属性
        Arrays.stream(fields).filter(k -> !Modifier.isStatic(k.getModifiers()))
                .forEach(field -> {

                    //如果标记 @Ignore 注解，字段不解析为数据表字段
                    if (field.isAnnotationPresent(Ignore.class)) {
                        return;
                    }

                    DbColumn dbColumn = new DbColumn();
                    dbColumn.setName(field.getName());
                    dbColumn.setDefinition(EntityDefinition.DefaultTypeDefinition.getOrDefault(field.getType().getName(), ""));

                    //字符串属性长度
                    int length = STRING_DEFAULT_LENGTH;

                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        String definition = column.columnDefinition();
                        //如果有自定义描述，使用自定义描述
                        if (StringUtils.isNotBlank(definition)) {
                            dbColumn.setDefinition(definition);
                        } else if (field.getType().getName().equals(EntityDefinition.StringTypeName)) {
                            length = column.length();
                        }
                    }

                    dbColumn.setDefinition(String.format(dbColumn.getDefinition(), length));

                    //Id 注解，标注为主键
                    if (field.isAnnotationPresent(Id.class)) {
                        dbColumn.setPrimaryKey(true);
                    }

                    columnSet.add(dbColumn);
                });
    }


    /**
     * 获取所有的 Class
     *
     * @return
     */
    private List<Class<?>> getAllClasses() {
        List<Class<?>> classList = new ArrayList<>();

        List<URL> dirs = null;
        try {
            if (null != specifyLoader) {
                dirs = Arrays.asList(specifyLoader.getURLs());
            } else {
                Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("");
                dirs = Collections.list(urls);
            }
        } catch (IOException e) {
            log.error("get all class by classloader fail for ", e);
            return classList;
        }

        for (URL url : dirs) {
            String packageDirName = "";
            String packageName = "";

            String protocol = url.getProtocol();

            if (FILE_PROTOCOL.equalsIgnoreCase(protocol)) {
                String filePath = null;
                try {
                    filePath = URLDecoder.decode(url.getFile(), UTF8);
                } catch (UnsupportedEncodingException e) {
                    log.error("class url decode fail for ", e);
                }
                // 以文件的方式扫描整个包下的文件 并添加到集合中
                if (StringUtils.isNotBlank(filePath)) {
                    getClassByFolder(packageName, filePath, classList);
                }
            } else if (JAR_PROTOCOL.equalsIgnoreCase(protocol)) {
                JarFile jar;
                try {
                    jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (!name.endsWith(CLASS_SUFFIX)) {
                            continue;
                        }

                        String className = name.replace(URL_SEPARATOR, SPLIT_DOT).substring(0, name.length() - CLASS_SUFFIX.length());
                        try {
                            if (null != specifyLoader) {
                                log.debug("class:" + className);
                                if (null != includePackages
                                        && includePackages.stream().anyMatch(k -> className.startsWith(k))) {
                                    classList.add(specifyLoader.loadClass(className));
                                }
                            } else {
                                classList.add(Class.forName(className));
                            }
                        } catch (ClassNotFoundException e) {
                            log.debug("get class fail for {}", e);
                        }
                    }
                } catch (IOException e) {
                    log.debug("read jar file fail for {}", e);
                }
            }
        }

        return classList;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param classes
     */
    private void getClassByFolder(String packageName, String packagePath, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirFiles = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(CLASS_SUFFIX));

        for (File file : dirFiles) {

            if (file.isDirectory()) {
                String targetPackageName = file.getName();

                if (StringUtils.isNotBlank(packageName)) {
                    targetPackageName = packageName + "." + file.getName();
                }

                getClassByFolder(targetPackageName, file.getAbsolutePath(), classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - CLASS_SUFFIX.length());
                try {
                    // 添加到集合中去
                    String targetClassName = packageName + SPLIT_DOT + className;
                    if (null != specifyLoader) {
                        classes.add(specifyLoader.loadClass(targetClassName));
                    } else {
                        classes.add(Class.forName(targetClassName));
                    }
                } catch (ClassNotFoundException e) {
                    log.error("get class fail for ", e);
                }
            }
        }
    }
}
