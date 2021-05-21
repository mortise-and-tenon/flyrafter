package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static fun.mortnon.flyrafter.resolver.Constants.*;

/**
 * 工具
 *
 * @author Moon Wu
 * @date 2021/4/26
 */
@Slf4j
public class FlyRafterUtils {
    private FlyRafterConfiguration configuration;
    private DataSource dataSource;
    private static ClassLoader specifyClassLoader;

    public FlyRafterUtils(DataSource dataSource, FlyRafterConfiguration configuration) {
        this.configuration = configuration;
        this.dataSource = dataSource;
    }

    public FlyRafterUtils(DataSource dataSource, FlyRafterConfiguration configuration, ClassLoader classLoader) {
        this(dataSource, configuration);
        setClassLoader(classLoader);
    }

    public static void setClassLoader(ClassLoader classLoader) {
        specifyClassLoader = classLoader;
    }

    /**
     * 计算文本的 hash 值
     *
     * @param origin
     * @return
     */
    public static String md5(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance(Constants.MD5);
            md.update(origin.getBytes(Constants.UTF8));
            BigInteger bi = new BigInteger(1, md.digest());

            return bi.toString(16);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取当前jar所在的目录
     *
     * @return
     */
    public static String currentLocation() {
        String path = "";
        try {
            URL location = FlyRafterUtils.class.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(location.getPath());
            //如果是目录,指向的是包所在路径，而不是文件所在路径
            //如果是文件,这个文件指定的是jar所在的路径(注意如果是作为依赖包，这个路径是jvm启动加载的jar文件名)
            if (file.isDirectory()) {
                path = file.getAbsolutePath();
            } else {
                path = file.getParent();//返回jar所在的父路径
            }
        } catch (Exception e) {
            log.info("get application path fail:", e);
        }
        return path;
    }

    /**
     * 获取指定的目录全路径
     *
     * @param folder
     * @return
     */
    public static String fullPath(String folder) {
        if (folder.startsWith(CLASSPATH)) {
            folder = folder.substring(CLASSPATH.length());
            return classFolder(folder);
        }

        return systemFolder(folder);

    }

    public static String sourcePath(String folder) {
        if (folder.startsWith(CLASSPATH)) {
            folder = folder.substring(CLASSPATH.length());
            String path = classFolder(folder);
            path = path.substring(0,path.length() - folder.length());
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                String parent = file.getParent();
                if (parent.endsWith(TARGET_PATH)) {
                    File parentFolder = new File(parent);
                    return parentFolder.getParent() + File.separator + "src/main/resources" + File.separator + folder;
                }
            }
        }
        return "";
    }

    /**
     * 从 class 目录中查找配置的 sql 目录完整路径
     *
     * @param folder
     * @return
     */
    private static String classFolder(String folder) {
        Enumeration<URL> urlEnumeration = null;
        try {
            if (null != specifyClassLoader) {
                urlEnumeration = specifyClassLoader.getResources("");
            } else {
                urlEnumeration = Thread.currentThread().getContextClassLoader().getResources("");
            }
        } catch (IOException e) {
            log.info("");
            return "";
        }
        if (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            log.debug("folder url protocol is {}.", url.getProtocol());
            if (FILE_PROTOCOL.equals(url.getProtocol())) {
                String targetFolderUrl = url.getFile();
                File targetFolder = new File(targetFolderUrl + folder);
                if (targetFolder.exists()) {
                    return targetFolder.getPath();
                } else {
                    log.info("create directory: {}", targetFolder.getPath());
                    targetFolder.mkdirs();
                    return targetFolder.getPath();
                }
            }
        }

        log.debug("query none folder in classpath.");

        return "";
    }

