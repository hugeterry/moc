package com.qkmoc.moc.util;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/30 08:27
 */
public class CopyUtil {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copyToClipBoard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("moc_copy", text));
    }

}
