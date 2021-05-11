package et.com.coopbankoromi.coopay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import et.com.coopbankoromi.coopay.helper.VolleySingleton;
import et.com.coopbankoromi.coopay.model.Customer;
import et.com.coopbankoromi.coopay.util.URLs;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailTxt;
    EditText mPasswordTxt;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private LinearLayout mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView mLoginBtn = findViewById(R.id.login_btn);

        mEmailTxt = findViewById(R.id.email_input);
        mPasswordTxt = findViewById(R.id.pass_input);
        mProgressBar = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
//
        mAuth = FirebaseAuth.getInstance();

        mPasswordTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    loginUser();
                    return true;
                }
                return false;
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username, password;

        // Reset errors.
        mEmailTxt.setError(null);
        mPasswordTxt.setError(null);
        final boolean[] userAuthenticated = {false};

        // Store values at the time of the login attempt.
        username = mEmailTxt.getText().toString();
        password = mPasswordTxt.getText().toString();

        boolean cancel = false;
        View focusView = null;

//        // Check for a valid email.
//        if (TextUtils.isEmpty(email)) {
//            mEmailTxt.setError(getString(R.string.error_field_required));
//            focusView = mEmailTxt;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailTxt.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailTxt;
//            cancel = true;
//        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordTxt.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordTxt;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


//            AccountManage accountManage = new AccountManage(this);
//            userAuthenticated = accountManage.loginUser(username, password);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);

                                //if no error in response
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    //getting the user from the response
                                    JSONObject userJson = obj.getJSONObject("user");

                                    //creating a new user object
                                    Customer user = new Customer(
                                            userJson.getInt("id"),
                                            userJson.getString("username"),
                                            userJson.getString("email"),
                                            userJson.getString("gender")
                                    );

                                    userAuthenticated[0] = true;

                                    //starting the profile activity
                                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    intent.putExtra("id", user.getId());
                                    intent.putExtra("username", user.getUsername());
                                    intent.putExtra("email", user.getEmail());
                                    intent.putExtra("gender", user.getGender());
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


            if (userAuthenticated[0]) {
                showProgress(false);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "email or password incorrect", Toast.LENGTH_LONG).show();
                showProgress(false);
            }

//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
//                                showProgress(false);
//
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
//                                showProgress(false);
//                            }
//                        }
//                    });


//            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "Register successful!", Toast.LENGTH_LONG).show();
//                                showProgress(false);
//
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
//                                showProgress(false);
//                            }
//                }
//            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}