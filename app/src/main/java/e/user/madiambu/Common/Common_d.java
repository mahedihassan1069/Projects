package e.user.madiambu.Common;



import android.location.Location;

import e.user.madiambu.Remote.FCMClient;
import e.user.madiambu.Remote.GoogleMapAPI;
import e.user.madiambu.Remote.IFCMService;
import e.user.madiambu.Remote.IGoogleAPI;
import e.user.madiambu.Remote.IGoogleAPIKey;
import e.user.madiambu.Remote.RetrofitClient;

public class Common_d {

    //Drivers Table contain information of avaliable driver..
    //Pickup request contain information about pickup request from user..
    //Riders Table contain info who register rider app
    //users table contain info who register driver app..

    public static String currentToken="";

    public static final  String driver_tbl="Drivers";
    public static final  String user_driver_tbl="DriverInformation";
    public static final  String user_rider_tbl="RiderInformation";
    public static final  String pickup_request_tbl="PickupRequest";
    public static final  String token_tbl="Tokens";

    //global last location;
    public static Location lastLocation=null;

    public static final String baseURL="http://maps.googleapis.com";
    public static final String fcmURL="https://fcm.googleapis.com/";

    public static final String googleAPIKey="http://maps.googleapis.com";

    public static double base_fare=2.55;
    public static double time_rate=0.35;
    public static double distance_rate=1.45;

    public static double getPrice(double km,int min)
    {
        return (base_fare+(time_rate*min)+(distance_rate*km));
    }


    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static IGoogleAPIKey getGoogleAPIKeyService()
    {
        return GoogleMapAPI.getClient(googleAPIKey).create(IGoogleAPIKey.class);
    }

}
