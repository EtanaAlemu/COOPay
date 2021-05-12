package et.com.coopbankoromi.coopay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import et.com.coopbankoromi.coopay.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView idTxt, usernameTxt, emailTxt, genderTxt;
        idTxt = findViewById(R.id.id);
        usernameTxt = findViewById(R.id.username);
        emailTxt = findViewById(R.id.email);
        genderTxt = findViewById(R.id.gender);

        Intent intent = getIntent();
        usernameTxt.setText(intent.getStringExtra("username"));
        idTxt.setText(String.valueOf(intent.getIntExtra("id", -1)));
        emailTxt.setText(intent.getStringExtra("email"));
        genderTxt.setText(intent.getStringExtra("gender"));

    }
}