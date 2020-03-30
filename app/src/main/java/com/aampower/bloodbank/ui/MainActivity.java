package com.aampower.bloodbank.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //2ukc6J3N1Kp0V/zyTZk/kPcgFJQ=
    //Debug: 0rJYIhbntIoWvHR47vU+aWkQBfc=

    Button btnFB, btnGmail, btnPhoneNumber;

    CallbackManager callbackManager;
    TextView txtEmail, txtBirthday, txtFriends;
    LoginButton login_button;
    ImageView avatar;
    ProgressDialog progressDialog;

    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 0;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = MainActivity.this;
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
        }

//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
//
//            Window window = context.getWindow();
//
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            window.setStatusBarColor(ContextCompat.getColor(context, R.color.textColorDark));
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//
//        }

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        txtEmail = findViewById(R.id.txtEmail);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtFriends = findViewById(R.id.txtFriends);
        login_button = findViewById(R.id.login_button);
        avatar = findViewById(R.id.avatar);
        signInButton = findViewById(R.id.sign_in_button);
        btnFB = findViewById(R.id.btnFB);
        btnGmail = findViewById(R.id.btnGmail);
        btnPhoneNumber = findViewById(R.id.btnPhoneNumber);

        login_button.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Retrieving data....");
                progressDialog.show();

                String accessToken = loginResult.getAccessToken().getToken();

                Log.d("token: ", accessToken);

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        progressDialog.dismiss();

                        Log.d("response", response.toString());

                        getData(object);

                    }
                });


                Bundle bundle = new Bundle();

                bundle.putString("fields", "id,email,birthday,friends");

                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        if (AccessToken.getCurrentAccessToken() != null){

            txtEmail.setText(AccessToken.getCurrentAccessToken().getUserId());

        }


        // Google sign in working here....


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // Google signout code

//        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(MainActivity.this, "Signout successfully!", Toast.LENGTH_SHORT).show();
//            }
//        });


        btnFB.setOnClickListener(this);
        btnGmail.setOnClickListener(this);
        btnPhoneNumber.setOnClickListener(this);

    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void getData(JSONObject object) {

        try {

            URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=200");

            Picasso.get().load(profile_picture.toString()).into(avatar);

            Toast.makeText(this, object.getString("email"), Toast.LENGTH_SHORT).show();

            txtEmail.setText(object.getString("email"));
            txtBirthday.setText(object.getString("birthday"));
            txtFriends.setText("Friends: " + object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }


        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            Picasso.get().load(account.getPhotoUrl()).into(avatar);

            Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();

            txtEmail.setText(account.getDisplayName());
            txtBirthday.setText(account.getEmail());
            txtFriends.setText(account.getId());



//            startActivity();
        } catch (ApiException e) {
            e.printStackTrace();

            Log.d("Google Sign Error", "signInResult: failed code= " + e.getStatusCode());

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onStart() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null){
            Picasso.get().load(account.getPhotoUrl()).into(avatar);

            txtEmail.setText(account.getDisplayName());
            txtBirthday.setText(account.getEmail());
            txtFriends.setText(account.getId());
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        if (preferences.getString("password", "").length() > 0){
            startActivity(new Intent(context, HomePageActivity.class));
            finish();
        }


        super.onStart();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnFB){
            //login_button.performClick();

            Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.btnGmail){
//            signIn();

            Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.btnPhoneNumber){

            startActivity(new Intent(context, PhoneVeriActivity.class));

        }

    }
}
