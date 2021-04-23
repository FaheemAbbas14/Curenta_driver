package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentBankDetailsBinding;
import com.curenta.driver.dto.Regex;
import com.curenta.driver.dto.UserInfo;


public class FragmentBankDetails extends Fragment {

    FragmentBankDetailsBinding fragmentBankDetailsBinding;
    String heading = "Required Steps";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentBankDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_bank_details, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();

        fragmentBankDetailsBinding.edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String bankName = fragmentBankDetailsBinding.edtName.getText().toString();
                    UserInfo.getInstance().bankACName = bankName;
                    if (!bankName.equalsIgnoreCase("")) {
                        fragmentBankDetailsBinding.txtNameError.setText("");

                    }
                    checkComplete(false);
                }
            }
        });
        fragmentBankDetailsBinding.edtName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String bankName = fragmentBankDetailsBinding.edtName.getText().toString();
                UserInfo.getInstance().bankACName = bankName;
                checkComplete(false);
            }
        });
        fragmentBankDetailsBinding.edtACNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String accountNumber = fragmentBankDetailsBinding.edtACNumber.getText().toString();
                    UserInfo.getInstance().bankACNumber = accountNumber;
                    if (!accountNumber.equalsIgnoreCase("")) {
                        fragmentBankDetailsBinding.txtACNumberError.setText("");

                    }
                    else if(!Regex.isValid(fragmentBankDetailsBinding.edtACNumber.getText().toString(), Regex.accountNumber)){
                        fragmentBankDetailsBinding.txtRutNumberError.setText(getResources().getString(R.string.error_invalid_acnumber));
                    }
                    checkComplete(false);
                }
            }
        });
        fragmentBankDetailsBinding.edtACNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentBankDetailsBinding.edtACNumber.getText().toString();
                UserInfo.getInstance().bankACNumber = value;
                checkComplete(false);
            }
        });
        fragmentBankDetailsBinding.edtRutNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String routingNumber = fragmentBankDetailsBinding.edtRutNumber.getText().toString();
                    UserInfo.getInstance().bankRTNumber = routingNumber;
                    if (!routingNumber.equalsIgnoreCase("")) {
                        fragmentBankDetailsBinding.txtRutNumberError.setText("");
                    }
                    else if(!Regex.isValid(fragmentBankDetailsBinding.edtRutNumber.getText().toString(), Regex.bankRoutingNumber)){
                        fragmentBankDetailsBinding.txtRutNumberError.setText(getResources().getString(R.string.error_invalid_rtnumber));
                    }
                    checkComplete(false);
                }
            }
        });
        fragmentBankDetailsBinding.edtRutNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentBankDetailsBinding.edtRutNumber.getText().toString();
                UserInfo.getInstance().bankRTNumber = value;
                checkComplete(false);
            }
        });
        fragmentBankDetailsBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bankName = fragmentBankDetailsBinding.edtName.getText().toString();
                String ACNumber = fragmentBankDetailsBinding.edtACNumber.getText().toString();
                String RTNumber = fragmentBankDetailsBinding.edtRutNumber.getText().toString();
                if (bankName.equalsIgnoreCase("")) {
                    fragmentBankDetailsBinding.txtNameError.setText(getResources().getString(R.string.error_invalid_bankName));
                } else {
                    UserInfo.getInstance().bankRTNumber = bankName;
                    fragmentBankDetailsBinding.txtNameError.setText("");
                }
                if (ACNumber.equalsIgnoreCase("") || !Regex.isValid(fragmentBankDetailsBinding.edtACNumber.getText().toString(), Regex.accountNumber)) {
                    fragmentBankDetailsBinding.txtACNumberError.setText(getResources().getString(R.string.error_invalid_acnumber));
                } else {
                    UserInfo.getInstance().bankRTNumber = ACNumber;
                    fragmentBankDetailsBinding.txtACNumberError.setText("");
                }
                if (RTNumber.equalsIgnoreCase("") || !Regex.isValid(fragmentBankDetailsBinding.edtRutNumber.getText().toString(), Regex.bankRoutingNumber)) {
                    fragmentBankDetailsBinding.txtRutNumberError.setText(getResources().getString(R.string.error_invalid_rtnumber));
                    return;
                } else {
                    UserInfo.getInstance().bankRTNumber = RTNumber;
                    fragmentBankDetailsBinding.txtRutNumberError.setText("");
                }

                checkComplete(true);
                if (UserInfo.getInstance().isBankDetailsCompleted) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        return fragmentBankDetailsBinding.getRoot();
    }
    private void checkComplete(boolean check) {
        UserInfo userInfo = UserInfo.getInstance();
        if (!userInfo.bankACName.equalsIgnoreCase("") && !userInfo.bankACNumber.equalsIgnoreCase("") && !userInfo.bankRTNumber.equalsIgnoreCase("")) {
            fragmentBankDetailsBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentBankDetailsBinding.imgNext.setEnabled(true);
            if(check) {
                UserInfo.getInstance().isBankDetailsCompleted = true;
            }
        } else {
            fragmentBankDetailsBinding.imgNext.setEnabled(true);

            if(check) {
                UserInfo.getInstance().isBankDetailsCompleted = false;
            }
            fragmentBankDetailsBinding.imgNext.setImageResource(R.drawable.next_icon);
        }
    }
}