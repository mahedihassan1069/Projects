package e.user.madiambu;

import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUp extends AppCompatActivity {
    private Button sp,gen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        gen=findViewById(R.id.gen);
        sp=findViewById(R.id.sp);

        loadFragment(new Sign_Up_gen());

        gen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                gen.setBackground(getDrawable(R.drawable.gen_user_sign_up_enable));
                sp.setBackground(getDrawable(R.drawable.owner_hospital_user_enable));
                loadFragment(new Sign_Up_gen());
            }
        });

        sp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                gen.setBackground(getDrawable(R.drawable.gen_user_enable));
                sp.setBackground(getDrawable(R.drawable.owner_hospital_user));
                loadFragment(new Sign_Up_Mng());
            }
        });


    }

    public void popBackStackTillEntry(int entryIndex) {

        if (getSupportFragmentManager() == null) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() <= entryIndex) {
            return;
        }
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                entryIndex);
        if (entry != null) {
            getSupportFragmentManager().popBackStackImmediate(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }

    @Override
    public void onBackPressed() {
        popBackStackTillEntry(0);
        super.onBackPressed();

        overridePendingTransition(R.anim.still, R.anim.slow_fade_out);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
