package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentBeckgroundCheckBinding;
import com.curenta.driver.databinding.FragmentSocialSecurityBinding;
import com.curenta.driver.dto.Regex;
import com.curenta.driver.dto.UserInfo;


public class FragmentSocialSecurity extends Fragment {
    FragmentSocialSecurityBinding fragmentSocialSecurityBinding;
    String heading = "Required Steps";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSocialSecurityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_social_security, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        fragmentSocialSecurityBinding.edtCode.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {


                    if (s.length() > 3) {
                        if (Character.isDigit(s.charAt(3)))
                            s.insert(3, " - ");
                    }
                    if (s.length() > 8) {
                        if (Character.isDigit(s.charAt(8)))
                            s.insert(8, " - ");
                    }
                    if(s.length()==15 && fragmentSocialSecurityBinding.checkBox.isChecked()){
                        if(Regex.isValid(s.toString(), Regex.socialSecurityNumber)){
                            fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);
                        }
                    }
                    else{
                        fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);
                    }
                }
                else{
                    if(s.length()!=15){
                        fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });

        fragmentSocialSecurityBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String socialCode = fragmentSocialSecurityBinding.edtCode.getText().toString();
                if (!socialCode.equalsIgnoreCase("")  && !socialCode.equalsIgnoreCase("123 - 45 - 6789")   && fragmentSocialSecurityBinding.checkBox.isChecked() && Regex.isValid(socialCode, Regex.socialSecurityNumber)&& socialCode.length()==15) {
                    UserInfo.getInstance().socialSecurityCode = socialCode;
                    fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);
                    UserInfo.getInstance().socialSecurityCode = socialCode;
                    UserInfo.getInstance().isBackgroundCheckCompleted = true;
                    ((LoginActivity) getActivity()).showFragment(new FragmentThankYou());
                } else {
                    if(!fragmentSocialSecurityBinding.checkBox.isChecked()){
                        fragmentSocialSecurityBinding.txtCodeError.setText("Check agree checkbox");
                    }
                    else{
                        fragmentSocialSecurityBinding.txtCodeError.setText("Invalid social security number");
                    }
                    UserInfo.getInstance().isBackgroundCheckCompleted = false;

                    fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);

                }
            }
        });
        fragmentSocialSecurityBinding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                              @Override
                                                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                                  if (isChecked) {
                                                                                      String socialCode = fragmentSocialSecurityBinding.edtCode.getText().toString();
                                                                                      if (!socialCode.equalsIgnoreCase("")) {
                                                                                          fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);

                                                                                      } else {
                                                                                          fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);

                                                                                      }
                                                                                  } else {
                                                                                      fragmentSocialSecurityBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);
                                                                                  }
                                                                              }
                                                                          }
        );
        return fragmentSocialSecurityBinding.getRoot();
    }
}