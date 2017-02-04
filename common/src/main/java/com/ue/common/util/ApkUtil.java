package com.ue.common.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by hawk on 2016/12/29.
 */

public class ApkUtil {
    public static long downloadApk(Context context, String title, String desc,String url,String apkName){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(desc);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        checkDirExists("Download");
        request.setDestinationInExternalPublicDir("Download", apkName);
        long downloadApkId = downloadManager.enqueue(request);
        return downloadApkId;
    }

    private static boolean checkDirExists(String folderName) {
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
}
