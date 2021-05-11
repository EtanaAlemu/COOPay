package et.com.coopbankoromi.coopay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        idTxt.setText(intent.getIntExtra("id", -1));
        usernameTxt.setText(intent.getStringExtra("username"));
        emailTxt.setText(intent.getStringExtra("email"));
        genderTxt.setText(intent.getStringExtra("gender"));

    }
}