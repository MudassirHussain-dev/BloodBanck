package com.aampower.bloodbank.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.aampower.bloodbank.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    PhotoView imgProfile;

    Toolbar fullToolBar;
    Activity context;

    String accName, imgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = FullImageActivity.this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_image);
        fullToolBar = findViewById(R.id.fullToolBar);
        imgProfile = findViewById(R.id.imgProfile);

        setSupportActionBar(fullToolBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = context.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(context, R.color.black));

            Fade fade = new Fade();
            View decor = getWindow().getDecorView();
            fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }


        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            accName = bundle.getString("accName");
            imgURL = bundle.getString("imgURL");

            if (getSupportActionBar() != null) {

                setTitle(accName);

                Picasso.get().load(imgURL).into(imgProfile);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();

        }

        return true;

    }
}
