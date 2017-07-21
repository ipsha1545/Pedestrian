package com.example.pedestrian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.curl.Curl;
import com.example.pedestrian.utils.AppConstants;
import com.example.pedestrian.utils.Preferences;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.acra.annotation.ReportsCrashes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;


/**
 * Created by deepak.sharma on 5/11/2017.
 */
@ReportsCrashes



public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    //For facebook
    private TextView username;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ImageView profilepic;
    private TextView data;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText et_loginId, et_password ;
    private Button btn_submit;
    private TextInputLayout input_layout_login_id, input_layout_password;

    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;

    Button button;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        et_loginId = (EditText) findViewById(R.id.et_loginId);
        et_password = (EditText) findViewById(R.id.et_password);

        data = (TextView) findViewById(R.id.data);
        //textView = (TextView)findViewById(R.id.textView);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);

        setFacebookData(loginResult);

    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook:onCancel");
        // ...
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
        // ...
    }
});}

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }



    //save users login for facebook


    public void updateUI() {
        Log.d(TAG, "Updated UI for user");
        // ...
    }


    @Override
    public void onClick(View v) {

        System.out.println("Button clicked");

        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }


        switch (v.getId())
        {
            case R.id.login_button:
                LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        Arrays.asList("user_friends", "email", "public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                setFacebookData(loginResult);

                            }

                            @Override
                            public void onCancel() {
                                Log.d("onCancel", "Cancelled by user");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Log.d("onError", "Facebook error");
                            }
                        });
                break;
        }
    }


    //For facebook login
    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(

                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();

                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);

                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic url is " + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");

                            submitFacebookLogin(email,id,Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());

                            Intent mainIntent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(mainIntent);
                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.login_button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);

            }

        });

    }

    //sign in with google
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = "";
            if (acct.getPhotoUrl() != null)
            personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String id = acct.getId();

            submitGmailLogin(personName, id, personPhotoUrl);


            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

        } else {
            // Signed out, show unauthenticated UI.

            Log.e(TAG, "Failed");
        }
    }


    private boolean validateName() {
        if (et_loginId.getText().toString().trim().isEmpty()) {
            input_layout_login_id.setError(getString(R.string.err_msg_login_id));
            requestFocus(input_layout_login_id);
            return false;
        } else {
            input_layout_login_id.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.err_msg_password));
            requestFocus(input_layout_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void submitLogin() {

        if (!validateName()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }


        String loginID = et_loginId.getText().toString();
        String password = et_password.getText().toString();
        String response = Curl.getInSeparateThread(AppConstants.BaseURL + "/login?" +"loginId=" + loginID + "&password="+ password);

        try {
            if (response.equalsIgnoreCase("")){
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_LONG).show();
            }else {
                JSONObject jsonObject = new JSONObject(response.toString());
                Preferences.setUserId(jsonObject.getInt("userId"));
                Preferences.setUserName(loginID);
                Preferences.setUserImage("");
                Intent mainIntent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(mainIntent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    private void submitGmailLogin(String username, String accountID, String imageUrl) {
      //  signOut();
        String response = Curl.getInSeparateThread(AppConstants.BaseURL + "/login?" +"loginId=" + username.replaceAll(" ","") + "&password="+ accountID + "&source=" + Integer.parseInt("1"));

        try {
            if (response.equalsIgnoreCase("")){
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_LONG).show();
            }else {
                JSONObject jsonObject = new JSONObject(response.toString());
                Preferences.setUserId(jsonObject.getInt("userId"));
                Preferences.setUserName(username);

                Log.e(TAG," Before setting user image:");
                Preferences.setUserImage(imageUrl);
                Log.e(TAG," After setting user image:" + imageUrl);

                Intent mainIntent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(mainIntent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    private void submitFacebookLogin(String username, String accountID, String imageUrl) {
        Log.e("entering","Enterig fblogin");
        String response = Curl.getInSeparateThread(AppConstants.BaseURL + "/login?" +"loginId=" + username.replaceAll(" ","") + "&password="+ accountID + "&source=" + Integer.parseInt("1"));

        try {
            if (response.equalsIgnoreCase("")){
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_LONG).show();
            }else {
                JSONObject jsonObject = new JSONObject(response.toString());
                Preferences.setUserId(jsonObject.getInt("userId"));
                Preferences.setUserName(username);

                Log.e("username","username" + Preferences.getUserName());
                Preferences.setUserImage(imageUrl);


                Intent mainIntent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(mainIntent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
// An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
        }
}
