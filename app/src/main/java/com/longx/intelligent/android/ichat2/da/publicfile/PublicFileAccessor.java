package com.longx.intelligent.android.ichat2.da.publicfile;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatImage{
        public static String save(String filePath, String saveFileName) {
            String imageFilePath = DataPaths.PublicFile.getChatImageFilePath(saveFileName);
            if(FileAccessHelper.save(FileAccessHelper.streamOf(filePath), imageFilePath)){
                return imageFilePath;
            }else {
                return null;
            }
        }
    }
}
