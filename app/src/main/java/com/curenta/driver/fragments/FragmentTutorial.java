package com.curenta.driver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.ViewPagerAdapter;
import com.curenta.driver.databinding.FragmentTutorialBinding;
import com.curenta.driver.utilities.Preferences;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentTutorial extends Fragment {
    FragmentTutorialBinding fragmentTutorialBinding;
    private int dotscount;
    private ImageView[] dots;
    int currentPosition = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTutorialBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_tutorial, container, false);
        ((LoginActivity) getActivity()).makeWhiteBackground();
        ((LoginActivity) getActivity()).moveToTop();
        ((LoginActivity) getActivity()).hideHeader();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (width*0.8), (int) (height * 0.85));
        fragmentTutorialBinding.viewPager.setLayoutParams(layoutParams);
        fragmentTutorialBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition == dotscount - 1) {
                    Preferences.getInstance().saveBoolean("tutorialShown", true);
                    Intent nextIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(nextIntent);
                    getActivity().finish();
                }
                else{
                    fragmentTutorialBinding.viewPager.setCurrentItem(fragmentTutorialBinding.viewPager.getCurrentItem() + 1, true);
                }

            }
        });
        fragmentTutorialBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance().saveBoolean("tutorialShown", true);
                Intent nextIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(nextIntent);
                getActivity().finish();

            }
        });
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext());

        fragmentTutorialBinding.viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            fragmentTutorialBinding.SliderDots.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));

        fragmentTutorialBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
                }
                currentPosition=position;
                if (position == dotscount - 1) {
                    fragmentTutorialBinding.btnNext.setText("Done");
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return fragmentTutorialBinding.getRoot();
    }


}
