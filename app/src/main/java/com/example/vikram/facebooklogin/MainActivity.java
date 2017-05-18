package com.example.vikram.facebooklogin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    //LoginButton loginButton;
    CallbackManager callbackManager;
    Button login, logout, share, glogin;
    LoginManager loginmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        //loginButton = (LoginButton) findViewById(R.id.login_button);
        loginmanager = com.facebook.login.LoginManager.getInstance();
        glogin = (Button)findViewById(R.id.google);
        login = (Button) findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);
        share = (Button) findViewById(R.id.share);
        share.setEnabled(false);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!=null)
        {
            login.setText("connected");
            share.setEnabled(true);
            logout.setEnabled(true);
        }

        //loginButton.setReadPermissions("public_profile", "email","user_friends");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout.setEnabled(false);
                loginmanager.logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile", "user_birthday", "user_friends"));
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginmanager.logOut();
                login.setText("CONNECT WITH FACEBOOK");
                login.setEnabled(true);
                share.setEnabled(false);
            }
        });

        loginmanager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                login.setText("CONNECTED");
                getUserDetails(loginResult);
                login.setEnabled(false);
                logout.setEnabled(true);
                share.setEnabled(true);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert, null);
                dialogBuilder.setView(dialogView);
                Button facebook = (Button) dialogView.findViewById(R.id.facebook);

                //facebook.animate().rotation(80).setDuration(2000).start();
                Button whatsapp = (Button) dialogView.findViewById(R.id.whatsapp);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog shareDialog = new ShareDialog(MainActivity.this);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent
                                    .Builder()
                                    .setQuote("Google")
                                    .setContentUrl(Uri.parse("https://www.google.com"))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                        alertDialog.dismiss();
                    }
                });
                whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
                        whatsappIntent.setPackage("com.whatsapp");

                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "http://google.com");
                        whatsappIntent.setType("text/plain");
                        //whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://com.example.vikram.facebooklogin/drawable/sample"));
                        //whatsappIntent.setType("*/*");
                        //whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(whatsappIntent);
                            alertDialog.dismiss();
                        } catch (android.content.ActivityNotFoundException ex) {

                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            //ToastHelper.MakeShortText("Whatsapp have not been installed.");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();



    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

// facebook
    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        //Intent intent = new Intent(MainActivity.this, UserProfile.class);
                        Log.d("userProfile", json_object.toString());
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }
}
