package tk.amorim.siewebapp.util;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import tk.amorim.siewebapp.R;

/**
 * Created by lucas on 09/07/2017.
 */

public class DialogHelper {
    public static void showDialog(Activity context, String msg, String title, boolean isErrDialog) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
        dlgAlert.setMessage(msg);
        if (!title.equals(""))
            dlgAlert.setTitle(title);
        if (isErrDialog)
            dlgAlert.setIcon(R.drawable.ic_warning);
        else
            dlgAlert.setIcon(R.drawable.ic_info);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dlgAlert.create().show();
    }
}
