package net.sjh.italker.common.app;

import android.os.SystemClock;

import java.io.File;

public class Application extends android.app.Application {

    private static Application instace;

    @Override
    public void onCreate() {
        super.onCreate();
        instace = this;
    }


    /**
     * 获取缓存文件夹地址
     *
     * @return 当前app的缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instace.getCacheDir();
    }


    public static File getPortraitTmpFile() {
        //得到头像目录的缓存地址
        File dir = new File(getCacheDirFile(), "portrait");
        //创建所有的对应的文件夹
        dir.mkdirs();

        //删除一些旧的缓存的文件
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        //返回一个当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.currentThreadTimeMillis() + ".jpg");
        return path;
    }
}
