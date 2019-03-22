package org.sunbird.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class ImportExportUtil {

    public static String getAttachmentFilePath(Context context, Uri uri) {

        InputStream is = null;
        FileOutputStream os = null;
        String fullPath = null;
        String name = null;

        try {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[] { MediaStore.MediaColumns.DISPLAY_NAME }, null, null, null);
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex);
            }
            fullPath = Environment.getExternalStorageDirectory() + "/" + name;
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(fullPath);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (fullPath != null) {
                File f = new File(fullPath);
                f.delete();
            }
        }

        return fullPath;
    }
}

