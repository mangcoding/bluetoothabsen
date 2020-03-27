package com.nugraha.bluetoothabsen;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Message {
    private static Toast toast;
    public static void message(Context context, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.show();
    }
}