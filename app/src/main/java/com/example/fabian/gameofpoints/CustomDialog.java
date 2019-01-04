package com.example.fabian.gameofpoints;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.AdapterView;

public class CustomDialog {

    public CustomDialog(Context context, String title, String text){ //Konstruktor
        setupDialog(context, title, text);
    }

    private void setupDialog(Context context, String title, String text){ //Es wird ein Dialogfenster gezeigt
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setTitle(title)
                .setMessage(text)
                .create(); //Ein Dialog wird kreiert und Titel und die Mitteilung werden mitgegeben
        dialog.show(); //Der Dialog wird angezeigt
    }
}