package com.fast0n.xdalabsconsole;


import android.content.Context;

import android.view.ViewGroup;



import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {

    public static void configSnackbar(Context context, Snackbar snack) {

        setRoundBordersBg(context, snack);

    }


    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar));
    }
}