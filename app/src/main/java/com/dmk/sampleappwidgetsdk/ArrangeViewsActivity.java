package com.dmk.sampleappwidgetsdk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.dmk.limbikasdk.views.LimbikaView;

/**
 * Created by DENNOH on 4/6/2016.
 */
public class ArrangeViewsActivity extends AppCompatActivity {

    //parent Framelayout
    FrameLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.arrange_activity);

         parent = (FrameLayout)findViewById(R.id.parentFrame);

        LimbikaView emptyBlock = new LimbikaView(getApplicationContext());
        emptyBlock.setKey("txt");
        emptyBlock.setText("This is a blue whale");
        emptyBlock.onResume();//restore view
        parent.addView(emptyBlock);

        LimbikaView imageBlock = new LimbikaView(getApplicationContext());
        imageBlock.setKey("img");
        imageBlock.setImage(R.drawable.ic_cat);
        emptyBlock.onResume();//restore view

        parent.addView(imageBlock);

        LimbikaView circle = new LimbikaView(getApplicationContext());
        circle.setCircleView(true);
        circle.setCircleColor(Color.DKGRAY);
        circle.setKey("circ_txt2");
        circle.setText("Text Here");
        circle.setTextColor(Color.WHITE);
        circle.onResume();//restore view
        parent.addView(circle);


        LimbikaView circleImage = new LimbikaView(getApplicationContext());
        circleImage.setCircleView(true);
        circleImage.setImage(R.drawable.ic_cat);
        circleImage.setKey("circ_img");
        parent.addView(circleImage);

        Button newWidget = (Button)findViewById(R.id.btn_new_widget);

        newWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   showChooserDialog();
            }
        });
    }

    private void showChooserDialog() {

        String widgetTypes[] = new String [] {"Empty Block","Block with Image","Block with Text","Empty Circle","Circle with Image","Circle with text"};

        AlertDialog.Builder dl = new AlertDialog.Builder(this);
        dl.setItems(widgetTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                createWidget(which);
            }

            private void createWidget(int which) {


               switch (which){
                   case 0:
                       LimbikaView emptyBlock = new LimbikaView(getApplicationContext());
                       emptyBlock.setKey("empty");
                       emptyBlock.onResume();//restore view
                       emptyBlock.setBorderColor(Color.BLUE);
                       parent.addView(emptyBlock);

                       break;
                   case 1:
                       LimbikaView imageBlock = new LimbikaView(getApplicationContext());
                       imageBlock.setKey("img");
                       imageBlock.setImage(R.drawable.ic_cat);
                       parent.addView(imageBlock);
                       break;

                   case 2:
                       LimbikaView textBlock = new LimbikaView(getApplicationContext());
                       textBlock.setText("Text Here");
                       textBlock.setKey("txt");
                       textBlock.onResume();//restore view

                       parent.addView(textBlock);
                       break;
                   case 3:
                       LimbikaView circle = new LimbikaView(getApplicationContext());
                       circle.setCircleView(true);
                       circle.setCircleColor(Color.BLACK);
                       circle.setKey("circ");
                       parent.addView(circle);
                       break;
                   case 4:
                       LimbikaView circleImage = new LimbikaView(getApplicationContext());
                       circleImage.setCircleView(true);
                       circleImage.setImage(R.drawable.ic_cat);
                       circleImage.setKey("circ_img");
                       parent.addView(circleImage);

                       break;
                   case 5:
                       LimbikaView circleText = new LimbikaView(getApplicationContext());
                       circleText.setCircleView(true);
                       circleText.setKey("circ_txt");
                       circleText.setText("Text Here");
                       parent.addView(circleText);
                       break;
               }




            }
        });
        dl.show();
    }
}
