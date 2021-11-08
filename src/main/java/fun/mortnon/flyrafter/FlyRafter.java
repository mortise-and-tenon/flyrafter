package fun.mortnon.flyrafter;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.resolver.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

@Slf4j
public class FlyRafter {
    private FlyRafterConfiguration configuration;
    private AnnotationProcessor annotationProcessor;
    private DataSource dataSource;
    private SQLConvertor convertor;

    private FileCreator fileCreator;

    FlyRafter(FlyRafterConfiguration configuration, DataSource dataSource) {
        this(configuration, dataSource, null);
    }

    FlyRafter(FlyRafterConfiguration configuration, DataSource dataSource, URLClassLoader classLoader) {
        this(configuration, dataSource, classLoader, null);
    }

    FlyRafter(FlyRafterConfiguration configuration, DataSource dataSource, URLClassLoader classLoader, List<String> includePackages) {
        this.configuration = configuration;
        this.annotationProcessor = new AnnotationProcessor(classLoader, includePackages);
        FlyRafterUtils.setClassLoader(classLoader);
        this.dataSource = dataSource;
        this.convertor = new BasicSQLConvertor(annotationProcessor, dataSource, configuration);
        this.fileCreator = new FileCreator();
    }

    /**
     * 启动 sql 生成
     */
    public void startup() {
        recoveryBackup();
        generateFile();
    }


    private void recoveryBackup() {
        log.info("recovery backup sql file.");
        String folder = configuration.getLocations().get(0);
        String backup = configuration.getBackup();
        log.info("backup folder:{}", backup);
        String sqlFolder = FlyRafterUtils.fullPath(folder);
        String backupFolder = FlyRafterUtils.fullPath(backup);
        if (StringUtils.isBlank(sqlFolder) || StringUtils.isBlank(backupFolder)) {
            log.info("stop recover sql from [{}] to [{}].", backupFolder, sqlFolder);
        }
        //指定了备份文件路径，才进行备份恢复
        if (StringUtils.isNotEmpty(backupFolder)) {
            try {
                File backupDirectory = new File(backupFolder);
                File sqlDirectory = new File(sqlFolder);

                FileUtils.copyDirectory(backupDirectory, sqlDirectory);
            } catch (IOException e) {
                log.warn("recovery backup fail for:", e);
            }
            log.info("recovery backup sql file finish.");
        }
    }

    /**
     * 生成 sql 文件
     */
    private void generateFile() {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(convertor.convert());
        String fileName = new FlyRafterUtils(dataSource, configuration).generateFileName(sqlBuffer.toString());
        fileCreator.createSQLFile(sqlBuffer.toString(), fileName, configuration);
    }
}
