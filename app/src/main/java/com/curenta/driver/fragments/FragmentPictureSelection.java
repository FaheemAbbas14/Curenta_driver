package com.curenta.driver.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentPictureSelectionBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.utilities.ImageConverter;
import com.curenta.driver.utilities.Utility;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentPictureSelection extends Fragment {
    FragmentPictureSelectionBinding fragmentPictureSelectionBinding;
    String heading = "Required Steps";
    public EnumPictureType pictureType;
    private static final int CAPTURE_PICCODE = 989;
    Bitmap bitmap;
    Uri imageUri;
    boolean isFromCamera;
    ProgressDialog dialog;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPictureSelectionBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_picture_selection, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).makeWhiteBackground();
        ((LoginActivity) getActivity()).moveToTop();
        dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        if (pictureType == EnumPictureType.PROFILEPIC) {
            fragmentPictureSelectionBinding.txtImageDescription.setText("show your whole face and tops of shoulders take your sunglasses and hat off");
        } else if (pictureType == EnumPictureType.DRIVING_LICENSE) {
            fragmentPictureSelectionBinding.txtImageDescription.setText("Make sure your driver's license is readable");
        } else if (pictureType == EnumPictureType.CAR_INSURANCE) {
            fragmentPictureSelectionBinding.txtImageDescription.setText("Make sure your Car Insurance is readable");
        } else if (pictureType == EnumPictureType.CAR_REGISTRATION) {
            fragmentPictureSelectionBinding.txtImageDescription.setText("Make sure your Car Registration is readable");
        }
        fragmentPictureSelectionBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null) {
                    if (pictureType == EnumPictureType.PROFILEPIC) {
                        UserInfo.getInstance().profilePic = bitmap;
                        UserInfo.getInstance().profilePicCamera = isFromCamera;
                        UserInfo.getInstance().profilePicURI = imageUri;
                    } else if (pictureType == EnumPictureType.DRIVING_LICENSE) {
                        UserInfo.getInstance().drivingLicensePic = bitmap;
                        UserInfo.getInstance().drivingLicensePicCamera = isFromCamera;
                        UserInfo.getInstance().drivingLicensePicURI = imageUri;
                    } else if (pictureType == EnumPictureType.CAR_INSURANCE) {
                        UserInfo.getInstance().carInsurancePic = bitmap;
                        UserInfo.getInstance().carInsurancePicCamera = isFromCamera;
                        UserInfo.getInstance().carInsurancePicURI = imageUri;
                    } else if (pictureType == EnumPictureType.CAR_REGISTRATION) {
                        UserInfo.getInstance().carRegistrationPic = bitmap;
                        UserInfo.getInstance().carRegistrationPicCamera = isFromCamera;
                        UserInfo.getInstance().carRegistrationPicURI = imageUri;
                    }
                    FragmentThankYou fragmentThankYou = new FragmentThankYou();
                    fragmentThankYou.pictureType = pictureType;
                    ((LoginActivity) getActivity()).showFragment(fragmentThankYou);

                } else {
                    Toast.makeText(getActivity(), "Please select a image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentPictureSelectionBinding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        fragmentPictureSelectionBinding.btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
//        cameraIntent();
        selectImage();
        return fragmentPictureSelectionBinding.getRoot();
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
                    dialog.show();
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    //imageUri = getImageUri(getContext(), bmp);
                    saveBitmap(bmp);


                }
            } else if (requestCode == 2) {
                dialog.show();
                Uri imageUritemp = data.getData();

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUritemp);
                if (bitmap != null) {
                    saveBitmap(bitmap);


                }


            }
        } catch (Exception e) {
            dialog.dismiss();
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(getContext(), "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmap(Bitmap bmp) {

        DisposableObserver<Object> over = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new Object();
            }
        }).observeOn(Schedulers.io()).subscribeWith(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                            byteArray.length);
                    if (AppElement.cw == null) {
                        AppElement.cw = new ContextWrapper(getActivity());
                    }
                    File directory = AppElement.cw.getDir("imageDir", Context.MODE_PRIVATE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "picture_" + timeStamp + ".jpg";
                    if (pictureType == EnumPictureType.PROFILEPIC) {
                        imageFileName = "profile_" + timeStamp + ".jpg";
                    } else if (pictureType == EnumPictureType.DRIVING_LICENSE) {
                        imageFileName = "drivinglicense_" + timeStamp + ".jpg";
                    } else if (pictureType == EnumPictureType.CAR_INSURANCE) {
                        imageFileName = "carinsurance_" + timeStamp + ".jpg";
                    } else if (pictureType == EnumPictureType.CAR_REGISTRATION) {
                        imageFileName = "carregistration_" + timeStamp + ".jpg";
                    }

                    File file = new File(directory, imageFileName);
                    imageUri = Uri.fromFile(file);
                    if (!file.exists()) {
                        Log.d("path", imageFileName+" created");
                        FileOutputStream fos = null;

                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap circularBitmap = ImageConverter.getCroppedBitmap(bitmap);
                            fragmentPictureSelectionBinding.imageView4.setImageBitmap(circularBitmap);
                        }
                    });
                    dialog.dismiss();
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}