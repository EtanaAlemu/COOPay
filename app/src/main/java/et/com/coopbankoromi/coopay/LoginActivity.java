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
import androidx.cardview.widget.CardView;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import et.com.coopbankoromi.coopay.helper.VolleySingleton;
import et.com.coopbankoromi.coopay.model.Customer;
import et.com.coopbankoromi.coopay.ui.DashboardActivity;
import et.com.coopbankoromi.coopay.ui.SignupActivity;
import et.com.coopbankoromi.coopay.util.URLs;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mUsernameTxt;
    EditText mPasswordTxt;
    TextView mSignUp;
    TextView mForgetPassword;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private LinearLayout mLoginFormView;
    private View focusView;
    private boolean cancel;
    private Customer user;

    public static boolean isValidUsername(String name) {

        // Regex to check valid username.
        String regex = "^[A-Za-z]\\w{5,29}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (name == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CardView mLoginBtn = findViewById(R.id.login_btn);
        mSignUp = findViewById(R.id.sign_up_btn);
        mForgetPassword = findViewById(R.id.forget_pass_btn);

        mUsernameTxt = findViewById(R.id.username_input);
        mPasswordTxt = findViewById(R.id.pass_input);
        mProgressBar = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        focusView = null;
        cancel = false;
//
//        mAuth = FirebaseAuth.getInstance();

        mUsernameTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    if (TextUtils.isEmpty(textView.getText().toString())) {
                        mUsernameTxt.setError(getString(R.string.error_field_required));
                        focusView = mUsernameTxt;
                        focusView.requestFocus();
                    } else if (!isValidUsername(textView.getText().toString())) {
                        mUsernameTxt.setError(getString(R.string.error_invalid_username));
                        focusView = mUsernameTxt;
                        focusView.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

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
        mLoginBtn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mForgetPassword.setOnClickListener(this);

    }

    private void loginUser() {
        String username, password;

        // Reset errors.
        mUsernameTxt.setError(null);
        mPasswordTxt.setError(null);
        final boolean[] userAuthenticated = {false};

        // Store values at the time of the login attempt.
        username = mUsernameTxt.getText().toString();
        password = mPasswordTxt.getText().toString();

        cancel = false;
        focusView = null;

        // Check for a valid email.
        if (TextUtils.isEmpty(username)) {
            mUsernameTxt.setError(getString(R.string.error_field_required));
            focusView = mUsernameTxt;
            cancel = true;
        } else if (!isValidUsername(username)) {
            mUsernameTxt.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameTxt;
            cancel = true;
        }
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
        if (TextUtils.isEmpty(password)) {
            mPasswordTxt.setError(getString(R.string.error_field_required));
            focusView = mPasswordTxt;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
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
                                    user = new Customer(
                                            userJson.getInt("id"),
                                            userJson.getString("username"),
                                            userJson.getString("email"),
                                            userJson.getString("gender")
                                    );

                                    userAuthenticated[0] = true;


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
                //starting the profile activity
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("username", user.getUsername());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("gender", user.getGender());
                startActivity(intent);
                finish();
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
    private void showProgress(boolean show) {

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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.login_btn:
                loginUser();
                break;
            case R.id.sign_up_btn:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                break;
            case R.id.forget_pass_btn:

                break;
        }
    }
}