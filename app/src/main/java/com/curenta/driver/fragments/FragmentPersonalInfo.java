package com.curenta.driver.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentPersonalInfoBinding;
import com.curenta.driver.dto.Regex;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.utilities.ImageConverter;
import com.curenta.driver.utilities.JsonLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FragmentPersonalInfo extends Fragment implements AdapterView.OnItemSelectedListener {
    FragmentPersonalInfoBinding fragmentPersonalInfoBinding;
    String heading = "Required Steps";
    Calendar myCalendar;
    HashMap<String, ArrayList<String>> states_hashmap = new HashMap<>();
    ArrayList<String> states_list = new ArrayList<>();
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPersonalInfoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_personal_info, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        ((LoginActivity) getActivity()).makeOrignalBackground();
        myCalendar = Calendar.getInstance();
        initGenderDropdown();
        processCities();
        if(UserInfo.getInstance().profilePic!=null){
            fragmentPersonalInfoBinding.imgProfilePic.setImageBitmap(UserInfo.getInstance().profilePic);
            checkCompletion(false);
        }
        else if(UserInfo.getInstance().imageURL!=null){
            Glide.with(this).load(UserInfo.getInstance().imageURL).centerCrop().into(fragmentPersonalInfoBinding.imgProfilePic);
        }
        fragmentPersonalInfoBinding.Birthday.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkCompletion(false);
            }
        });
        fragmentPersonalInfoBinding.edtStreet.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkCompletion(false);
            }
        });
        fragmentPersonalInfoBinding.edtZipCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkCompletion(false);
            }
        });
        fragmentPersonalInfoBinding.edtCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkCompletion(false);
            }
        });
        fragmentPersonalInfoBinding.imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPictureSelection fragmentPictureSelection = new FragmentPictureSelection();
                fragmentPictureSelection.pictureType = EnumPictureType.PROFILEPIC;
                ((LoginActivity) getActivity()).showFragment(fragmentPictureSelection);
            }
        });
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        fragmentPersonalInfoBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCompletion(true);
                if (UserInfo.getInstance().isPersonalInfoCompleted) {
                    try {
                        if (getActivity().getSupportFragmentManager() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } catch(IllegalStateException ex) {

                    }
                    catch(Exception ex) {

                    }
                } else {
                   // Toast.makeText(getContext(), "Please fill all information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentPersonalInfoBinding.Birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return fragmentPersonalInfoBinding.getRoot();
    }

    private void processCities() {
        states_list.clear();
        states_hashmap.clear();
        try {
            JSONArray data = new JSONArray(JsonLoader.loadJSONFromAsset(getActivity(), "us_states_and_cities.json"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject state = data.getJSONObject(i);
                String state_name = state.getString("state");
                states_list.add(state_name);
                JSONArray citiesArray = state.getJSONArray("cities");
                ArrayList<String> cities = new ArrayList<>();
                for (int c = 0; c < citiesArray.length(); c++) {
                    cities.add((String) citiesArray.get(c));
                }
                states_hashmap.put(state_name, cities);

            }
            initStatesDropdown();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void initImageView() {
//        Bitmap mbitmap=((BitmapDrawable) getResources().getDrawable(R.drawable.camera_icon)).getBitmap();
//        Bitmap roundeImage= ImageConverter.roundeCornerBitimap(mbitmap);
//         fragmentPersonalInfoBinding.imgProfilePic.setImageBitmap(roundeImage);
//    }


    private void initGenderDropdown() {
        // Spinner click listener
        fragmentPersonalInfoBinding.gender.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");
        categories.add("Other");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem_main, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        fragmentPersonalInfoBinding.gender.setAdapter(dataAdapter);
    }

    private void initStatesDropdown() {
        // Spinner click listener
        fragmentPersonalInfoBinding.edtState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
               // initCitiesDropdown(item);
                UserInfo.getInstance().state = item;
                checkCompletion(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem_main, states_list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        fragmentPersonalInfoBinding.edtState.setAdapter(dataAdapter);
    }

//    private void initCitiesDropdown(String state) {
//        // Spinner click listener
//        fragmentPersonalInfoBinding.edtCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                UserInfo.getInstance().city = item;
//                checkCompletion(false);
//            }
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),R.layout.spinneritem_main, states_hashmap.get(state));
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
//
//        // attaching data adapter to spinner
//        fragmentPersonalInfoBinding.edtCity.setAdapter(dataAdapter);
//    }

    private void updateLabel() {
        String myFormat = "MM-dd-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        UserInfo.getInstance().DOB = sdf.format(myCalendar.getTime());
        fragmentPersonalInfoBinding.Birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        UserInfo.getInstance().gender = item;
        checkCompletion(false);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void checkCompletion(boolean completeCheck) {
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.DOB = fragmentPersonalInfoBinding.Birthday.getText().toString();
        userInfo.street = fragmentPersonalInfoBinding.edtStreet.getText().toString();
        userInfo.zipcode = fragmentPersonalInfoBinding.edtZipCode.getText().toString();
        userInfo.city = fragmentPersonalInfoBinding.edtCity.getText().toString();
        if ((userInfo.profilePic!=null || userInfo.imageURL!=null ) & userInfo.DOB != null && userInfo.gender != null && userInfo.street != null && userInfo.city != null && userInfo.state != null && userInfo.zipcode != null && !userInfo.DOB.equalsIgnoreCase("") && !userInfo.gender.equalsIgnoreCase("") && !userInfo.street.equalsIgnoreCase("") && !userInfo.city.equalsIgnoreCase("") && !userInfo.state.equalsIgnoreCase("") && !userInfo.zipcode.equalsIgnoreCase("") && Regex.isValid(fragmentPersonalInfoBinding.edtZipCode.getText().toString(), Regex.zipCode) && !userInfo.city.equalsIgnoreCase("")) {
            fragmentPersonalInfoBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentPersonalInfoBinding.imgNext.setEnabled(true);
            if(completeCheck) {
                UserInfo.getInstance().isPersonalInfoCompleted = true;
            }
        } else {

            fragmentPersonalInfoBinding.imgNext.setEnabled(true);
            if(completeCheck) {
                if(fragmentPersonalInfoBinding.edtStreet.getText().toString().equalsIgnoreCase("")){
                    fragmentPersonalInfoBinding.txtStreetError.setText("Invalid street");
                }
                else{
                    fragmentPersonalInfoBinding.txtStreetError.setText("");
                }
                if(fragmentPersonalInfoBinding.edtZipCode.getText().toString().equalsIgnoreCase("")){
                    fragmentPersonalInfoBinding.txtZipCodeError.setText("Invalid zip code");
                }
//                else if(Regex.isValid(fragmentPersonalInfoBinding.edtZipCode.getText().toString(), Regex.zipCode)){
//                    fragmentPersonalInfoBinding.txtZipCodeError.setText("Invalid zip code");
//                }
                else{
                    fragmentPersonalInfoBinding.txtZipCodeError.setText("");
                }
                if(fragmentPersonalInfoBinding.edtCity.getText().toString().equalsIgnoreCase("")){
                    fragmentPersonalInfoBinding.txtCityError.setText("Invalid city");
                }
                else{
                    fragmentPersonalInfoBinding.txtCityError.setText("");
                }
                if(fragmentPersonalInfoBinding.Birthday.getText().toString().equalsIgnoreCase("")){
                    fragmentPersonalInfoBinding.txtDOBError.setText("Invalid date of birth");
                }
                else{
                    fragmentPersonalInfoBinding.txtDOBError.setText("");
                }
                UserInfo.getInstance().isPersonalInfoCompleted = false;
            }
            fragmentPersonalInfoBinding.imgNext.setImageResource(R.drawable.next_icon);

        }
    }


}