    /**
     * 从系统目录查找配置的 sql 目录
     *
     * @param folder
     * @return
     */
    private static String systemFolder(String folder) {
        if (folder.startsWith(FILE_SYSTEM)) {
            folder = folder.replaceFirst(FILE_SYSTEM, FILE_PROTOCOL);
        }

        try {
            URL url = new URL(folder);
            String targetFolderUrl = url.getFile();
            File targetFolder = new File(targetFolderUrl);
            if (targetFolder.exists()) {
                return targetFolder.getPath();
            } else {
                log.info("create directory:{}", targetFolderUrl);
                targetFolder.mkdirs();
                return targetFolder.getPath();
            }
        } catch (MalformedURLException e) {
            log.info("fatal folder value {} for {}.", folder, e.getMessage());
        }

        return "";
    }

    /**
     * 生成 sql 文件名
     * 如果有版本 sql，版本自动加1
     *
     * @param sql
     * @return
     */
    public String generateFileName(String sql) {
        String folder = configuration.getLocations().get(0);

        String sqlFolder = fullPath(folder);
        String version = configuration.getVersionPattern();

        String hash = md5(sql);

        //如果查找到有资源文件夹，检测已有版本文件名称，获取版本号
        if (StringUtils.isNotBlank(sqlFolder)) {
            File targetFolder = new File(sqlFolder);

            if (targetFolder.exists() && targetFolder.isDirectory()) {
                //先从记录表中读取版本文件名
                String historyTableFileName = historyFileName();
                //版本表中存在 sql 记录，从表中读取前一版本文件名
                //版本表中不存在 sql 记录，从文件夹中获取前一版本文件名
                if (StringUtils.isNotBlank(historyTableFileName)) {
                    //文件 hash 一致，不生成新文件
                    if (historyTableFileName.contains(hash)) {
                        log.info("exists same content file in table before 1 history file.");
                        return "";
                    }

                    String[] nameArray = historyTableFileName.split(configuration.getSeparator());
                    if (nameArray.length > 0) {
                        version = nameArray[0].replace(configuration.getPrefix(), "");
                    }
                } else {
                    //存在版本文件
                    if (targetFolder.listFiles().length != 0) {
                        version = Arrays.stream(targetFolder.listFiles()).filter(file -> file.getName().startsWith("V"))
                                .map(v -> {
                                    String[] nameArray = v.getName().split(configuration.getSeparator());
                                    if (nameArray.length > 0) {
                                        return nameArray[0].replace(configuration.getPrefix(), "");
                                    }
                                    return "1";
                                }).max((v1, v2) -> new VersionComparator().compare(v1, v2)).get();
                    }

                    String historyFileName = String.format(configuration.getFileTemplate(), version, hash);

                    File preSameFile = Arrays.stream(targetFolder.listFiles())
                            .filter(file -> file.getName().equalsIgnoreCase(historyFileName))
                            .findAny().orElse(null);

                    //如果前一版本与当前内容一致，不用生成新文件，返回文件名空
                    if (null != preSameFile) {
                        log.info("exists same content file in before 1 history file.");
                        return "";
                    }
                }

                String newVersion = newVersion(version);
                log.info("flyrafter generate version {},hash {} sql file.", newVersion, hash);
                return String.format(configuration.getFileTemplate(), newVersion, hash);
            }
        }

        return "";
    }

    /**
     * 获取最新的 flyway 迁移 sql 文件名
     *
     * @return
     */
    private String historyFileName() {
        String historyTableName = configuration.getTableName();
        if (StringUtils.isBlank(historyTableName)) {
            return "";
        }
        try {
            if (null == dataSource) {
                return "";
            }
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.createStatement()
                    .executeQuery(String.format(SELECT_FLYWAY_TABLE, historyTableName));
            while (resultSet.next()) {
                return resultSet.getString(FLYWAY_SCRIPT_COLUMN);
            }
        } catch (SQLException e) {
            log.info("query history file name from flyway table empty.");
        }
        return "";
    }

