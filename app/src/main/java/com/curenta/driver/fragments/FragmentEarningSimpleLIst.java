package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.EarningSimpleAdapter;
import com.curenta.driver.databinding.FragmentEarningSimpleLIstBinding;
import com.curenta.driver.dto.EarningDto;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.EarningAPIResponse;
import com.curenta.driver.retrofit.apiDTO.EarningRequest;
import com.curenta.driver.utilities.InternetChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentEarningSimpleLIst extends Fragment {
    FragmentEarningSimpleLIstBinding fragmentEarningSimpleLIstBinding;
    EarningSimpleAdapter earningListAdapter;
    public static final boolean SHOW_ADAPTER_POSITIONS = true;
    ArrayList<EarningDto> earnings = new ArrayList<>();
    ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEarningSimpleLIstBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_earning_simple_l_ist, container, false);
        Calendar calendar = Calendar.getInstance();
        String to = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        getEarnings(from, to);
        fragmentEarningSimpleLIstBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Calendar calendar = Calendar.getInstance();
                String to = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

                String from;
                switch (checkedId) {
                    case R.id.dailyButton:
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                        getEarnings(from, to);
                        break;
                    case R.id.weekButton:
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                        getEarnings(from, to);
                        break;
                    case R.id.monthButtion:
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                        getEarnings(from, to);
                        break;
                    case R.id.yearButton:
                        calendar.set(Calendar.MONTH, 0);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                        getEarnings(from, to);
                        break;
                    default:
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        from = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                        getEarnings(from, to);
                        break;
                }
            }
        });
        fragmentEarningSimpleLIstBinding.header.txtLabel.setText("Earnings");
        fragmentEarningSimpleLIstBinding.header.imageView3.setVisibility(View.INVISIBLE);
        fragmentEarningSimpleLIstBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch (IllegalStateException ex) {

                } catch (Exception ex) {

                }
            }
        });

        fragmentEarningSimpleLIstBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return fragmentEarningSimpleLIstBinding.getRoot();
    }


    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    private void getEarnings(String from, String to) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                ProgressDialog dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                EarningRequest requestDto = new EarningRequest(LoggedInUser.getInstance().driverId, from, to);
                Gson gson = new Gson();
                String request = gson.toJson(requestDto);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentadispatcherURL);
                RetrofitClient.getAPIClient().getDriverEarnings(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<EarningAPIResponse>() {
                            @Override
                            public void onSuccess(EarningAPIResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {

                                    Log.d("earningAPICall", "success " + response.toString());
                                    if (response.data.size() > 0) {
                                        Log.d("earningAPICall", "record size " + response.data.size());
                                        earnings.clear();
                                        double totalEarning=0;
                                        for (EarningAPIResponse.Earning earning : response.data) {
                                            totalEarning=totalEarning+earning.earningValue;
                                            try {
                                                String date = earning.createDate;
                                                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                                                Date newDate = spf.parse(date);
                                                spf = new SimpleDateFormat("dd-MM-yyyy");
                                                date = spf.format(newDate);
                                                System.out.println(date);
                                                earnings.add(new EarningDto(date, "$" + earning.earningValue));
                                            } catch (Exception e) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                Log.d("earningAPICall", "parsing failed " + e.toString());
                                            }
                                        }
                                        fragmentEarningSimpleLIstBinding.recyclerView.setVisibility(View.VISIBLE);
                                        EarningSimpleAdapter adapter = new EarningSimpleAdapter(earnings);
                                        fragmentEarningSimpleLIstBinding.recyclerView.setHasFixedSize(true);
                                        fragmentEarningSimpleLIstBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        fragmentEarningSimpleLIstBinding.recyclerView.setAdapter(adapter);
                                        DecimalFormat numberFormat = new DecimalFormat("#.00");
                                        String roundedtotalEarning=numberFormat.format(totalEarning);
                                        fragmentEarningSimpleLIstBinding.txtAmount.setText("$"+roundedtotalEarning);
                                    } else {
                                        fragmentEarningSimpleLIstBinding.recyclerView.setVisibility(View.INVISIBLE);
                                        fragmentEarningSimpleLIstBinding.txtAmount.setText("$0.0");
                                        Toast.makeText(getActivity().getApplicationContext(), "No earnings", Toast.LENGTH_SHORT).show();

                                    }

                                } else {
                                    Log.d("earningAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), "Not earnings", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getActivity().getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("earningAPICall", "failed " + e.toString());
        }
    }
}