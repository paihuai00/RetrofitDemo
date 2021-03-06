package com.csx.retrofitdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import com.csx.retrofitdemo.BuildConfig;
import java.io.File;

/**
 * Date: 2019/12/27
 * create by cuishuxiang
 * description:
 */
public class InstallApkUtils {

    /**
     * @param apkFile ：  需要安装的apk
     * @return
     * @Description 安装apk
     */
    public static void installApk(Context context,File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+ ".fileprovider", apkFile);  //包名.fileprovider
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