    private String newVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return "";
        }

        String[] array;

        if (version.contains(SPLIT_DOT_STR)) {
            array = version.split(REGEX_SPLIT_DOT);
        } else {
            array = new String[]{version};
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (array.length == i + 1) {
                stringBuilder.append(Integer.parseInt(array[i]) + 1);
            } else {
                stringBuilder.append(array[i]);
            }

            stringBuilder.append(SPLIT_DOT_STR);
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1).toString();
    }

    /**
     * 将驼峰名称转换为下划线形式
     *
     * @param name
     * @return
     */
    public static String convertToUnderscore(String name) {
        StringBuffer strBuffer = new StringBuffer();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && strBuffer.length() > 0) {
                strBuffer.append(UNDERSCORE);
            }

            strBuffer.append(strBuffer.length() > 0 ? Character.toLowerCase(c) : c);
        }

        return strBuffer.toString();
    }

    /**
     * 将下划线形式转换为驼峰
     *
     * @param name
     * @return
     */
    public static String convertToCamelcase(String name) {
        StringBuffer strBuffer = new StringBuffer();
        boolean hasUnderline = false;
        for (char c : name.toCharArray()) {
            if (UNDERSCORE.equals(c)) {
                if (strBuffer.length() == 0) {
                    strBuffer.append(c);
                    continue;
                }

                if (hasUnderline) {
                    strBuffer.append(c);
                }

                hasUnderline = true;
                continue;
            }

            //如果前一个是下划线，当前又是小写，转为大写
            if (hasUnderline && Character.isLowerCase(c)) {
                hasUnderline = false;
                strBuffer.append(Character.toUpperCase(c));
            } else {
                strBuffer.append(c);
            }
        }

        return strBuffer.toString();
    }

    /**
     * 比较两个名称在驼峰、下划线格式两种格式混合下，是否一致
     *
     * @param nameOne
     * @param nameTwo
     * @return
     */
    public static boolean nameEquals(String nameOne, String nameTwo) {
        if (nameOne.equals(nameTwo)) {
            return true;
        }

        Set<String> setOne = new HashSet<>();
        Set<String> setTwo = new HashSet<>();

        setOne.add(nameOne);
        setOne.add(convertToUnderscore(nameOne));
        setOne.add(convertToCamelcase(nameOne));

        setTwo.add(nameTwo);
        setTwo.add(convertToUnderscore(nameTwo));
        setTwo.add(convertToCamelcase(nameTwo));

        setOne.addAll(setTwo);

        return setOne.size() < 6;
    }

    /**
     * SQL 文件版本比较
     */
    static class VersionComparator implements Comparator<String> {
        /**
         * 版本号支持以下形式：
         * 1
         * 001
         * 20210427010203
         * 1.2.3.4
         * <p>
         * 比较规则按以下进行：
         * 无 . 作为分隔符的，按数字大小比较
         * 有 . 作为分隔符的，如果段数不一致，末尾补为0，保证段数一致，再依次比较
         * 从左到右，数字大的版本更大，相同则比较下一段数字，直到比较出结果
         *
         * @param v1 比较的第一个版本
         * @param v2 比较第二个版本
         * @return -1：版本一更小；0：版本一样；1：版本一更大
         */
        @Override
        public int compare(String v1, String v2) {
            String[] v1Array, v2Array;

            if (v1.contains(SPLIT_DOT_STR)) {
                v1Array = v1.split(REGEX_SPLIT_DOT);
            } else {
                v1Array = new String[]{v1};
            }

            if (v2.contains(SPLIT_DOT_STR)) {
                v2Array = v2.split(REGEX_SPLIT_DOT);
            } else {
                v2Array = new String[]{v2};
            }

            int length = v1Array.length > v2Array.length ? v1Array.length : v2Array.length;

            for (int i = 0; i < length; i++) {
                long v1Segment = getSegment(i, v1Array);
                long v2Segment = getSegment(i, v2Array);

                if (v1Segment < v2Segment) {
                    return -1;
                } else if (v1Segment > v2Segment) {
                    return 1;
                }
            }

            return 0;
        }

        private long getSegment(int index, String[] array) {
            //如果数组为空或当前段无数据，返回默认0
            if (array.length == 0 || array.length < index + 1) {
                return 0;
            }

            return Long.parseLong(array[index]);
        }
    }
}
