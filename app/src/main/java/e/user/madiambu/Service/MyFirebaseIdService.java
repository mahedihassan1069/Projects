package e.user.madiambu.Service;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import androidx.annotation.NonNull;
import e.user.madiambu.Common.Common_d;
import e.user.madiambu.CustommerCall;
import e.user.madiambu.Model.Token;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        //beacuse i will send a firebase message with contain lat and lng from rider app
        //so i need to convert message to LatLng
        String type=remoteMessage.getData().get("type");

       // Log.d("TYPE", type);
   try {
       if (type.equals("driver")) {

           double lat = Double.valueOf(Objects.requireNonNull(remoteMessage.getData().get("altitude")));
           double lon = Double.valueOf(Objects.requireNonNull(remoteMessage.getData().get("longitude")));
           String riderToken = remoteMessage.getData().get("riderToken");

           Log.d("Recieved", remoteMessage.getData().get("type") + " " + lat + " " + lon);
           //LatLng customer_location=new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
           Intent intent = new Intent(getBaseContext(), CustommerCall.class);
           intent.putExtra("lat", lat);
           intent.putExtra("lng", lon);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       /* if (remoteMessage.getNotification() != null) {
            intent.putExtra("customer", remoteMessage.getNotification().getTitle());
        }*/
           intent.putExtra("riderToken", riderToken);

           startActivity(intent);
       } else {

           final String title = remoteMessage.getData().get("title");
           final String body = remoteMessage.getData().get("body");


           /*showNotification(remoteMessage.getNotification().getRiderToken(),remoteMessage.getNotification().getBody());*/

           Handler handler = new Handler(Looper.getMainLooper());
           handler.post(new Runnable() {
               @Override
               public void run() {
                   if (title != null || body != null)
                       Toast.makeText(MyFirebaseIdService.this, "" + title + ": " + body, Toast.LENGTH_SHORT).show();

               }
           });


       }
   }catch (Exception e)
   {
       e.printStackTrace();
   }





//        LatLng customer_location=new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
//
//        Intent intent=new Intent(getBaseContext(), CustommerCall.class);
//        intent.putExtra("lat",customer_location.latitude);
//        intent.putExtra("lng",customer_location.longitude);
//
//       startActivity(intent);

        /*showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());*/


    }

   /* private void showNotification(String title, String body) {
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID="com.example.ubarclone.test";

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("MOBIN");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
    }*/

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refreshToken);

        /*Log.d("TOKENFIREBASE",s);*/
    }

    private void updateTokenToServer(String refreshToken) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference(Common_d.token_tbl);

        Token token=new Token(refreshToken);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) //if already login,must update Token..
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(token);
    }


}
