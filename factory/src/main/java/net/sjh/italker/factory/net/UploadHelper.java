package net.sjh.italker.factory.net;


import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.sjh.italker.factory.Factory;
import net.sjh.italker.utils.HashUtil;

import java.io.File;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * 上传工具类，用于上传任意文件到阿里OSS
 */
public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();


    public static final String ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    // 上传的仓库名
    private static final String BUCKET_NAME = "italker-new";

    private static OSS getClient() {
        OSSCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider("LTAIYQD07p05pHQW", "2txxzT8JXiHKEdEjylumFy6sXcDQ0G");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传最终方法，成功返回一个路径
     *
     * @param objKey 上传上去后，在服务器上的独立的key
     * @param path   需要上传的文件的路径
     * @return 返回一个存储地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);

        try {

            //初始化上传的client
            OSS client = getClient();
            //开始同步上传
            PutObjectResult putObjectResult = client.putObject(request);
            //得到一个外网可访问地址
            String url = client.presignPublicObjectURL(BUCKET_NAME,objKey);
            Log.d(TAG,String.format("PublicObjectUrl:%s",url));
            return url;
        } catch (Exception e) {
            // 本地异常如网络异常等
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path 本地地址
     * @return 服务器地址
     * */
    public static String uploadImage(String path){
        String key = getImageObjkey(path);
        return upload(key,path);
    }

    public static String uploadPortrait(String path){
        String key = getPortraitObjkey(path);
        return upload(key, path);
    }

    public static String uploadAudio(String path){
        String key = getAudioObjkey(path);
        return upload(key, path);
    }

    private static String getImageObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getPortraitObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getAudioObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }

    /**
     * 分月存储，避免一个文件夹太多
     *
     * @return yyyyMM
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

}
