package com.curenta.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentChatBinding;
import com.curenta.driver.dto.LoggedInUser;


public class ChatFragment extends Fragment {

    FragmentChatBinding fragmentChatBinding;

    public void onViewCreated(View view, Bundle savedInstanceState) {


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_chat, container, false);

        fragmentChatBinding.txtLabel.setText(LoggedInUser.getInstance().fname + " " + LoggedInUser.getInstance().lname);

        fragmentChatBinding.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return fragmentChatBinding.getRoot();
    }


}