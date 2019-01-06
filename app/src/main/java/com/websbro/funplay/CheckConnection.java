package com.websbro.funplay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Toast;

import com.websbro.funplay.Activities.HomeActivity;
import com.websbro.funplay.Fragments.HomeFragment;

import java.util.Timer;
import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    AlertDialog dialogBox;

    private Context context;
    public CheckConnection(Context context){
        this.context = context;
    }
    public void run() {
        if(C.isConnected(context)){
            System.out.println("connected");
            if(dialogBox!=null){
                dialogBox.dismiss();
                dialogBox=null;
            }

        }else {
            //DISCONNECTED
                Handler handler = new Handler(Looper.getMainLooper());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(dialogBox==null) {
                            AlertDialog.Builder myDialogBox = new AlertDialog.Builder(context);
                            myDialogBox.setTitle("No Internet");
                            myDialogBox.setMessage("Check your internet connection");
                            myDialogBox.setCancelable(false);
                            myDialogBox.setPositiveButton("try again", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    dialogBox=null;

                                }

                            });
                            myDialogBox.setNegativeButton("exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dialogBox=null;
                                    C.GO_OFFLINE = true;
                                    System.exit(0);
                                }
                            });

                            dialogBox = myDialogBox.create();
                            dialogBox.show();
                        }
                    }
                }, 500);

            System.out.println("disconncted");
        }




    }
}





