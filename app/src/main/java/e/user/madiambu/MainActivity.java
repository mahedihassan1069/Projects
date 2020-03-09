package e.user.madiambu;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button signUp, signIn;
    private TextView Driver_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        Driver_login=findViewById(R.id.driver_login);

        Driver_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDriverLogin();
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slow_fade_in, R.anim.still);
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slow_fade_in, R.anim.still);
                finish();
            }
        });
    }

    private void sendDriverLogin()
    {
        Intent driverIntent=new Intent(MainActivity.this, driver_Code.class);
        startActivity(driverIntent);
    }
}
