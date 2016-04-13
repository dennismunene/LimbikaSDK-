package com.dmk.sampleappwidgetsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int BLOCK =100;
    public static final int BLOCKIMAGE =200;
    public static final int BLOCKTEXT =300;
    public static final int BLOCKCIRCLE =400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //block with nothing
        findViewById(R.id.block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ArrangeViewsActivity.class));
            }
        });

        //block with image
        findViewById(R.id.blockimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShowCaseActivity.class));
            }
        });

    }




}
