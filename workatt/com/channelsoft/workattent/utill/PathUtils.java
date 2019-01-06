package com.channelsoft.workattent.utill;

import java.io.File;
import java.net.URLDecoder;

public class PathUtils {

    /**
     * 获取 wrokatt.jar包路径
     *
     * @return
     */
    public static String getAbsolutePath() {

        try {
            String basePath = PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            basePath = URLDecoder.decode(basePath, "utf-8");
            if (basePath.endsWith(".jar")) {
                basePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
            }
            File f = new File(basePath);
            basePath = f.getAbsolutePath();
            return basePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
