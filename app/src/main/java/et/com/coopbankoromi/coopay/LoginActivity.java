package et.com.coopbankoromi.coopay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import et.com.coopbankoromi.coopay.controller.DashboardActivity;
import et.com.coopbankoromi.coopay.helper.VolleySingleton;
import et.com.coopbankoromi.coopay.model.Customer;
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
    private CoordinatorLayout coordinatorLayout;

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
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        showProgress(false);

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
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        mLoginBtn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mForgetPassword.setOnClickListener(this);

    }


    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.ROOT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            showProgress(false);
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);

                                //if no error in response
                                if (!obj.getBoolean("error")) {

                                    //getting the user from the response
                                    JSONObject userJson = obj.getJSONObject("user");

                                    //creating a new user object
                                    user = new Customer(
                                            userJson.getInt("id"),
                                            userJson.getString("username"),
                                            userJson.getString("email")
                                    );

                                    //starting the profile activity
                                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    intent.putExtra("id", user.getId());
                                    intent.putExtra("username", user.getUsername());
                                    intent.putExtra("email", user.getEmail());
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, obj.getString("message"), Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                }
                                            });
                                    snackbar.setActionTextColor(Color.RED);
                                    View sbView = snackbar.getView();
                                    TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                    textView.setTextColor(Color.YELLOW);
                                    snackbar.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            showProgress(false);
                            if (error instanceof TimeoutError) {
                                //This indicates that the reuest has either time out or there is no connection

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "request time out", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            } else if (error instanceof NoConnectionError) {
                                // Error indicating that there was an Authentication Failure while performing the request

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "no connection", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            } else if (error instanceof AuthFailureError) {
                                // Error indicating that there was an Authentication Failure while performing the request

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "authentication failure", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            } else if (error instanceof ServerError) {
                                //Indicates that the server responded with a error response

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "server error", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            } else if (error instanceof NetworkError) {
                                //Indicates that there was network error while performing the request

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "network error", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            } else if (error instanceof ParseError) {
                                // Indicates that the server response could not be parsed

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "server response couldn't be parsed", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "server response couldn't be parsed", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                            }

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("apicall", "login");
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);



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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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