package e.user.madiambu;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class mngHome extends AppCompatActivity {

    private BottomNavigationView mngMainNav;
    private FrameLayout mngFrame;

    private MngHomeFragment mngHomeFragment;
    private MngAddHospitalFragment mngAddHospitalFragment;
    private MngAddDriverFragment mngAddDriverFragment;
    private MngProfileFragment mngProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mng_home);

        mngFrame=(FrameLayout) findViewById(R.id.m_main_frame);
        mngMainNav=(BottomNavigationView) findViewById(R.id.m_main_nav);

        mngHomeFragment=new MngHomeFragment();
        mngAddHospitalFragment=new MngAddHospitalFragment();
        mngAddDriverFragment=new MngAddDriverFragment();
        mngProfileFragment=new MngProfileFragment();

        setFragment(mngHomeFragment);

        mngMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.mng_nav_home:
                        //mManiNav.setItemBackgroundResource(R.color.blue);
                        setFragment(mngHomeFragment);
                        return true;

                    case R.id.mng_nav_addhospital:
                        // mManiNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(mngAddHospitalFragment);
                        return true;

                    case R.id.mng_nav_adddriver:
                        //mManiNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(mngAddDriverFragment);
                        return true;
                    case R.id.mng_nav_profile:
                        // mManiNav.setItemBackgroundResource(R.color.light_blue);
                        setFragment(mngProfileFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment Fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.m_main_frame,Fragment);
        transaction.commit();


    }
}
