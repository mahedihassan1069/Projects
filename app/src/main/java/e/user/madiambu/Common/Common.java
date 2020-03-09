package e.user.madiambu.Common;

import android.location.Location;

public class Common {
    //Drivers Table contain information of avaliable driver..
    //Pickup request contain information about pickup request from user..
    //Riders Table contain info who register rider app
    //users table contain info who register driver app..

    public static final  String driver_tbl="Drivers";
    public static final  String user_driver_tbl="DriverInformation";
    public static final  String user_rider_tbl="RiderInformation";
    public static final  String pickup_request_tbl="PickupRequest";

    //global last location;
    public static Location lastLocation=null;


    public static double base_fare=2.55;
    public static double time_rate=0.35;
    public static double distance_rate=1.45;

    public static double formulaPrice(double km,double min)
    {
        return (base_fare+(time_rate*min)+(distance_rate*km));
    }
}
