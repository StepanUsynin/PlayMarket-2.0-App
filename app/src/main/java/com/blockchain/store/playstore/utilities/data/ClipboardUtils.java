package com.blockchain.store.playstore.utilities.data;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by samsheff on 21/09/2017.
 */

public class ClipboardUtils {

    public static void copyToClipboard(Context context, String data) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data", data);
        clipboard.setPrimaryClip(clip);
    }
}
