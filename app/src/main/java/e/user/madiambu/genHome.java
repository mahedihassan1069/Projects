package e.user.madiambu;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class genHome extends AppCompatActivity {

    private BottomNavigationView mManiNav;
    private FrameLayout mFrame;

    private GenHomeFragment genHomeFragment;
    private GenHospitalFragment genHospitalFragment;
    private GenAmbulanceFragment genAmbulanceFragment;
    private GenProfilefragment genProfilefragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_home);

        mFrame=(FrameLayout) findViewById(R.id.main_frame);
        mManiNav=(BottomNavigationView) findViewById(R.id.main_nav);

        genHomeFragment=new GenHomeFragment();
        genHospitalFragment=new GenHospitalFragment();
        genAmbulanceFragment=new GenAmbulanceFragment();
        genProfilefragment=new GenProfilefragment();

        setFragment(genHomeFragment);

        mManiNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.gen_nav_home:
                        //mManiNav.setItemBackgroundResource(R.color.blue);
                        setFragment(genHomeFragment);
                        return true;

                    case R.id.gen_nav_hospital:
                       // mManiNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(genHospitalFragment);
                        return true;

                    case R.id.gen_nav_ambulance:
                        //mManiNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(genAmbulanceFragment);
                        return true;
                    case R.id.gen_nav_profile:
                       // mManiNav.setItemBackgroundResource(R.color.light_blue);
                        setFragment(genProfilefragment);
                        return true;

                        default:
                            return false;
                }
            }
        });

    }

    private void setFragment(Fragment Fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame,Fragment);
        transaction.commit();


    }
}
