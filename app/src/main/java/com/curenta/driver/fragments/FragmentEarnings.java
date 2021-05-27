package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.EarningListAdapter;
import com.curenta.driver.databinding.FragmentEarningsBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;
import com.curenta.driver.retrofit.apiDTO.EarningAPIResponse;
import com.curenta.driver.retrofit.apiDTO.EarningRequest;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentEarnings extends Fragment {
    FragmentEarningsBinding fragmentEarningsBinding;
    EarningListAdapter earningListAdapter;
    public static final boolean SHOW_ADAPTER_POSITIONS = true;
    ArrayList<EarningListAdapter.Section> sections = new ArrayList<>();
    ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEarningsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_earnings, container, false);
        getEarnings();
        for (int i = 0; i < 5; i++) {
            appendSection(i, 5);
        }
        fragmentEarningsBinding.header.txtLabel.setText("Earnings");
        fragmentEarningsBinding.header.imageView3.setVisibility(View.INVISIBLE);
        fragmentEarningsBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity()!=null && getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch(IllegalStateException ex) {

                }
                catch(Exception ex) {

                }
            }
        });

        fragmentEarningsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        earningListAdapter = new EarningListAdapter(5, 5, false, false, false, SHOW_ADAPTER_POSITIONS);
        earningListAdapter.setData(sections);
        fragmentEarningsBinding.recyclerView.setAdapter(earningListAdapter);
        return fragmentEarningsBinding.getRoot();
    }

    public void appendSection(int index, int itemCount) {
        EarningListAdapter.Section section = new EarningListAdapter.Section();
        section.index = index;
        section.copyCount = 0;
        section.header = "December 2020";

        for (int j = 0; j < itemCount; j++) {
            section.items.add(new EarningListAdapter.Item("November 30 - December 6", "$320.41 "));
        }

        sections.add(section);
    }

    private void getEarnings() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                ProgressDialog dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                EarningRequest requestDto = new EarningRequest("" + LoggedInUser.getInstance().driverId);
                Gson gson = new Gson();
                String request = gson.toJson(requestDto);
                RetrofitClient.getAPIClient().getDriverEarnings(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<EarningAPIResponse>() {
                            @Override
                            public void onSuccess(EarningAPIResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {

                                    Log.d("earningAPICall", "success " + response.toString());

                                   //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.d("earningAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("earningAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("earningAPICall", "failed " + e.toString());
        }
    }
}