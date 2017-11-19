package dev.uublabs.weekend2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    private DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    void showDialog()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        newFragment = MyDialogFragment.newInstance(1);
        newFragment.show(ft, "dialog");
    }

    public void goToPdf(View view)
    {
        startActivity(new Intent(this, PdfViewerActivity.class));
    }

    public void displayDialog(View view)
    {
        showDialog();
        //only display the dialog fragment for 3 seconds

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                newFragment.dismiss();
            }
        }, 3000);
    }

    public void displayAlert(View view)
    {
        switch (view.getId())
        {
            case(R.id.btnAlertDefault):
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Alert message to be shown");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            }
            case (R.id.btnAlertCustom):
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater = alertDialog.getLayoutInflater();
                @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.custom_alert, null);
                alertDialog.setView(dialogView);
                alertDialog.setTitle("CustomAlertDialog");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            }
            case (R.id.btnAlertOptions):
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose favorite player");
                builder.setItems(R.array.players, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case 0:
                                Toast.makeText(MainActivity.this, "Black Mamba, Nice Choice!", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "D Wade, FTW!", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Kick it old skool with Timmy D!", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(MainActivity.this, "Cry Baby James!", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(MainActivity.this, "Look at all those dimes Nash!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void sendSMS(View view)
    {
        startActivity(new Intent(this, SMSActivity.class));
    }

    public void sendNotification(View view)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "dev.uublabs.weekend2")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Reminder to Text").setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentText("You haven't contacted Rob in a while, send him a text!");

        Intent resultIntent = new Intent(this, SMSActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());
    }

    public void goToStopWatch(View view)
    {
        startActivity(new Intent(this, StopWatchActivity.class));
    }

}
