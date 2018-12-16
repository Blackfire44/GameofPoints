package com.example.fabian.gameofpoints;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.AdapterView;

public class CustomDialog {

    public CustomDialog(Context context, String title, String text){
        setupDialog(context, title, text);
    }

    private void setupDialog(Context context, String title, String text){
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setTitle(title)
                .setMessage(text)
                .create();
        dialog.show();
    }
}
/*.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int i) {
        dialog.cancel();
        }
        })*/
//dialog.setCanceledOnTouchOutside(false);