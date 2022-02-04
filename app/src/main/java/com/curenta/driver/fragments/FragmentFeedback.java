package com.curenta.driver.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.ImageAdapter;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentFeedbackBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.ImageModel;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.CrashReportResponse;
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


public class FragmentFeedback extends Fragment {

    FragmentFeedbackBinding fragmentFeedbackBinding;
    ArrayList<Bitmap> images;
    ArrayList<Uri> imagesURIs;
    ArrayList<ImageModel> arrayList;
    private static final int CAPTURE_PICCODE = 989;
    Bitmap bitmap;
    Uri imageUri;
    ProgressDialog dialog;
    public EnumPictureType enumPictureType;
    boolean isFromCamera;
    public RideDetailListAdapter.Order order;
    public ArrayList<RideDetailListAdapter.Section> sections = new ArrayList<>();
    public int index = 0;
    ImageAdapter adpter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFeedbackBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_feedback, container, false);
        arrayList = new ArrayList<>();
        images = new ArrayList<>();
        imagesURIs = new ArrayList<>();
        setDataAdopter();
        fragmentFeedbackBinding.header.txtLabel.setText("Feedback");
        fragmentFeedbackBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
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

        fragmentFeedbackBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = fragmentFeedbackBinding.edtTitle.getText().toString();
                String message = fragmentFeedbackBinding.edtDescription.getText().toString();
                if (!title.equalsIgnoreCase("") && !message.equalsIgnoreCase("")) {
                    sendFeedback(title, message);
                } else {
                    Toast.makeText(getContext(), "Please enter title and description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //item click listner
        fragmentFeedbackBinding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                boolean isClickable = arrayList.get(position).isClickable();
                if (isClickable) {
                    selectImage();
                }
            }
        });
        return fragmentFeedbackBinding.getRoot();
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
        fragmentFeedbackBinding.gridview.setAdapter(adpter);
        adpter.notifyDataSetChanged();

    }

    private void cameraIntent() {
        boolean cameraPermission= Utility.checkCameraPermission(getContext());
        boolean storagePermission= Utility.checkPermission(getContext());
        boolean writePermission= Utility.checkWritePermission(getContext());
        if(cameraPermission && storagePermission && writePermission) {
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
            } else if (requestCode == 2 && data != null) {

                Uri imageUritemp = data.getData();
                if (imageUritemp != null) {
                    Bitmap localbitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUritemp);
                    if (localbitmap != null) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        localbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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


    public void sendFeedback(String titleText, String messageText) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                LoggedInUser user = LoggedInUser.getInstance();
                MultipartBody.Part[] pics = new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("pickupProofImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i] = ConfirmDeliveryImage;
                }

                RequestBody routeID = RequestBody.create(MediaType.parse("text/plain"),
                        "" + AppElement.routeId);
                RequestBody orderId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + AppElement.orderId);
                RequestBody driverId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + LoggedInUser.getInstance().driverId);
                RequestBody title = RequestBody.create(MediaType.parse("text/plain"),
                        titleText);
                RequestBody message = RequestBody.create(MediaType.parse("text/plain"),
                        messageText);
                RequestBody plateform = RequestBody.create(MediaType.parse("text/plain"),
                        "ANDRIOD");
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().sendCrashReport(pics, title, message, driverId, routeID, orderId, plateform)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CrashReportResponse>() {
                            @Override
                            public void onSuccess(CrashReportResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    Log.d("feedbackAPICall", "success " + response.toString());
                                    Toast.makeText(getActivity().getApplicationContext(), "Thank you for submitting feedback", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } else {
                                    Log.d("feedbackAPICall", "fail " + response.toString());
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("feedbackAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("feedbackAPICall", "failed " + e.toString());
        }

    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle("Upload Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    isFromCamera = true;
                    cameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    isFromCamera = false;
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}