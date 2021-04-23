package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentFormsBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.UserInfo;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class FragmentForms extends Fragment  {
    FragmentFormsBinding fragmentFormsBinding;
    boolean isAllComplete = false;
    String heading = "Forms";
    ProgressDialog dialog;
UserInfo userInfo;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFormsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_forms, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        userInfo=UserInfo.getInstance();

        fragmentFormsBinding.llyW9Forms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final PdfActivityConfiguration config = new PdfActivityConfiguration.Builder(getContext()).build();
//                PdfActivity.showDocument(getContext(), userInfo.w9Uri, config);
                FragmentViewForm fragmentViewForm=new FragmentViewForm();
                fragmentViewForm.heading="W9 Form";
                fragmentViewForm.formUri=userInfo.w9Uri;
                ((LoginActivity) getActivity()).showFragment(fragmentViewForm);
                userInfo.isw9Completed = true;
                checkFormCompletion();
                checkComplete();

            }
        });
        fragmentFormsBinding.llyI9Forms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final PdfActivityConfiguration config = new PdfActivityConfiguration.Builder(getContext()).build();
//
//                PdfActivity.showDocument(getContext(), userInfo.i9Uri, config);
                FragmentViewForm fragmentViewForm=new FragmentViewForm();
                fragmentViewForm.heading="I9 Form";
                fragmentViewForm.formUri=userInfo.i9Uri;
                ((LoginActivity) getActivity()).showFragment(fragmentViewForm);
                userInfo.isi9Completed = true;
                checkFormCompletion();
                checkComplete();
            }
        });

        fragmentFormsBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        ((LoginActivity) getActivity()).moveToTop();
        checkComplete();
        checkFormCompletion();
        copyForms();
        return fragmentFormsBinding.getRoot();
    }

    private void copyForms() {

        File directory = AppElement.cw.getDir("fileDir", Context.MODE_PRIVATE);
        File i9file = new File(directory, "formi9.pdf");
        File w9file = new File(directory, "formw9.pdf");


        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!i9file.exists()) {
           // i9file.mkdirs();
            userInfo.i9Uri=copyFile(getActivity(), i9file, "formi9.pdf");
        } else {
            userInfo.i9Uri = Uri.fromFile(i9file);
        }
        if (!w9file.exists()) {
           // w9file.mkdirs();
            userInfo.w9Uri =copyFile(getActivity(), w9file, "formw9.pdf");
        } else {

            userInfo.w9Uri = Uri.fromFile(w9file);

        }
    }

    private void checkComplete() {

        if (userInfo.isw9Completed && userInfo.isi9Completed) {
            fragmentFormsBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentFormsBinding.imgNext.setEnabled(true);
            isAllComplete = true;
        } else {
            fragmentFormsBinding.imgNext.setEnabled(true);
            fragmentFormsBinding.imgNext.setImageResource(R.drawable.next_icon);
            isAllComplete = false;
        }

    }

    public void checkFormCompletion() {
//    if (UserInfo.getInstance().isw9Completed) {
//        fragmentFormsBinding.imgW9Forms.setImageResource(R.drawable.checked_icon);
//        fragmentFormsBinding.llyW9Forms.setEnabled(false);
//        fragmentFormsBinding.llyW9Forms.setEnabled(false);
//    }
//    if (UserInfo.getInstance().isi9Completed) {
//        fragmentFormsBinding.imgI9Forms.setImageResource(R.drawable.checked_icon);
//        fragmentFormsBinding.llyI9Forms.setEnabled(false);
//        fragmentFormsBinding.llyI9Forms.setEnabled(false);
//    }
    }



    private Uri copyFile(Context context, File file, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream in = assetManager.open(fileName);


            Uri fileUri = Uri.fromFile(file);
            if (!file.exists()) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int read = in.read(buffer);
                    while (read != -1) {
                        fos.write(buffer, 0, read);
                        read = in.read(buffer);
                    }
                    in.close();
                    fos.flush();
                    fos.close();
                    return fileUri;
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}