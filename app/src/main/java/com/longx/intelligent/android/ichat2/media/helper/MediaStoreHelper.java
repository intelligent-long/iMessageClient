package com.longx.intelligent.android.ichat2.media.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.DirectoryInfo;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/1/7 at 6:46 PM.
 */
public class MediaStoreHelper {

    public static List<MediaInfo> getImages(Context context, int page, int pageSize) {
        return getDirectoryImages(context, null, page, pageSize);
    }

    public static List<MediaInfo> getAllImages(Context context) {
        return getAllDirectoryImages(context, null);
    }

    public static List<MediaInfo> getDirectoryImages(Context context, String directoryPath, int page, int pageSize) {
        return getMedias(context, directoryPath, MediaType.IMAGE, page, pageSize);
    }

    public static List<MediaInfo> getAllDirectoryImages(Context context, String directoryPath) {
        return getAllMedias(context, directoryPath, MediaType.IMAGE);
    }

    public static List<MediaInfo> getVideos(Context context, int page, int pageSize) {
        return getDirectoryVideos(context, null, page, pageSize);
    }

    public static List<MediaInfo> getAllVideos(Context context) {
        return getAllDirectoryVideos(context, null);
    }

    public static List<MediaInfo> getDirectoryVideos(Context context, String directoryPath, int page, int pageSize) {
        return getMedias(context, directoryPath, MediaType.VIDEO, page, pageSize);
    }

    public static List<MediaInfo> getAllDirectoryVideos(Context context, String directoryPath) {
        return getAllMedias(context, directoryPath, MediaType.VIDEO);
    }

    public static List<MediaInfo> getMedias(Context context, int page, int pageSize){
        return getDirectoryMedias(context, null, page, pageSize);
    }
    public static List<MediaInfo> getAllMedias(Context context){
        return getAllDirectoryMedias(context, null);
    }

    public static List<MediaInfo> getDirectoryMedias(Context context, String directoryPath, int page, int pageSize){
        return getMedias(context, directoryPath, null, page, pageSize);
    }

    public static List<MediaInfo> getAllDirectoryMedias(Context context, String directoryPath){
        return getAllMedias(context, directoryPath, null);
    }

