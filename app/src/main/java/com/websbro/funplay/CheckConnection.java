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
                            myDialogBox.setTitle("Alert");
                            myDialogBox.setMessage("alert text");
                            myDialogBox.setCancelable(false);
                            myDialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    dialogBox=null;

                                }

                            });
                            myDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dialogBox=null;
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





