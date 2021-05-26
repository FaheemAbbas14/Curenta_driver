package com.curenta.driver.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.ImageAdapter;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentConfirmDeliveryBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.ImageModel;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.ConfirmDeliveryResponse;
import com.curenta.driver.retrofit.apiDTO.ConfirmOrderResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Utility;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class FragmentConfirmDelivery extends Fragment {

    FragmentConfirmDeliveryBinding fragmentConfirmDeliveryBinding;
    ArrayList<Bitmap> images;
    ArrayList<Uri> imagesURIs;
    ArrayList<ImageModel> arrayList;
    private static final int CAPTURE_PICCODE = 989;
    Bitmap bitmap;
    Uri imageUri;
    ProgressDialog dialog;
    public EnumPictureType enumPictureType;
    public String routeId;
    public RideDetailListAdapter.Order order;
    public ArrayList<RideDetailListAdapter.Section> sections = new ArrayList<>();
    public int index = 0;
    ImageAdapter adpter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentConfirmDeliveryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_confirm_delivery, container, false);
        arrayList = new ArrayList<>();
        images=new ArrayList<>();
        imagesURIs=new ArrayList<>();
        setDataAdopter();
        if (enumPictureType == EnumPictureType.ORDER_PICKUP) {
            fragmentConfirmDeliveryBinding.txtLabel2.setText("Please take a photo for orders, which confirm that you received orders");
        } else if (enumPictureType == EnumPictureType.ORDER_DELIVER) {
            fragmentConfirmDeliveryBinding.txtLabel2.setText("Once our Client received medication Please take Photo of receipt and continue your way to the rest of the Clients");
        } else if (enumPictureType == EnumPictureType.ORDER_COMPLETED) {
            fragmentConfirmDeliveryBinding.txtLabel2.setText("Once our Client received medication Please take Photo of receipt and continue your way to the rest of the Clients");
        }
        fragmentConfirmDeliveryBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (enumPictureType == EnumPictureType.ORDER_PICKUP) {
                    confirmPickup();

                } else {
                    RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                    confirmDelivery();
                }
            }
        });

        //item click listner
        fragmentConfirmDeliveryBinding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                boolean isClickable = arrayList.get(position).isClickable();
                if (isClickable) {
                    cameraIntent();
                }
            }
        });
        return fragmentConfirmDeliveryBinding.getRoot();
    }

    private void setDataAdopter() {
        Bitmap myLogo = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.addphoto);
        arrayList.clear();
        for (int i = 0; i < images.size(); i++) {
            ImageModel imagemodel = new ImageModel();
            imagemodel.setmThumbIds(images.get(i));
            imagemodel.setClickable(false);
            //add in array list
            arrayList.add(imagemodel);
        }
        ImageModel imagemodel = new ImageModel();
        imagemodel.setmThumbIds(myLogo);
        imagemodel.setClickable(true);
        //add in array list
        arrayList.add(imagemodel);
        adpter = new ImageAdapter(getContext(), arrayList);
        fragmentConfirmDeliveryBinding.gridview.setAdapter(adpter);
        adpter.notifyDataSetChanged();
        if (images.size()>0) {
            fragmentConfirmDeliveryBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);
            fragmentConfirmDeliveryBinding.btnSubmit.setEnabled(true);
        } else {
            fragmentConfirmDeliveryBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);
            fragmentConfirmDeliveryBinding.btnSubmit.setEnabled(false);
        }
    }

    private void cameraIntent() {
        boolean result = Utility.checkPermission(getContext());
        if (result) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,
                    CAPTURE_PICCODE);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_PICCODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    // imageUri = getImageUri(getContext(), bmp);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                            byteArray.length);

                    File directory = AppElement.cw.getDir("imageDir", Context.MODE_PRIVATE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "picture_" + timeStamp + ".jpg";
                    File file = new File(directory, imageFileName);
                    imageUri = Uri.fromFile(file);
                    if (!file.exists()) {
                        Log.d("path", file.toString());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //add in array list
                    images.add(bitmap);
                    imagesURIs.add(imageUri);
                    setDataAdopter();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(getContext(), "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                }
                break;
        }
    }
    public void confirmDelivery() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                LoggedInUser user = LoggedInUser.getInstance();

                RequestBody routeStepId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + order.routeStepId);
                RequestBody routeID = RequestBody.create(MediaType.parse("text/plain"),
                        "" + routeId);
//                RequestBody orderId = RequestBody.create(MediaType.parse("text/plain"),
//                        "" + order.orderId);
                Log.d("deliveryAPICall", " routeID " + routeId + " routeStepId " + order.routeStepId);
                MultipartBody.Part[] pics =new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("ConfirmOrderImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i]=ConfirmDeliveryImage;
                }

                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().confirmDelivery(pics, routeID, routeStepId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ConfirmOrderResponse>() {
                            @Override
                            public void onSuccess(ConfirmOrderResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    Log.d("deliveryAPICall", "success " + response.toString());
                                    //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();
                                    sections.get(0).items.get(index).isCompleted = true;
                                    Log.d("deliveryAPICall", "index " + index + " size " + (sections.get(0).items.size() - 1));
                                    if (index < sections.get(0).items.size() - 1) {
                                        sections.get(0).items.get(index + 1).isFocused = true;
                                    } else {
                                        fragmentThankYouAction.isCompleted = true;
                                        enumPictureType = EnumPictureType.ORDER_COMPLETED;
                                    }
                                    if (enumPictureType == EnumPictureType.ORDER_PICKUP) {

                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_PICKUP;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    } else if (enumPictureType == EnumPictureType.ORDER_DELIVER) {

                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_DELIVER;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    } else if (enumPictureType == EnumPictureType.ORDER_COMPLETED) {
                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    }
                                } else {
                                    if (response.responseMessage.equalsIgnoreCase("no order found")) {
                                        ((DashboardActivity) getActivity()).cancelNotification(1);
                                    } else if (response.responseMessage.equalsIgnoreCase("Route does not exist")) {
                                        ((DashboardActivity) getActivity()).cancelNotification(2);
                                    } else {
                                        Log.d("deliveryAPICall", "fail " + response.toString());
                                        Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("deliveryAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("deliveryAPICall", "failed " + e.toString());
        }
    }

    public void confirmPickup() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                LoggedInUser user = LoggedInUser.getInstance();
                MultipartBody.Part[] pics =new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("pickupProofImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i]=ConfirmDeliveryImage;
                }

                RequestBody routeID = RequestBody.create(MediaType.parse("text/plain"),
                        "" + routeId);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().orderPickupWithImage(pics, routeID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ConfirmDeliveryResponse>() {
                            @Override
                            public void onSuccess(ConfirmDeliveryResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    Log.d("pickupAPICall", "success " + response.toString());
                                    //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();
                                    sections.get(0).items.get(index).isCompleted = true;
                                    if (index < sections.get(0).items.size() - 1) {
                                        sections.get(0).items.get(index + 1).isFocused = true;
                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_PICKUP;
                                    } else {
                                        fragmentThankYouAction.isCompleted = true;
                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                                    }
                                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);


                                } else {
                                    Log.d("pickupAPICall", "fail " + response.toString());
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("pickupAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("pickupAPICall", "failed " + e.toString());
        }
    }

}