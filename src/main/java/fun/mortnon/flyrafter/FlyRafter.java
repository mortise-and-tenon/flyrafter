package fun.mortnon.flyrafter;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.resolver.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

@Slf4j
public class FlyRafter {
    private FlyRafterConfiguration configuration;
    private AnnotationProcessor annotationProcessor;
    private DataSource dataSource;
    private SQLConvertor convertor;

    private FileCreator fileCreator;

    FlyRafter(FlyRafterConfiguration configuration, DataSource dataSource) {
        this.configuration = configuration;
        this.annotationProcessor = new AnnotationProcessor();
        this.dataSource = dataSource;
        this.convertor = new BasicSQLConvertor(annotationProcessor, dataSource);
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
        try {
            File backupDirectory = new File(backupFolder);
            File sqlDirectory = new File(sqlFolder);

            FileUtils.copyDirectory(backupDirectory, sqlDirectory);
        } catch (IOException e) {
            log.warn("recovery backup fail for:", e);
        }
        log.info("recovery backup sql file finish.");
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
