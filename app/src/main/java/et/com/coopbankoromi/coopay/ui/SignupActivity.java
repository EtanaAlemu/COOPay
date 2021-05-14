package et.com.coopbankoromi.coopay.ui;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import et.com.coopbankoromi.coopay.LoginActivity;
import et.com.coopbankoromi.coopay.R;
import et.com.coopbankoromi.coopay.helper.VolleySingleton;
import et.com.coopbankoromi.coopay.util.URLs;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, username, password, conf_password;
    CardView signup;
    TextView login;
    ProgressBar progressBar;
    private View focusView;
    private boolean cancel;
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
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username_input);
        password = findViewById(R.id.pass_input);
        conf_password = findViewById(R.id.confirm_pass_input);
        signup = findViewById(R.id.signup_btn);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        focusView = null;
        cancel = false;


        signup.setOnClickListener(this);
        login.setOnClickListener(this);


        conf_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    signupUser();
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.login:
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.signup_btn:
                signupUser();
                break;
        }
    }

    private void signupUser() {
        String mEmail, mUsername, mPassword, mConfPassword;

        // Reset errors.
        email.setError(null);
        username.setError(null);
        password.setError(null);
        conf_password.setError(null);

        // Store values at the time of the login attempt.
        mEmail = email.getText().toString().trim();
        mUsername = username.getText().toString();
        mPassword = password.getText().toString();
        mConfPassword = conf_password.getText().toString();

        cancel = false;
        focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(mUsername)) {
            username.setError(getString(R.string.error_field_required));
            focusView = username;
            cancel = true;
        } else if (!isValidUsername(mUsername)) {
            username.setError(getString(R.string.error_invalid_username));
            focusView = username;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(mEmail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mPassword)) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }
        if (!isPasswordValid(mPassword)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid confirm password, if the user entered one.
        if (TextUtils.isEmpty(mConfPassword)) {
            conf_password.setError(getString(R.string.error_field_required));
            focusView = conf_password;
            cancel = true;
        }
        if (!isConfPasswordValid(mPassword, mConfPassword)) {
            conf_password.setError(getString(R.string.error_invalid_conf_password));
            focusView = conf_password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

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
                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, obj.getString("message"), Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                    //starting the login activity
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, obj.getString("message"), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, e.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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
                    params.put("apicall", "signup");
                    params.put("username", mUsername);
                    params.put("email", mEmail);
                    params.put("password", mPassword);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isConfPasswordValid(String password, String mConfPassword) {
        if (password != null && mConfPassword != null) {
            return password.equals(mConfPassword);
        }
        return false;
    }

    private boolean isEmailValid(String email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern) && email.length() > 0;
    }

    private void showProgress(boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}