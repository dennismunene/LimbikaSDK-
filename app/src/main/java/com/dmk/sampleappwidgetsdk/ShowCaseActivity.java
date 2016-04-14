package com.dmk.sampleappwidgetsdk;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.dmk.limbikasdk.views.LimbikaView;


/**
 * Created by DENNOH on 3/21/2016.
 */
public class ShowCaseActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();
    int TYPE = 0;



    LimbikaView limbikaView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcase);



        limbikaView =new LimbikaView(this);
        limbikaView.setKey("txt");
        limbikaView.setEnabled(false);//disable view
        limbikaView.onResume();

        addContentView(limbikaView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        LimbikaView  limbikaView1 =new LimbikaView(this);

        limbikaView1.setKey("circ_txt");
        limbikaView1.setEnabled(false);//disable view
        limbikaView1.onResume();

        addContentView(limbikaView1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LimbikaView limbikaView2 =new LimbikaView(this);

        limbikaView2.setKey("img");
        limbikaView2.setEnabled(false);//disable view
        limbikaView2.onResume();


        //to get view from database
         Bitmap savedBitmap =   limbikaView.getSavedBitmap();

        addContentView(limbikaView2, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));




    }

}
