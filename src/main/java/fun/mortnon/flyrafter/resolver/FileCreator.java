package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件创建工具
 *
 * @author Moon Wu
 * @date 2021/4/25
 */
@Slf4j
public class FileCreator implements Constants {

    /**
     * 创建 SQL 文件
     *
     * @param sql
     * @param fileName
     * @param configuration
     * @return
     */
    public boolean createSQLFile(String sql, String fileName, FlyRafterConfiguration configuration) {
        String folder = configuration.getLocations().get(0);
        String backup = configuration.getBackup();

        if (StringUtils.isBlank(fileName)) {
            log.info("file name is empty,will not create file.");
            return false;
        }

        String sqlFolder = FlyRafterUtils.fullPath(folder);
        String backupFolder = FlyRafterUtils.fullPath(backup);
        if (StringUtils.isNotBlank(sqlFolder)) {
            return internalCreateFile(sql, fileName, sqlFolder, backupFolder);
        }
        log.error("resource folder {} is not exists.", folder);
        return false;
    }

    private boolean internalCreateFile(String content, String fileName, String folder, String backupFolder) {
        if (StringUtils.isBlank(folder)) {
            log.info("create sql file fail for empty flyway folder.");
            return false;
        }

        File sqlFile = new File(folder + File.separator + fileName);
        //如果同名文件存在，先删除
        if (sqlFile.exists()) {
            log.info("delete same name older sql file.");
            sqlFile.delete();
        }

        try {
            boolean createFileResult = sqlFile.createNewFile();
            if (createFileResult) {
                log.debug("create sql file {} success.", fileName);
                try (FileWriter fileWriter = new FileWriter(sqlFile)) {
                    log.debug("write sql into file {}", fileName);
                    fileWriter.write(content);
                }

                //复制文件到备份目录
                FileUtils.copyFileToDirectory(sqlFile, new File(backupFolder));
                return true;
            }
            log.info("create sql file {} fail.", fileName);
            return false;
        } catch (IOException e) {
            log.error("create sql file {} fail for {}", fileName, e);
            return false;
        }
    }
}
