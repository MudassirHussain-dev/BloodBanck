package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.aampower.bloodbank.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_splash);

      // testing();


        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }



    private void testing(){

        int s = 0, c;                       // s for terms of series, c for counter to generate n terms
        for (c = 1; c <= 5; c++) {
            s = s * 10 + c;
            System.out.print(s + " ");
        }

//        for(int i=1;i<=5;i++){
//            for(int j=1;j<=i;j++){
//                System.out.print(" "+i+" ");
//            }
//            System.out.print("\n");
//        }


//        int n = 5;
//        char c = 'c';
//
//
//        for(int i=1;i<=n;i++)
//        {
//            for(int j=1;j<=n-i;j++)
//
//            {
//                System.out.print(" ");
//            }
//
//            for(int j=1;j<=i*2-1;j++)
//
//            {
//                System.out.print(c);
//            }
//            System.out.println();
//
//        }
//        for(int i=n-1;i>0;i--)
//        {
//            for(int j=1;j<=n-i;j++)
//
//            {
//                System.out.print(" ");
//            }
//            for(int j=1;j<=i*2-1;j++)
//
//            {
//                System.out.print(c);
//            }
//            System.out.println();
//        }

    }


}
