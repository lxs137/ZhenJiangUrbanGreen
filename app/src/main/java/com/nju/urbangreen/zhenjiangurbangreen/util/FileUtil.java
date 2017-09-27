package com.nju.urbangreen.zhenjiangurbangreen.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nju.urbangreen.zhenjiangurbangreen.attachments.AttachmentRecord;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by lxs on 17-8-11.
 */

public class FileUtil {
    public static String getBaseMapTPKDir() {
//        return getAttachSaveDir() + "tpk" + File.separator;
        return getAppFileDir() + "tpk" + File.separator;
    }

    public static String getAttachSaveDir() {
        return getAppFileDir() + "attaches" + File.separator;
    }

    public static String getAppFileDir() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator
                + "nju_greenland" + File.separator;
    }

    public static void deleteFile(String path) {
        File deleteFile = new File(path);
        if (deleteFile.isDirectory()) {
            File[] files = deleteFile.listFiles();
            for (File file : files) {
                deleteFile(file.getPath());
                if(file.exists())
                    file.delete();
            }

        } else
        if(deleteFile.exists())
            deleteFile.delete();

    }

    public static String byte2SizeStr(float byteValue) {
        String str[] = {"B", "KB", "MB", "GB"};
        for(int i = 0, n = str.length; i < n; i++) {
            if(byteValue < 1024)
                return String.format("%.2f", byteValue) + str[i];
            else
                byteValue /= 1024;
        }
        return String.format("%.2f", byteValue * 1024) + str[str.length - 1];
    }

    public static Intent getFileViewIntent(String path) {
        String extension = getFileExtension(getFileName(path));
        Intent fileIntent = null;
        switch (extension) {
            case ".doc":
                fileIntent = getWordFileIntent(path);
                break;
            case ".xls":
                fileIntent = getExcelFileIntent(path);
                break;
            case ".ppt":
                fileIntent = getPptFileIntent(path);
                break;
            case ".pdf":
                fileIntent = getPdfFileIntent(path);
                break;
            case ".png":
            case ".jpg":
            case ".jpeg":
                fileIntent = getImageFileIntent(path);
                break;
            case ".avi":
            case ".mp4":
                fileIntent = getVideoFileIntent(path);
                break;
            case ".mp3":
                fileIntent =getAudioFileIntent(path);
                break;
            case ".txt":
                fileIntent = getTextFileIntent(path);
                break;
            default:
                break;
        }
        return fileIntent;
    }

    public static boolean isIntentAvailable(Intent intent) {
        Context context = MyApplication.getContext();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String getSizeStr(long length) {
        float size = length;
        String names[] = {"字节", "KB", "MB", "GB"};
        for(int i = 0, n = names.length; i < n; i++) {
            if(size < 1024) {
                return String.format(Locale.CHINA, "%.2f", size) + names[i];
            } else {
                size /= 1024.f;
            }
        }
        return String.format(Locale.CHINA, "%.2f", size * 1024.f) + names[names.length - 1];
    }

    public static String getPath(Uri uri) {

        Context context = MyApplication.getContext();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    private static Intent getPptFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // android获取一个用于打开Excel文件的intent
    private static Intent getExcelFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // android获取一个用于打开Word文件的intent
    private static Intent getWordFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // android获取一个用于打开文本文件的intent
    private static Intent getTextFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "text/plain");

        return intent;
    }

    // android获取一个用于打开PDF文件的intent
    private static Intent getPdfFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    // android获取一个用于打开图片文件的intent
    private static Intent getImageFileIntent(String path)

    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // android获取一个用于打开视频文件的intent
    private static Intent getVideoFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "video/*");
        return intent;

    }

    // android获取一个用于打开音频文件的intent
    private static Intent getAudioFileIntent(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "audio/*");
        return intent;

    }

}
