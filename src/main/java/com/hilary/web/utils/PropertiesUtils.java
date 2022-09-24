package com.hilary.web.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 14:49
 * @description:
 **/
@Slf4j
public class PropertiesUtils {


    /**
     * 获取properties对象
     */
    public static Properties getProperties(String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            log.error(fileName + "文件未找到");
        } catch (IOException e) {
            e.getStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
        return properties;
    }

    /**
     * 新增或修改数据
     * @param key
     * @param value
     * @param fileName
     */
    public static void setValue(String key, String value, String fileName) {

        FileOutputStream fileOutputStream = null;
        try {
            // 如果文件夹不存在就创建
            File file = new File(fileName);
            if (!file.exists()&& !file.isDirectory()) {
                file.createNewFile();
            }
            Properties properties = getProperties(fileName);
            properties.setProperty(key, value);
            fileOutputStream = new FileOutputStream("simbot-bots/" + fileName);
            properties.store(fileOutputStream, "编辑机器人成功 code:" + fileName.replace(".bot", ""));
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }
    public static void getFileContent(Object fileInPath) {
        BufferedReader br = null;
        try {
            if (fileInPath == null) {
                return ;
            }
            if (fileInPath instanceof String) {
                br = new BufferedReader(new FileReader(new File((String) fileInPath)));
            } else if (fileInPath instanceof InputStream) {
                br = new BufferedReader(new InputStreamReader((InputStream) fileInPath));
            }
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
