package com.rora.phase.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;

import com.rora.phase.R;

import javax.annotation.Nullable;

public class Dialog implements Runnable {
    private final String title;
    private final String message;
    private final String positiveBtnTitle;
    private final String negativeBtnTitle;
    private final Activity activity;
    private final Runnable runOnPositiveDismiss;
    private final Runnable runOnNegativeDismiss;

    private AlertDialog alert;

    private static final ArrayList<Dialog> rundownDialogs = new ArrayList<>();

    private Dialog(Activity activity, String title, String message, @Nullable String positiveBtnTitle, @Nullable String negativeBtnTitle, Runnable runOnPositiveDismiss, Runnable runOnNegativeDismiss) {
        this.activity = activity;
        this.title = title;
        this.message = message;
        this.positiveBtnTitle = positiveBtnTitle;
        this.negativeBtnTitle = negativeBtnTitle;
        this.runOnPositiveDismiss = runOnPositiveDismiss;
        this.runOnNegativeDismiss = runOnNegativeDismiss;
    }

    public static void closeDialogs() {
        try {
            synchronized (rundownDialogs) {
                for (Dialog d : rundownDialogs) {
                    if (d.alert.isShowing()) {
                        d.alert.dismiss();
                    }
                }

                rundownDialogs.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void displayDialog(final Activity activity, String title, String message, @Nullable String positiveBtnTitle, @Nullable String negativeBtnTitle, Runnable runOnPositiveDismiss, @Nullable Runnable runOnNegativeDismiss, final boolean endAfterDismiss) {
        activity.runOnUiThread(new Dialog(activity, title, message, positiveBtnTitle, negativeBtnTitle, () -> {
            if (endAfterDismiss) {
                activity.finish();
            }
        }, () -> {
            if (endAfterDismiss) {
                activity.finish();
            }
        }));
    }

    public static void displayNotifyDialog(final Activity activity, String title, String message, @Nullable String positiveBtnTitle, Runnable runOnPositiveDismiss) {
        activity.runOnUiThread(new Dialog(activity, title, message, positiveBtnTitle, null, runOnPositiveDismiss, null));
    }

    public static void displayDialog(final Activity activity, String title, String message, final boolean endAfterDismiss) {
        activity.runOnUiThread(new Dialog(activity, title, message, null, null, () -> {
            if (endAfterDismiss) {
                activity.finish();
            }
        }, () -> {
            if (endAfterDismiss) {
                activity.finish();
            }
        }));
    }

    public static void displayDialog(Activity activity, String title, String message, @Nullable String positiveBtnTitle, @Nullable String negativeBtnTitle, Runnable runOnPositiveDismiss, @Nullable Runnable runOnNegativeDismiss) {
        activity.runOnUiThread(new Dialog(activity, title, message, positiveBtnTitle, negativeBtnTitle, runOnPositiveDismiss, runOnNegativeDismiss));
    }

    public static void displayDialog(Activity activity, String title, String message, Runnable runOnPositiveDismiss) {
        activity.runOnUiThread(new Dialog(activity, title, message, null, null, runOnPositiveDismiss, null));
    }

    @Override
    public void run() {
        // If we're dying, don't bother creating a dialog
        if (activity.isFinishing())
            return;

        alert = new AlertDialog.Builder(activity).create();

        alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(true);

        alert.setButton(AlertDialog.BUTTON_POSITIVE, positiveBtnTitle == null ? activity.getResources().getText(android.R.string.ok) : positiveBtnTitle, (dialog, which) -> {
            synchronized (rundownDialogs) {
                rundownDialogs.remove(Dialog.this);
                alert.dismiss();
            }

            if (runOnPositiveDismiss != null)
                runOnPositiveDismiss.run();
        });

        //alert.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getResources().getText(R.string.help), new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int which) {
        //        synchronized (rundownDialogs) {
        //            rundownDialogs.remove(Dialog.this);
        //            alert.dismiss();
        //        }
        //
        //        runOnPositiveDismiss.run();
        //
        //        HelpLauncher.launchTroubleshooting(activity);
        //    }
        //});

        if (negativeBtnTitle != null || runOnNegativeDismiss != null)
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, negativeBtnTitle == null ? activity.getResources().getText(android.R.string.cancel) : negativeBtnTitle, (dialog, which) -> {
                synchronized (rundownDialogs) {
                    rundownDialogs.remove(Dialog.this);
                    alert.dismiss();
                }

                if (runOnNegativeDismiss != null)
                    runOnNegativeDismiss.run();
            });

        alert.setOnShowListener(dialog -> {
            // Set focus to the OK button by default
            Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            button.requestFocus();
        });

        synchronized (rundownDialogs) {
            rundownDialogs.add(this);
            alert.show();
        }
    }

}