    private static List<MediaInfo> getMedias(Context context, String directoryPath, MediaType mediaType, int page, int pageSize) {
        List<MediaInfo> mediaUris = new ArrayList<>();
        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Files.FileColumns.DATA);
        projection.add(MediaStore.Files.FileColumns.MEDIA_TYPE);
        projection.add(MediaStore.Files.FileColumns._ID);
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        String selection;
        String[] selectionArgs = null;
        if(directoryPath == null && mediaType == null) { //所有的
            projection.add(MediaStore.Images.Media.DATE_ADDED);
            projection.add(MediaStore.Video.Media.DATE_ADDED);
            projection.add(MediaStore.Images.Media.DATE_MODIFIED);
            projection.add(MediaStore.Video.Media.DATE_MODIFIED);
            projection.add(MediaStore.Images.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DURATION);
            projection.add(MediaStore.Images.Media.HEIGHT);
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
            projection.add(MediaStore.Video.Media.WIDTH);
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }else if(mediaType == null){ //指定路径，所有类型
            projection.add(MediaStore.Images.Media.DATE_ADDED);
            projection.add(MediaStore.Video.Media.DATE_ADDED);
            projection.add(MediaStore.Images.Media.DATE_MODIFIED);
            projection.add(MediaStore.Video.Media.DATE_MODIFIED);
            projection.add(MediaStore.Images.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DURATION);
            projection.add(MediaStore.Images.Media.HEIGHT);
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
            projection.add(MediaStore.Video.Media.WIDTH);
            selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    + ")"
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA + " REGEXP ?";
            selectionArgs = new String[] { directoryPath + "/[^/]*"};
        }else if(directoryPath == null){ //所有路径，指定类型
            int mediaType1 = -1;
            if(mediaType.equals(MediaType.IMAGE)){
                projection.add(MediaStore.Images.Media.DATE_ADDED);
                projection.add(MediaStore.Images.Media.DATE_MODIFIED);
                projection.add(MediaStore.Images.Media.DATE_TAKEN);
                projection.add(MediaStore.Images.Media.HEIGHT);
                projection.add(MediaStore.Images.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            }else if(mediaType.equals(MediaType.VIDEO)){
                projection.add(MediaStore.Video.Media.DATE_ADDED);
                projection.add(MediaStore.Video.Media.DATE_MODIFIED);
                projection.add(MediaStore.Video.Media.DATE_TAKEN);
                projection.add(MediaStore.Video.Media.DURATION);
                projection.add(MediaStore.Video.Media.HEIGHT);
                projection.add(MediaStore.Video.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            }
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + mediaType1;
        }else { //指定路径，指定类型
            int mediaType1 = -1;
            if(mediaType.equals(MediaType.IMAGE)){
                projection.add(MediaStore.Images.Media.DATE_ADDED);
                projection.add(MediaStore.Images.Media.DATE_MODIFIED);
                projection.add(MediaStore.Images.Media.DATE_TAKEN);
                projection.add(MediaStore.Images.Media.HEIGHT);
                projection.add(MediaStore.Images.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            }else if(mediaType.equals(MediaType.VIDEO)){
                projection.add(MediaStore.Video.Media.DATE_ADDED);
                projection.add(MediaStore.Video.Media.DATE_MODIFIED);
                projection.add(MediaStore.Video.Media.DATE_TAKEN);
                projection.add(MediaStore.Video.Media.DURATION);
                projection.add(MediaStore.Video.Media.HEIGHT);
                projection.add(MediaStore.Video.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            }
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + mediaType1
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA + " REGEXP ?";
            selectionArgs = new String[] { directoryPath + "/[^/]*"};
        }

        // 构建Bundle以请求分页
        Bundle queryArgs = new Bundle();
        queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, page * pageSize);
        queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder);

        ContentResolver contentResolver = context.getContentResolver();
        Uri queryUri = MediaStore.Files.getContentUri("external");

        try (Cursor cursor = contentResolver.query(queryUri, projection.toArray(new String[0]), queryArgs, null)) {
            if (cursor != null) {
                try {
                    int columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    int columnMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                    int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    int columnIndexImageDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                    int columnIndexVideoDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                    int columnIndexImageDateModified = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                    int columnIndexVideoDateModified = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
                    int columnIndexImagesDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                    int columnIndexVideoDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                    int columnIndexDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int columnIndexImageHeight = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                    int columnIndexImageWidth = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                    int columnIndexVideoHeight = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
                    int columnIndexVideoWidth = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
                    while (cursor.moveToNext()) {
                        long mediaId = cursor.getLong(columnIndexID);
                        int mediaType1 = cursor.getInt(columnMediaType);
                        long dateAdded;
                        long dateModified;
                        long dateTaken;
                        String filePath = cursor.getString(columnIndexData);
                        if (mediaType1 == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                            Uri contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                            dateAdded = cursor.getLong(columnIndexImageDateAdded);
                            dateTaken = cursor.getLong(columnIndexImagesDateTaken);
                            dateModified = cursor.getLong(columnIndexImageDateModified);
                            int imageWidth = cursor.getInt(columnIndexImageWidth);
                            int imageHeight = cursor.getInt(columnIndexImageHeight);
                            mediaUris.add(new MediaInfo(contentUri, filePath, MediaType.IMAGE, dateAdded, dateModified, dateTaken, -1, imageWidth, imageHeight, -1, -1));
                        } else if (mediaType1 == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                            Uri contentUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                            dateAdded = cursor.getLong(columnIndexVideoDateAdded);
                            dateTaken = cursor.getLong(columnIndexVideoDateTaken);
                            dateModified = cursor.getLong(columnIndexVideoDateModified);
                            long videoDuration = cursor.getLong(columnIndexDuration);
                            int videoWidth = cursor.getInt(columnIndexVideoWidth);
                            int videoHeight = cursor.getInt(columnIndexVideoHeight);
                            mediaUris.add(new MediaInfo(contentUri, filePath, MediaType.VIDEO, dateAdded, dateModified, dateTaken, videoDuration, -1, -1, videoWidth, videoHeight));
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        return mediaUris;
    }

    private static List<MediaInfo> getAllMedias(Context context, String directoryPath, MediaType mediaType) {
        List<MediaInfo> mediaInfos = new ArrayList<>();
        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Files.FileColumns.DATA);
        projection.add(MediaStore.Files.FileColumns.MEDIA_TYPE);
        projection.add(MediaStore.Files.FileColumns._ID);
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        String selection;
        String[] selectionArgs = null;
        if(directoryPath == null && mediaType == null) { //所有的
            projection.add(MediaStore.Images.Media.DATE_ADDED);
            projection.add(MediaStore.Video.Media.DATE_ADDED);
            projection.add(MediaStore.Images.Media.DATE_MODIFIED);
            projection.add(MediaStore.Video.Media.DATE_MODIFIED);
            projection.add(MediaStore.Images.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DURATION);
            projection.add(MediaStore.Images.Media.HEIGHT);
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
            projection.add(MediaStore.Video.Media.WIDTH);
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }else if(mediaType == null){ //指定路径，所有类型
            projection.add(MediaStore.Images.Media.DATE_ADDED);
            projection.add(MediaStore.Video.Media.DATE_ADDED);
            projection.add(MediaStore.Images.Media.DATE_MODIFIED);
            projection.add(MediaStore.Video.Media.DATE_MODIFIED);
            projection.add(MediaStore.Images.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DATE_TAKEN);
            projection.add(MediaStore.Video.Media.DURATION);
            projection.add(MediaStore.Images.Media.HEIGHT);
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Video.Media.HEIGHT);
            projection.add(MediaStore.Video.Media.WIDTH);
            selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    + ")"
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA + " REGEXP ?";
            selectionArgs = new String[] { directoryPath + "/[^/]*"};
        }else if(directoryPath == null){ //所有路径，指定类型
            int mediaType1 = -1;
            if(mediaType.equals(MediaType.IMAGE)){
                projection.add(MediaStore.Images.Media.DATE_ADDED);
                projection.add(MediaStore.Images.Media.DATE_MODIFIED);
                projection.add(MediaStore.Images.Media.DATE_TAKEN);
                projection.add(MediaStore.Images.Media.HEIGHT);
                projection.add(MediaStore.Images.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            }else if(mediaType.equals(MediaType.VIDEO)){
                projection.add(MediaStore.Video.Media.DATE_ADDED);
                projection.add(MediaStore.Video.Media.DATE_MODIFIED);
                projection.add(MediaStore.Video.Media.DATE_TAKEN);
                projection.add(MediaStore.Video.Media.DURATION);
                projection.add(MediaStore.Video.Media.HEIGHT);
                projection.add(MediaStore.Video.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            }
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + mediaType1;
        }else { //指定路径，指定类型
            int mediaType1 = -1;
            if(mediaType.equals(MediaType.IMAGE)){
                projection.add(MediaStore.Images.Media.DATE_ADDED);
                projection.add(MediaStore.Images.Media.DATE_MODIFIED);
                projection.add(MediaStore.Images.Media.DATE_TAKEN);
                projection.add(MediaStore.Images.Media.HEIGHT);
                projection.add(MediaStore.Images.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            }else if(mediaType.equals(MediaType.VIDEO)){
                projection.add(MediaStore.Video.Media.DATE_ADDED);
                projection.add(MediaStore.Video.Media.DATE_MODIFIED);
                projection.add(MediaStore.Video.Media.DATE_TAKEN);
                projection.add(MediaStore.Video.Media.DURATION);
                projection.add(MediaStore.Video.Media.HEIGHT);
                projection.add(MediaStore.Video.Media.WIDTH);
                mediaType1 = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            }
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + mediaType1
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA + " REGEXP ?";
            selectionArgs = new String[] { directoryPath + "/[^/]*"};
        }
        ContentResolver contentResolver = context.getContentResolver();
        Uri queryUri = MediaStore.Files.getContentUri("external");
        Cursor cursor = contentResolver.query(
                queryUri,
                projection.toArray(new String[0]),
                selection,
                selectionArgs,
                sortOrder
        );
        if (cursor != null) {
            try {
                int columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                int columnMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                int columnIndexImageDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int columnIndexVideoDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int columnIndexImageDateModified = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                int columnIndexVideoDateModified = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
                int columnIndexImagesDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int columnIndexVideoDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                int columnIndexDuration = -1;
                if (mediaType == null || mediaType.equals(MediaType.VIDEO)) {
                    columnIndexDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                }
                int columnIndexImageHeight = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int columnIndexImageWidth = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int columnIndexVideoHeight = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
                int columnIndexVideoWidth = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
                while (cursor.moveToNext()) {
                    long mediaId = cursor.getLong(columnIndexID);
                    int mediaType1 = cursor.getInt(columnMediaType);
                    long dateAdded;
                    long dateModified;
                    long dateTaken;
                    String filePath = cursor.getString(columnIndexData);
                    if (mediaType1 == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                        Uri contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                        dateAdded = cursor.getLong(columnIndexImageDateAdded);
                        dateTaken = cursor.getLong(columnIndexImagesDateTaken);
                        dateModified = cursor.getLong(columnIndexImageDateModified);
                        int imageWidth = cursor.getInt(columnIndexImageWidth);
                        int imageHeight = cursor.getInt(columnIndexImageHeight);
                        mediaInfos.add(new MediaInfo(contentUri, filePath, MediaType.IMAGE, dateAdded, dateModified, dateTaken, -1, imageWidth, imageHeight, -1, -1));
                    } else if (mediaType1 == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        Uri contentUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                        dateAdded = cursor.getLong(columnIndexVideoDateAdded);
                        dateTaken = cursor.getLong(columnIndexVideoDateTaken);
                        dateModified = cursor.getLong(columnIndexVideoDateModified);
                        long videoDuration = cursor.getLong(columnIndexDuration);
                        int videoWidth = cursor.getInt(columnIndexVideoWidth);
                        int videoHeight = cursor.getInt(columnIndexVideoHeight);
                        mediaInfos.add(new MediaInfo(contentUri, filePath, MediaType.VIDEO, dateAdded, dateModified, dateTaken, videoDuration, -1, -1, videoWidth, videoHeight));
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return mediaInfos;
    }

    public static List<DirectoryInfo> getAllMediaDirectories(Context context) {
        return getMediaDirectories(context, null);
    }

    public static List<DirectoryInfo> getAllImageDirectories(Context context) {
        return getMediaDirectories(context, MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
    }

    public static List<DirectoryInfo> getAllVideoDirectories(Context context) {
        return getMediaDirectories(context, MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
    }

    private static List<DirectoryInfo> getMediaDirectories(Context context, @Nullable String selection) {
        Map<String, DirectoryInfo> tempMap = new HashMap<>();
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DURATION,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.WIDTH,
        };

        if (selection == null) {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }

        Uri queryUri = MediaStore.Files.getContentUri("external");
        try (Cursor cursor = context.getContentResolver().query(
                queryUri,
                projection,
                selection,
                null,
                null)) {
            if (cursor != null) {
                int columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                int columnIndexMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int columnIndexImageDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int columnIndexVideoDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int columnIndexImageDateModified = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                int columnIndexVideoDateModified = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
                int columnIndexImagesDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int columnIndexVideoDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                int columnIndexDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int columnIndexImageHeight = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int columnIndexImageWidth = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int columnIndexVideoHeight = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
                int columnIndexVideoWidth = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
                while (cursor.moveToNext()) {
                    String currentFilePath = cursor.getString(columnIndexData);
                    int type = cursor.getInt(columnIndexMediaType);
                    long mediaId = cursor.getLong(columnIndexID);
                    long dateAdded;
                    long dateModified;
                    long dateTaken;
                    long videoDuration = -1;
                    int imageWidth = -1, imageHeight = -1, videoWidth = -1, videoHeight = -1;
                    MediaType currentMediaType = (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) ? MediaType.IMAGE : MediaType.VIDEO;
                    String directoryPath = currentFilePath.substring(0, currentFilePath.lastIndexOf("/"));
                    Uri currentFileUri;
                    if (currentMediaType.equals(MediaType.IMAGE)) {
                        currentFileUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                        dateAdded = cursor.getLong(columnIndexImageDateAdded);
                        dateTaken = cursor.getLong(columnIndexImagesDateTaken);
                        dateModified = cursor.getLong(columnIndexImageDateModified);
                        imageWidth = cursor.getInt(columnIndexImageWidth);
                        imageHeight = cursor.getInt(columnIndexImageHeight);
                    } else {
                        currentFileUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.toString(mediaId));
                        dateAdded = cursor.getLong(columnIndexVideoDateAdded);
                        dateTaken = cursor.getLong(columnIndexVideoDateTaken);
                        dateModified = cursor.getLong(columnIndexVideoDateModified);
                        videoDuration = cursor.getLong(columnIndexDuration);
                        videoWidth = cursor.getInt(columnIndexVideoWidth);
                        videoHeight = cursor.getInt(columnIndexVideoHeight);
                    }
                    DirectoryInfo directoryInfo = tempMap.getOrDefault(directoryPath, new DirectoryInfo(directoryPath, 0, Long.MAX_VALUE, Long.MIN_VALUE, new MediaInfo(currentFileUri, currentFilePath, currentMediaType, dateAdded, dateModified, dateTaken, videoDuration, imageWidth, imageHeight, videoWidth, videoHeight)));
                    int mediaCount = directoryInfo.getMediaCount() + 1;
                    long mediaEarliestAddedTime = Math.min(directoryInfo.getMediaEarliestAddedTime(), dateAdded);
                    long mediaLatestAddedTime = Math.max(directoryInfo.getMediaLatestAddedTime(), dateAdded);
                    MediaInfo coverMediaInfo = directoryInfo.getCoverMediaInfo();
                    if (directoryInfo.getCoverMediaInfo().getPath() == null || dateAdded > directoryInfo.getMediaEarliestAddedTime()) {
                        coverMediaInfo = new MediaInfo(currentFileUri, currentFilePath, currentMediaType, dateAdded, dateModified, dateTaken, videoDuration, imageWidth, imageHeight, videoWidth, videoHeight);
                    }
                    tempMap.put(directoryPath, new DirectoryInfo(directoryPath, mediaCount, mediaEarliestAddedTime, mediaLatestAddedTime, coverMediaInfo));
                }
            }
        }
        return new ArrayList<>(tempMap.values());
    }


    public static int getImagesCount(Context context){
        return getImagesCount(context, null, true);
    }

    public static int getImagesCount(Context context, String directoryPath, boolean includeSubdirectories) {
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = null;
        String[] selectionArgs = null;

        if (directoryPath != null) {
            if (includeSubdirectories) {
                // 包含子文件夹
                selection = MediaStore.Images.Media.DATA + " LIKE ?";
                selectionArgs = new String[]{directoryPath + "%"};
            } else {
                // 不包含子文件夹
                selection = MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DATA + " NOT LIKE ?";
                selectionArgs = new String[]{directoryPath + "/%", directoryPath + "/%/%"};
            }
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)) {
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getVideoCount(Context context) {
        return getVideoCount(context, null, true);
    }

    public static int getVideoCount(Context context, String directoryPath, boolean includeSubdirectories) {
        String[] projection = {MediaStore.Video.Media._ID};
        String selection = null;
        String[] selectionArgs = null;

        if (directoryPath != null) {
            if (includeSubdirectories) {
                // 包含子文件夹
                selection = MediaStore.Video.Media.DATA + " LIKE ?";
                selectionArgs = new String[]{directoryPath + "%"};
            } else {
                // 不包含子文件夹
                selection = MediaStore.Video.Media.DATA + " LIKE ? AND " + MediaStore.Video.Media.DATA + " NOT LIKE ?";
                selectionArgs = new String[]{directoryPath + "/%", directoryPath + "/%/%"};
            }
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)) {
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

}
