package com.curenta.driver.utilities;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by Faheem on 2/2/2021.
 */
public class FragmentUtils {
    private static FragmentUtils instance;
    private AppCompatActivity activity;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public static FragmentUtils getInstance( ){
        if (instance == null) {
            instance = new FragmentUtils();
        }


        return instance;
    }

    private FragmentUtils() {
    }

    public synchronized void addFragment(Context act, Fragment fragment, int fragment_container) {
        this.activity = (AppCompatActivity) act;
        try {
            if (activity!=null) {
                fragmentManager = activity.getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount()>0){
                  Fragment frag =   getCurrentFragment();


                    if( (fragment!=null)){
                        if(((frag!=null))&&(frag.getClass().equals(fragment.getClass()))){
//                            Toast.makeText(act, "Already Open", Toast.LENGTH_SHORT).show();

                        }else{
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                            fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                            fragmentTransaction.commitAllowingStateLoss();
                        }
                    }

                }else{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                    fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
           e.printStackTrace();

        }
    }
    public synchronized void addFragment(Activity act, Fragment fragment, int fragment_container, boolean addToBackstack) {
        this.activity = (AppCompatActivity) act;
        try {
            if (activity!=null) {
                fragmentManager = activity.getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount()>0){
                    Fragment frag =   getCurrentFragment();


                    if( (fragment!=null)){
                        if(((frag!=null))&&(frag.getClass().equals(fragment.getClass()))){
//                            Toast.makeText(act, "Already Open", Toast.LENGTH_SHORT).show();

                        }else{
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                            if (addToBackstack) {
                                fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                            }
                            fragmentTransaction.commitAllowingStateLoss();
                        }
                    }

                }else{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                    if (addToBackstack) {
                        fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
          e.printStackTrace();

        }
    }

    private FragmentUtils(Activity act, Fragment fragment, int fragment_container , boolean addTobackStack) {
        this.activity = (AppCompatActivity) act;



        try {
            if (activity!=null) {
                fragmentManager = activity.getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount()>0){
                    Fragment frag =   getCurrentFragment();


                    if( (fragment!=null)){
                        if(((frag!=null))&&(frag.getClass().equals(fragment.getClass()))){
//                            Toast.makeText(act, "Already Open", Toast.LENGTH_SHORT).show();

                        }else{
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                            if(addTobackStack){
                                fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                            }
                            fragmentTransaction.commitAllowingStateLoss();
                        }
                    }

                }else{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(fragment_container, fragment , Integer.toString(getFragmentCount()));
                    if(addTobackStack){
                        fragmentTransaction.addToBackStack(Integer.toString(getFragmentCount()));
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
        e.printStackTrace();

        }
    }

    private FragmentUtils(AppCompatActivity activity){
        this.activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
    }

    public Fragment getFragmentAt(int index) {
        return fragmentManager.getBackStackEntryCount() > 0 ? fragmentManager.findFragmentByTag(Integer.toString(index)) : null;
    }

    protected Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }


    protected int getFragmentCount() {
        return activity.getSupportFragmentManager().getBackStackEntryCount();
    }
}
