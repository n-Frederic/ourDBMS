package Util.Func;

import Database.DatabaseManager;
import User.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class logUtil {

    private static final String LOG_FILE = "../TestData/UserManager/log.txt";

    public static void log(String command) {
        try {
            File file = new File(LOG_FILE);

            // 如果文件不存在，先创建
            if (!file.exists()) {
                file.createNewFile();
            }

            // 使用rw模式打开文件
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                // 移动到文件末尾
                raf.seek(raf.length());

                String user = UserManager.getCurrentUser().toString();

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String logLine = String.format("[%s] user: %s | command: %s%n", timestamp, user, command);

                raf.write(logLine.getBytes("UTF-8"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
