/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.User.UserItems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import dev.dileepabandara.finedeliver.R;
import dev.dileepabandara.finedeliver.User.UserDashboard;

public class UserGuideToPostItem extends AppCompatActivity {

    Button btnOKToPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide_to_post_item);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnOKToPost = findViewById(R.id.btnOKToPost);

        notification();
        dialogBoxMessage();

        btnOKToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserGuideToPostItem.this, UserDashboard.class));
                finishAffinity();
            }
        });

    }


    private void dialogBoxMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserGuideToPostItem.this);
        builder.setTitle("Order has been sent successfully!");
        builder.setMessage("Your order has been sent to our team.We will checkout your order availability and let you know." +
                "\nThank you for using FineDeliver service");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
/*        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void notification() {
        //Show notification
        String notificationTitle = "Order Successfully Send";
        String notificationMessage = "Your order confirmation is now pending";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                UserGuideToPostItem.this
        )
                .setSmallIcon(R.drawable.ic_round_phone_android_24)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        //OnTouch goto activity
        Intent intent = new Intent(UserGuideToPostItem.this, UserGuideToPostItem.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", notificationMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(UserGuideToPostItem.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        notificationManager.notify(0, builder.build());
    }

}