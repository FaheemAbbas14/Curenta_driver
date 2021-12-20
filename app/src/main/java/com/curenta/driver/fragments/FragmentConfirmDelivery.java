package com.curenta.driver.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.curenta.driver.utilities.FileUtils;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String TAG = "ConfirmDelivery";
    private TextRecognizer detector;
    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    private String currentPhotoPath = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentConfirmDeliveryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_confirm_delivery, container, false);
        detector = new TextRecognizer.Builder(getActivity()).build();
        arrayList = new ArrayList<>();
        images = new ArrayList<>();
        imagesURIs = new ArrayList<>();
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
                if (order != null && order.routeStepId != null && routeId != null) {
                    if (enumPictureType == EnumPictureType.ORDER_PICKUP) {
                        confirmPickup();

                    } else {
                        RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                        confirmDelivery();
                    }
                } else {
                    Toast.makeText(getContext(), "Issue while uploading photos.Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //item click listner
        fragmentConfirmDeliveryBinding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                boolean isClickable = arrayList.get(position).isClickable();
                if (isClickable) {
                    selectImage();
                }
            }
        });
        setDataAdopter();
        return fragmentConfirmDeliveryBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        setDataAdopter();
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle("Upload Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from Gallery")) {
                    openImagesDocument();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file;
        try {
            file = getImageFile(); // 1
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Please take another image", Toast.LENGTH_SHORT).show();

            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(getActivity(), R.color.labelblue));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);

//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                checkReadability(bitmap);

            openCropActivity(uri, uri);
        }
    else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
        if (data != null) {
            Uri uri = UCrop.getOutput(data);
            showImage(uri);
        }
    }
        else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), sourceUri);
               // checkReadability(bitmap);
                openCropActivity(sourceUri, destinationUri);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Please select different  picture.", Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);
    }

    private void showImage(Uri imageUri) {
        try {
            File file;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                file = FileUtils.getFile(getActivity(), imageUri);
            } else {
                file = new File(currentPhotoPath);
            }
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            checkReadability(bitmap);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please select different  picture.", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void checkReadability(Bitmap bitmap) {
        try {

            if (detector.isOperational() && bitmap != null) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> textBlocks = detector.detect(frame);
                String blocks = "";
                String lines = "";
                String words = "";
                for (int index = 0; index < textBlocks.size(); index++) {
                    //extract scanned text blocks here
                    TextBlock tBlock = textBlocks.valueAt(index);
                    blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    for (Text line : tBlock.getComponents()) {
                        //extract scanned text lines here
                        lines = lines + line.getValue() + "\n";
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            words = words + element.getValue() + ", ";
                        }
                    }
                }
                if (textBlocks.size() == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Image is not readable , please take another photo!")
                            .setNeutralButton("Ok", null)
                            .setTitle("Blurry Image")
                            .create()
                            .show();
                } else {
                    Toast.makeText(getActivity(), "readable image", Toast.LENGTH_SHORT)
                            .show();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                            byteArray.length);

                    File directory = AppElement.cw.getDir("imageDir", Context.MODE_PRIVATE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "picture_" + timeStamp + ".jpg";
                    File newFile = new File(directory, imageFileName);
                    imageUri = Uri.fromFile(newFile);
                    if (!newFile.exists()) {
                        Log.d("path", newFile.toString());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(newFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //add in array list
                    images.add(bitmap);
                    imagesURIs.add(imageUri);
                    setDataAdopter();
                }
            } else {
                Toast.makeText(getActivity(), "Could not set up the detector!", Toast.LENGTH_SHORT)
                        .show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to load Image", Toast.LENGTH_SHORT)
                    .show();

        }
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
        if (images.size() > 0) {
            fragmentConfirmDeliveryBinding.btnSubmit.setBackgroundResource(R.drawable.blue_rounded);
            fragmentConfirmDeliveryBinding.btnSubmit.setEnabled(true);
        } else {
            fragmentConfirmDeliveryBinding.btnSubmit.setBackgroundResource(R.drawable.grey_rounded);
            fragmentConfirmDeliveryBinding.btnSubmit.setEnabled(false);
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
                MultipartBody.Part[] pics = new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("ConfirmOrderImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i] = ConfirmDeliveryImage;
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
                                        ((DashboardActivity) getActivity()).RideNewNotification(1);
                                    } else if (response.responseMessage.equalsIgnoreCase("Route does not exist")) {
                                        ((DashboardActivity) getActivity()).RideNewNotification(2);
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
                MultipartBody.Part[] pics = new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("pickupProofImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i] = ConfirmDeliveryImage;
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