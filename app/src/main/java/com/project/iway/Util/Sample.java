package com.project.iway.Util;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.project.iway.Activity.MainActivity;
import com.project.iway.R;

public class Sample extends IntroActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setButtonCtaVisible(true);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_BACKGROUND);
        TypefaceSpan labelSpan = new TypefaceSpan(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? "sans-serif-medium" : "sans serif");
        SpannableString label = SpannableString
                .valueOf(getString(R.string.label_button_cta_canteen_intro));
        label.setSpan(labelSpan, 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setButtonCtaLabel(label);
        setButtonCtaClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Sample.this, "Hi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Sample.this, MainActivity.class));
            }
        });

        setPageScrollDuration(500);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPageScrollInterpolator(android.R.interpolator.fast_out_slow_in);
        }

        addSlide(new SimpleSlide.Builder()
                .title("Welcome!")
                .description("The safety of everybody is important in the midst of an emergency.")
                .image(R.drawable.a)
                .background(R.color.white)
                .backgroundDark(R.color.black)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Conveniently Report Road Emergencies")
                .description("Using this mobile application, users will experience conducive ways of reporting of road emergencies.")
                .image(R.drawable.b)
                .background(R.color.white)
                .backgroundDark(R.color.black)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("iWay’s Unique Features")
                .description("This mobile application is equipped with two unique features:  the Geotagging and Crowdsourcing ")
                .image(R.drawable.c)
                .background(R.color.white)
                .backgroundDark(R.color.black)
                .build());

        autoplay(2500, INFINITE);
    }


}
