package e.user.madiambu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class driverHome extends AppCompatActivity {

    private BottomNavigationView dManiNav;
    private FrameLayout mFrame;

    private DriverHomeFragment driverHomeFragment;
    private  DriverPickUpRequest driverPickUpRequest;
    private DriverHistory driverHistory;
    private DriverProfile driverProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        mFrame=(FrameLayout) findViewById(R.id.d_main_frame);
        dManiNav=(BottomNavigationView) findViewById(R.id.driver_main_nav);
        
        driverHomeFragment=new DriverHomeFragment();
        driverPickUpRequest=new DriverPickUpRequest();
        driverHistory=new DriverHistory();
        driverProfile=new DriverProfile();

        setFragment(driverHomeFragment);

        dManiNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.driver_nav_home:
                        //mManiNav.setItemBackgroundResource(R.color.blue);
                        setFragment(driverHomeFragment);
                        return true;

                    /*case R.id.driver_nav_pickup_request:
                        // mManiNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(driverPickUpRequest);
                        return true;

                    case R.id.driver_nav_history:
                        //mManiNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(driverHistory);
                        return true;*/
                    case R.id.driver_nav_profile:
                        // mManiNav.setItemBackgroundResource(R.color.light_blue);
                        setFragment(driverProfile);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.d_main_frame,fragment);
        transaction.commit();
    }
}
