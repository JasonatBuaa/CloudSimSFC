package org.cloudbus.cloudsim.util;

import java.io.*;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName JsonUtils.java
 * @Description TODO
 * @createTime 2021-07-24 15:43
 */
public class JsonUtils {
    public static String jsonRead(String path) {
        String jsonStr = "";
        try {
            File jsonFile = new File(path);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();

            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void jsonWrite(String path, String jsonStr) {
        try {
            File jsonFile = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile));
            writer.write(jsonStr);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
