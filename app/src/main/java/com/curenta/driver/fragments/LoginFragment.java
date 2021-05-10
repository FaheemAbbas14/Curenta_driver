package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.MainApplication;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentLoginBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.LoginRequest;
import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.Helper;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    FragmentLoginBinding fragmentLoginBinding;
    boolean isEmailValid, isPasswordValid;
    String heading = "Login";
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private static final int FB_SIGN_IN = 2;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    String TAG = "Login";
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentLoginBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).makeOrignalBackground();
        fragmentLoginBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserInfo.getInstance().deviceId.equalsIgnoreCase("test")){
                    String deviceId = Helper.getDeviceId(getApplicationContext());
                    UserInfo.getInstance().deviceId = deviceId;
                }
                if(UserInfo.getInstance().fcmToken!=null && UserInfo.getInstance().fcmToken.equalsIgnoreCase("test")){
                    MainApplication.setupOnseSignal();
                }
                SetValidation();
               // ((LoginActivity) getActivity()).showFragment(new FragmentTutorial());
            }
        });
        fragmentLoginBinding.txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentForgetPassword());
            }
        });
        initFb();
        initGoogle();

        fragmentLoginBinding.imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
        fragmentLoginBinding.imgFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
            }
        });
        generateKeyHash();
        return fragmentLoginBinding.getRoot();
    }

    private void initGoogle() {

        if (googleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }


    private void initFb() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginManager
                = LoginManager.getInstance();
        callbackManager
                = CallbackManager.Factory.create();

        loginManager
                .registerCallback(
                        callbackManager,
                        new FacebookCallback<LoginResult>() {

                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                GraphRequest request = GraphRequest.newMeRequest(

                                        loginResult.getAccessToken(),

                                        new GraphRequest.GraphJSONObjectCallback() {

                                            @Override
                                            public void onCompleted(JSONObject object,
                                                                    GraphResponse response) {

                                                if (object != null) {
                                                    try {
                                                        String first_name = object.getString("first_name");
                                                        String last_name = object.getString("last_name");
                                                        String email = object.getString("email");
                                                        String fbUserID = object.getString("id");
                                                        String image_url = "https://graph.facebook.com/" + fbUserID + "/picture?type=normal";
                                                        UserInfo.getInstance().firstName = first_name;
                                                        UserInfo.getInstance().lastName = last_name;
                                                        UserInfo.getInstance().userEmail = email;
                                                        UserInfo.getInstance().imageURL = image_url;
                                                        FragmentUtils.getInstance().addFragment(getActivity(), new RegistrationFragment(), R.id.fragContainer);

                                                        // do action after Facebook login success
                                                        // or call your API
                                                    } catch (JSONException | NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

                                Bundle parameters = new Bundle();
                                parameters.putString(
                                        "fields",
                                        "id, first_name,last_name, email, gender");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                Log.v("LoginScreen", "---onCancel");
                            }

                            @Override
                            public void onError(FacebookException error) {
                                // here write code when get error
                                Log.v("LoginScreen", "----onError: "
                                        + error.getMessage());
                            }
                        });

    }


    public void SetValidation() {
        // Check for a valid email address.
        if (fragmentLoginBinding.edtEmail.getText().toString().isEmpty()) {
            fragmentLoginBinding.txtEmailError.setText(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(fragmentLoginBinding.edtEmail.getText().toString()).matches()) {
            fragmentLoginBinding.txtEmailError.setText(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            fragmentLoginBinding.txtEmailError.setText("");
        }

        // Check for a valid password.
        if (fragmentLoginBinding.edtPassword.getText().toString().isEmpty()) {
            fragmentLoginBinding.txtPasswordError.setText(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (fragmentLoginBinding.edtPassword.getText().length() < 6) {
            fragmentLoginBinding.txtPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            fragmentLoginBinding.txtPasswordError.setText("");
        }

        if (isEmailValid && isPasswordValid) {
            RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
            doLogin(fragmentLoginBinding.edtEmail.getText().toString(), fragmentLoginBinding.edtPassword.getText().toString());

        }

    }

    private void doLogin(String email, String password) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);

                dialog.show();
                LoginRequest loginRequest = new LoginRequest(email, password, UserInfo.getInstance().fcmToken, UserInfo.getInstance().deviceId);
                Gson gson = new Gson();
                String request = gson.toJson(loginRequest);
                RetrofitClient.getAPIClient().loginAPI(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DriverAPIResponse>() {
                            @Override
                            public void onSuccess(DriverAPIResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    LoggedInUser loggedInUser = LoggedInUser.getInstance();
                                    loggedInUser.copyData(response.data);
                                    loggedInUser.password=password;
                                    Gson gson = new Gson();
                                    String loggedInUserGson = gson.toJson(loggedInUser);
                                    Preferences.getInstance().setString("loggedInUser",loggedInUserGson);
                                    Log.d("loginAPICall", "success " + response.toString());
                                    boolean isTutorialShown=Preferences.getInstance().getBoolean("tutorialShown");
                                    if(!isTutorialShown){
                                        ((LoginActivity) getActivity()).showFragment(new FragmentTutorial());
                                    }
                                    else {
                                    Intent nextIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    startActivity(nextIntent);
                                    getActivity().finish();
                                    }
                                   // Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.d("loginAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("loginAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("loginAPICall", "failed " + e.toString());
        }
    }

    private void generateKeyHash() {
        PackageInfo info;
        try {
            info = getActivity().getPackageManager().getPackageInfo("com.curenta.driver", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.d("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            // add this line
            callbackManager.onActivityResult(
                    requestCode,
                    resultCode,
                    data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            String name = result.getSignInAccount().getDisplayName();
            String first_name = result.getSignInAccount().getGivenName();
            String last_name = result.getSignInAccount().getFamilyName();
            String email = result.getSignInAccount().getEmail();
            if(result.getSignInAccount().getPhotoUrl()!=null){
                UserInfo.getInstance().imageURL = result.getSignInAccount().getPhotoUrl().toString();
            }
            UserInfo.getInstance().firstName = first_name;
            UserInfo.getInstance().lastName = last_name;
            UserInfo.getInstance().userEmail = email;

            FragmentUtils.getInstance().addFragment(getActivity(), new RegistrationFragment(), R.id.fragContainer);
        } else {
            Toast.makeText(getContext(), "Sign in cancel " + result.getStatus(), Toast.LENGTH_LONG).show();
        }
    }

}