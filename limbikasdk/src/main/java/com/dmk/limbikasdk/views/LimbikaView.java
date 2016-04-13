package com.dmk.limbikasdk.views;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.dmk.limbikasdk.R;
import com.dmk.limbikasdk.db.Database;


public class LimbikaView extends View {

    public Context context;
    public Bitmap mBitmap = null;
    Point[] points = new Point[4];
    Point point1, point3;
    Point point2, point4;
    Point startMovePoint;
    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    // variable to know what ball is being dragged
    private Paint paint;
    private Canvas canvas;
    private int image = -1;


    private Bitmap imageBitmap = null;
    boolean isCircleView = false;
    //defaults
    int canvasHeight = 256;
    int canvasWidth = 256;
    int rotation = 0;
    float dX, dY;
    //Listeners vars
    DoubleTapListener doubleTapListener;
    RotationListener rotationListener;
    SingleTapListener singleTapListener;
    LongTapListener longTapListener;
    GestureDetector gestureDetector;
    DragListener dragListener;
    Rect viewBounds = null;
    int width, height;
    WindowManager wm;
    int xCenter;
    int yCenter;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;

    private String text;
    private Typeface typeFace;
    private int textColor = -1;
    private int textSize = -1;
    private int circleColor = -1;
    private int borderColor = -1;
    float posX, posY;
    private String key;

    public LimbikaView(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        //  setLayoutParams(new RelativeLayout.LayoutParams(layout_width, layout_height));
        init(context);
    }

    public LimbikaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        canvas = new Canvas();
        init(context);
    }

    public LimbikaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        canvas = new Canvas();
        init(context);
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setKey(String key) {
        this.key = key;
    }

    int sleft, sright, stop, sbottom;

    /**
     * restores the view if it exists
     **/
    public void onResume() {

        String sql = "select * from viewState where key='" + key + "'";
        Cursor c = db.rawQuery(sql, null);

        if (c.moveToFirst()) {
            int x = c.getInt(c.getColumnIndex("x"));
            int y = c.getInt(c.getColumnIndex("y"));
            int rotation = c.getInt(c.getColumnIndex("rotation"));
            int circleColor = c.getInt(c.getColumnIndex("circleColor"));
            int borderColor = c.getInt(c.getColumnIndex("borderColor"));
            int textColor = c.getInt(c.getColumnIndex("textColor"));
            int backgroundColor = c.getInt(c.getColumnIndex("backgroundColor"));
            int isCircleView = c.getInt(c.getColumnIndex("isCircleView"));
            int textSize = c.getInt(c.getColumnIndex("textSize"));
            int width = c.getInt(c.getColumnIndex("width"));

            sleft = c.getInt(c.getColumnIndex("left"));
            sright = c.getInt(c.getColumnIndex("right"));
            stop = c.getInt(c.getColumnIndex("top"));
            sbottom = c.getInt(c.getColumnIndex("bottom"));

            int height = c.getInt(c.getColumnIndex("height"));
            int drawable = c.getInt(c.getColumnIndex("drawable"));

            String key = c.getString(c.getColumnIndex("key"));
            String userText = c.getString(c.getColumnIndex("userText"));


            posX = x;
            posY = y;

            setX(x);
            setY(y);
            setRotation(rotation);
            setCircleColor(circleColor);
            setBorderColor(borderColor);

            if (!userText.equals("null"))
                setText(userText);

            setTextColor(textColor);
            setTextSize(textSize);
            setBackgroundColor(backgroundColor);
            setCircleView(isCircleView == 1);

            if (image == -1)
                setImage(drawable);

            canvasHeight = 0;
            canvasWidth = 0;

            canvasHeight = height + 50;
            canvasWidth = width + 50;

            invalidate();

        } else {
            posX = 0;
            posY = 0;

            // setting the start point for the balls
            point1 = new Point();
            point1.x = 50;
            point1.y = 20;

            point2 = new Point();
            point2.x = 150;
            point2.y = 20;

            point3 = new Point();
            point3.x = 150;
            point3.y = 120;

            point4 = new Point();
            point4.x = 50;
            point4.y = 120;
        }

    }


    /**
     * saves current view state and position to database
     ***/
    public void saveViewState() {
        Rect myViewRect = new Rect();
        getGlobalVisibleRect(myViewRect);
        float x = myViewRect.left;
        float y = getY();


        //check if this view already exists in db
        String check = "select * from viewState where key='" + key + "'";
        Cursor c = db.rawQuery(check, null);


        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();

        String sql;
        if (c.moveToFirst())
            sql = "update viewState set x=" + x + ",y=" + y + ",rotation=" + rotation + ",isCircleView=" + (isCircleView ? 1 : 0) + ",circleColor=" + circleColor + ",userText='" + text + "',textColor=" + textColor + ",textSize=" + textSize + ",borderColor=" + borderColor + ",drawable=" + image + ",backgroundColor=1,width=" + canvasWidth + ",height=" + canvasHeight + ",left=" + left + ",right=" + right + ",top=" + top + ",bottom=" + bottom + " where key='" + key + "'";
        else
            sql = "insert into viewState values(" + x + "," + y + "," + rotation + ",'" + key + "'," + (isCircleView ? 1 : 0) + "," + circleColor + ",'" + text + "'," + textColor + "," + textSize + "," + borderColor + ",1," + image + "," + canvasWidth + "," + canvasHeight + "," + left + "," + right + "," + top + "," + bottom + ")";

        db.execSQL(sql);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTypeFace(Typeface typeFace) {
        this.typeFace = typeFace;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(int res) {
        this.image = res;
    }

    public void setImage(Bitmap bitmap) {
        this.imageBitmap = bitmap;
    }

    public void setCircleView(boolean bol) {
        this.isCircleView = bol;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {


        int width = bm.getWidth();
        int height = bm.getHeight();


        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width < 0 ? 10 : width, height < 0 ? 10 : height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap getBitmapImage() {
        Resources r = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(r, image);
        return bitmap;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


    // Drawable d = context.getResources().getDrawable(res);
    public void drawImage(Canvas canvas, int res, Bitmap drawableBitmap, int xCenter, int yCenter) {


        Resources r = getResources();
        Bitmap bitmap = drawableBitmap != null ? drawableBitmap : BitmapFactory.decodeResource(r, res);
        mBitmap = bitmap;

        int newWidth = getWidth() - colorballs.get(0).getWidthOfBall();
        int newHeight = getHeight() - colorballs.get(0).getHeightOfBall();


        bitmap = getResizedBitmap(bitmap, newWidth, newHeight);
        canvas.drawBitmap(isCircleView ? getCircleBitmap(bitmap) : bitmap, xCenter, yCenter, paint);


    }

    public void setDoubleTapListener(DoubleTapListener doubleTapListener) {
        this.doubleTapListener = doubleTapListener;
    }

    public void setRotationListener(RotationListener rotationListener) {
        this.rotationListener = rotationListener;
    }

    public void setSingleTapListener(SingleTapListener singleTapListener) {
        this.singleTapListener = singleTapListener;
    }

    public void setLongTapListener(LongTapListener longTapListener) {
        this.longTapListener = longTapListener;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }


    boolean shouldRotate = true;

    public void drag(MotionEvent event, View view) {


        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (event.getActionMasked()) {


            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on

                if (singleTapListener != null) {
                    singleTapListener.onSingleTap(this);
                }

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                startMovePoint = new Point(X, Y);


                //rotate button clicked

                if (isEnabled())
                    if (balID == 0) {
                        if (rotation == 360)
                            rotation = -45;

                        //   if (shouldRotate) {
                        rotation += 45;
                        setRotation(rotation);
                        // }

                        if (rotationListener != null) {
                            rotationListener.onRotate(rotation);
                        }
                    }


                if (balID == 3) {
                    //disable view
                    setVisibility(View.GONE);
                }


                //change balls to selected state
                for (ColorBall ball : colorballs) {
                    if (ball.getID() == 2) {
                        ball.setBitmap(R.drawable.icon_resize);
                        ball.setX(gRight);
                        ball.setY(gBottom);
                    } else if (ball.getID() == 3)
                        ball.setBitmap(R.drawable.icon_delete);
                    else if (ball.getID() == 0)
                        ball.setBitmap(R.drawable.ic_rotate);
                    else {
                        ball.setBitmap(R.drawable.ic_circle_default);
                        ball.setX(gLeft);
                        ball.setY(gBottom);
                    }
                }

                // a ball
                if (points[0] == null) {
                    //initialize rectangle.
                    points[0] = new Point();
                    points[0].x = X;
                    points[0].y = Y;

                    points[1] = new Point();
                    points[1].x = X;
                    points[1].y = Y + canvasHeight;

                    points[2] = new Point();
                    points[2].x = sright;
                    points[2].y = sbottom;

                    points[3] = new Point();
                    points[3].x = X + canvasWidth;
                    points[3].y = Y;

                    balID = 2;
                    groupId = 1;
                    // declare each ball with the ColorBall class
                    for (Point pt : points) {
                        colorballs.add(new ColorBall(getContext(), R.drawable.ic_circle, pt));
                    }
                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = colorballs.size() - 1; i >= 0; i--) {
                        ColorBall ball = colorballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();
                            if (balID == 1 || balID == 3) {
                                groupId = 2;
                            } else {
                                groupId = 1;
                            }


                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball
                //todo enable drag
               /* view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                */
                // if(X>0 && Y>0)


                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                width = size.x;
                height = size.y;

                if (isEnabled()) // only when view is enabled

                    if (balID > -1) {


                        if (balID == 2) {
                            /// bringToFront();
                            colorballs.get(balID).setX(X);
                            colorballs.get(balID).setY(Y);
                        }

                        paint.setColor(Color.GREEN);
                        Rect rect = new Rect();

                        if (groupId == 1) {
                            colorballs.get(1).setX(colorballs.get(0).getX());
                            colorballs.get(1).setY(colorballs.get(2).getY());
                            colorballs.get(3).setX(colorballs.get(2).getX());
                            colorballs.get(3).setY(colorballs.get(0).getY());
                            //   rect.set(colorballs.get(0).getX(), colorballs.get(2).getY(), colorballs.get(2).getX(), colorballs.get(0).getY());

                            Log.d("LimbikaView", "GROUP 1");
                        } else {

                            colorballs.get(0).setX(colorballs.get(1).getX());
                            colorballs.get(0).setY(colorballs.get(3).getY());
                            colorballs.get(2).setX(colorballs.get(3).getX());
                            colorballs.get(2).setY(colorballs.get(1).getY());
                            Log.d("LimbikaView", "GROUP 2");

                        }

                        rect.set(colorballs.get(1).getX(), colorballs.get(3).getY(), colorballs.get(3).getX(), colorballs.get(1).getY());


                        canvasWidth = rect.width();
                        canvasHeight = rect.height();

                        //  double diagonal = Math.sqrt((canvasHeight*canvasHeight + canvasWidth*canvasWidth));


                        xCenter = rect.centerX() - rect.width() / 2;
                        yCenter = rect.centerX() - rect.height() / 2;


                        //    double diagonal = Math.sqrt((canvasHeight*canvasHeight + canvasWidth*canvasWidth))/2-30;


                        // if (isCircleView)
                        ///    if (canvasHeight > canvasWidth)
                        //        canvas.drawCircle(xCenter, yCenter, (int) canvasHeight / 2 - 15, paint);
                        //    else
                        //      canvas.drawCircle(xCenter, yCenter, (int) canvasWidth / 2 - 15, paint);


                        //   if (!TextUtils.isEmpty(text))

                        //   drawDigit(canvas, canvasHeight, xCenter, yCenter, Color.BLACK, text);


                        // if (image != -1)
                        //   canvas.drawBitmap(getResizedBitmap(getBitmapImage(), rect.width() - 10, rect.height() - 10), colorballs.get(0).getX() + 30, colorballs.get(0).getY() + 30, paint);

                        getLayoutParams().height = canvasHeight + colorballs.get(0).getHeightOfBall();
                        getLayoutParams().width = canvasWidth + colorballs.get(0).getWidthOfBall();


                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;
                        if ((newX <= canvasWidth / 2 || newX >= width - canvasWidth - 50) ||
                                (newY <= canvasHeight / 2 || newY >= height - canvasHeight - 50)) {//don't move oustide
                            shouldRotate = false;

                        }

                        invalidate();


                    } else {


                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;
                        if ((newX <= 0 || newX >= width - canvasWidth - 10) ||
                                (newY <= 0 || newY >= height - canvasHeight)) {//don't move oustide
                            shouldRotate = false;
                            break;
                        }

                        shouldRotate = true;

                        if (dragListener != null)
                            dragListener.onDrag(event.getRawX() + dX, event.getRawY() + dY);

                        setX(event.getRawX() + dX);
                        setY(event.getRawY() + dY);


                        if (false/*startMovePoint!=null*/) { //dont move bitch!
                            paint.setColor(Color.CYAN);
                            int diffX = X - startMovePoint.x;
                            int diffY = Y - startMovePoint.y;


                            startMovePoint.x = X;
                            startMovePoint.y = Y;
                            colorballs.get(0).addX(diffX);
                            colorballs.get(1).addX(diffX);
                            colorballs.get(2).addX(diffX);
                            colorballs.get(3).addX(diffX);
                            colorballs.get(0).addY(diffY);
                            colorballs.get(1).addY(diffY);
                            colorballs.get(2).addY(diffY);
                            colorballs.get(3).addY(diffY);
                            if (groupId == 1) {
                                canvas.drawRect(point1.x, point3.y, point3.x, point1.y,
                                        paint);
                            } else {
                                canvas.drawRect(point2.x, point4.y, point4.x, point2.y,
                                        paint);
                            }


                            //  view.animate()
                            //           .x(event.getRawX() + dX)
                            ///        .y(event.getRawY() + dY)
                            //       .setDuration(0)
                            //    .start();


                            invalidate();
                        }
                    }


                break;

            case MotionEvent.ACTION_UP:

                saveViewState();
                // touch drop - just do things here after dropping
                requestLayout();


                //change balls to un-selected state
                if (isEnabled()) // only when view is enabled

                    for (ColorBall ball : colorballs) {

                        if (ball.getID() == 2)
                            ball.setBitmap(R.drawable.icon_resize);
                        else if (ball.getID() == 3)
                            ball.setBitmap(R.drawable.icon_delete);
                        else if (ball.getID() == 0)
                            ball.setBitmap(R.drawable.ic_rotate);
                        else
                            ball.setBitmap(R.drawable.ic_circle_default);
                    }


                break;

            default:
                break;
        }

    }


    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    Database helper;

    SQLiteDatabase db;

    private void init(Context context) {

        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        helper = new Database(getContext());
        db = helper.getWritableDatabase();

        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        gestureDetector = new GestureDetector(context, new GestureListener());


        //position of the rectangle
        int X = (int) posX;
        int Y = (int) posY;

        //initialize rectangle.
        points[0] = new Point();
        points[0].x = X;
        points[0].y = Y;

        points[1] = new Point();
        points[1].x = X;
        points[1].y = Y + canvasHeight;

        points[2] = new Point();
        points[2].x = X + canvasWidth;
        points[2].y = Y + canvasHeight;

        points[3] = new Point();
        points[3].x = X + canvasWidth;
        points[3].y = Y;

        balID = 2;
        groupId = 1;
        // declare each ball with the ColorBall class
        for (Point pt : points) {
            colorballs.add(new ColorBall(getContext(), R.drawable.ic_circle_default, pt));
        }


        //todo enable rotation setRotation(45);

    }


    int gRight;
    int gBottom;
    int gLeft;
    int gTop;

    // the method that draws the balls
    // @Override
    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect();
        // rect.set(colorballs.get(1).getX(), colorballs.get(3).getY(),sright, sbottom);
        // canvasWidth = rect.width();
        //canvasHeight = rect.height();

        getLayoutParams().height = canvasHeight + colorballs.get(0).getHeightOfBall();
        getLayoutParams().width = canvasWidth + colorballs.get(0).getWidthOfBall();
        requestLayout();
        //allow drawing outside the canvas
//        Rect newRect = canvas.getClipBounds();
//        newRect.inset(-colorballs.get(0).getWidthOfBall()/3, -colorballs.get(0).getWidthOfBall()/3);  //make the rect larger
//
        //    canvas.clipRect (newRect, Region.Op.REPLACE);

        //happily draw outside the bound now

        //canvasHeight = getLayoutParams().height;
        // canvasWidth = getLayoutParams().width;


        //move canvas to correct position
        //     canvas.translate(colorballs.get(0).getX(),colorballs.get(0).getY());

        // canvas.rotate(45,getWidth()/2,getHeight()/2);
        int left, top, right, bottom;
        left = sleft;
        top = stop;
        right = getWidth() - colorballs.get(0).getWidthOfBall() + 5;
        bottom = getHeight() - colorballs.get(0).getHeightOfBall() + 5;

        gRight = right;
        gBottom = bottom;
     /*   if (points[3] == null) { //point4 null when user did not touch and move on screen.

            left = sleft;
            top =stop;
            right= getWidth();
            bottom =getHeight();

           // return;
        }else {

            left = points[0].x;
            top = points[0].y;
            right = points[0].x;
            bottom = points[0].x;

            for (int i = 1; i < points.length; i++) {
                left = left > points[i].x ? points[i].x : left;
                top = top > points[i].y ? points[i].y : top;
                right = right < points[i].x ? points[i].x : right;
                bottom = bottom < points[i].y ? points[i].y : bottom;
            }
        }*/
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x : left;
            top = top > points[i].y ? points[i].y : top;
            right = right < points[i].x ? points[i].x : right;
            bottom = bottom < points[i].y ? points[i].y : bottom;
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);


        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        if (borderColor != -1)
            paint.setColor(borderColor);
        else
            paint.setColor(Color.parseColor("#AADB1255"));

        paint.setStrokeWidth(2);

        //draw red rect only when view is enabled
        if (isEnabled())
            canvas.drawRect(
                    left + colorballs.get(0).getWidthOfBall() / 2,
                    top + colorballs.get(0).getWidthOfBall() / 2,
                    right + colorballs.get(2).getWidthOfBall() / 2,
                    bottom + colorballs.get(2).getWidthOfBall() / 2, paint);


        //fill the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT/*parseColor("#55DB1255")*/);
        paint.setStrokeWidth(0);


        canvas.drawRect(
                left + colorballs.get(0).getWidthOfBall() / 2,
                top + colorballs.get(0).getWidthOfBall() / 2,
                right + colorballs.get(2).getWidthOfBall() / 2,
                bottom + colorballs.get(2).getWidthOfBall() / 2, paint);

        paint.setStyle(Paint.Style.FILL);

        if (circleColor != -1)
            paint.setColor(circleColor);
        else
            paint.setColor(Color.parseColor("#55DB1255"));

        paint.setStrokeWidth(0);

        int xCenter = ((left + colorballs.get(0).getWidthOfBall() / 2) + (right + colorballs.get(2).getWidthOfBall() / 2)) / 2;
        int yCenter = ((top + colorballs.get(0).getWidthOfBall()) / 2 + (bottom + colorballs.get(2).getWidthOfBall() / 2)) / 2;


        if (isCircleView && image == -1)
            if (canvasHeight > canvasWidth)
                canvas.drawCircle(xCenter, yCenter, canvasWidth / 2, paint);
            else
                canvas.drawCircle(xCenter, yCenter, canvasHeight / 2, paint);

        if (!TextUtils.isEmpty(text))
            if (isCircleView)
                drawDigit(canvas, canvasHeight > canvasWidth ? canvasWidth / 16 : canvasHeight / 16
                        , xCenter, yCenter, Color.BLACK, text);
            else
                drawDigit(canvas, canvasHeight > canvasWidth ? canvasWidth / 16 : canvasHeight / 16
                        , xCenter, yCenter, Color.BLACK, text);


        //draw the corners
        BitmapDrawable bitmap = new BitmapDrawable();
        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setStrokeWidth(0);


        //draw balls only when view is enabled
        if (isEnabled())
            for (int i = 0; i < colorballs.size(); i++) {
                ColorBall ball = colorballs.get(i);


                //  draw balls onn correct position
                if (i == 1)
                    canvas.drawBitmap(ball.getBitmap(), left, bottom,
                            paint);
                else if (i == 3)
                    canvas.drawBitmap(ball.getBitmap(), right, top,
                            paint);
                else if (i == 2)
                    canvas.drawBitmap(ball.getBitmap(), right, bottom,
                            paint);
                else
                    canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                            paint);


                if (ball.getID() == 0)
                    canvas.drawText("" + getRotation(), ball.getX(), ball.getY(), paint);
                //   else
                //  canvas.drawText("" + (i + 1), ball.getX(), ball.getY(), paint);

            }


        //  xCenter = ((left + colorballs.get(0).getWidthOfBall() / 2) + (right + colorballs.get(2).getWidthOfBall() / 2)) / 2;
        //  yCenter = ((top + colorballs.get(0).getWidthOfBall()) / 2 + (bottom + colorballs.get(2).getWidthOfBall() / 2)) / 2;


        if (image != -1)
            drawImage(canvas, image, imageBitmap, left + colorballs.get(0).getWidthOfBall() / 2, top + colorballs.get(0).getWidthOfBall() / 2);


        gRight = right;
        gLeft = left;
        gTop = top;
        gBottom = bottom;
    }

    private void drawDigit(Canvas canvas, int textSize, float cX, float cY, int color, String text) {

        Rect bounds = new Rect();
        bounds.set(gLeft, gTop, gRight, gBottom);


        TextPaint tempTextPaint = new TextPaint();
        tempTextPaint.setAntiAlias(true);
        tempTextPaint.setStyle(Paint.Style.FILL);
        if (typeFace != null)
            tempTextPaint.setTypeface(typeFace);
        tempTextPaint.setTextAlign(Paint.Align.LEFT);

        if (textColor != -1)
            tempTextPaint.setColor(textColor);
        else
            tempTextPaint.setColor(color);

        if (this.textSize != -1)
            tempTextPaint.setTextSize(this.textSize);
        else
            tempTextPaint.setTextSize(18);

        tempTextPaint.setTextAlign(Paint.Align.LEFT);

        //int xPos = (int) cX;
        // int yPos = (int) (cY - (tempTextPaint.descent() + tempTextPaint.ascent()) / 2);

        //Static layout which will be drawn on canvas
        //textOnCanvas - text which will be drawn
        //text paint - paint object
        //bounds.width - width of the layout
        //Layout.Alignment.ALIGN_CENTER - layout alignment
        //1 - text spacing multiply
        //1 - text spacing add
        //true - include padding
        //
        StaticLayout sl = new StaticLayout(text, tempTextPaint, canvasWidth >
                canvasHeight ? ((canvasWidth / 2 ) < 0 ? 1 : (canvasWidth / 2 )) : ((canvasHeight / 2 ) < 0 ? 1 : (canvasHeight / 2 )),
                Layout.Alignment.ALIGN_CENTER, 1, 1, false);

        canvas.save();


        //calculate X and Y coordinates - In this case we want to draw the text in the
        //center of canvas so we calculate
        //text height and number of lines to move Y coordinate to center.
        float textHeight = getTextHeight(text, tempTextPaint);
        int numberOfTextLines = sl.getLineCount();
        float textYCoordinate = bounds.exactCenterY() -
                ((numberOfTextLines * textHeight) / 2);

        //text will be drawn from left
        float textXCoordinate = cX - 30;

        //if (isCircleView)
        //      canvas.translate(textXCoordinate, textYCoordinate + 30);
        //   else
        canvas.translate(canvasHeight > canvasWidth ? cX - canvasWidth / 5 : cX - canvasHeight / 5, cY);

        //draws static layout on canvas
        sl.draw(canvas);
        canvas.restore();

     /*   Paint tempTextPaint = new Paint();
        tempTextPaint.setAntiAlias(true);
        tempTextPaint.setStyle(Paint.Style.FILL);
      //  tempTextPaint.setTypeface(tf);
        tempTextPaint.setTextAlign(Paint.Align.LEFT);
        tempTextPaint.setColor(color);
        tempTextPaint.setTextSize(textSize);

        //get max text size
        int size = 0;
        do {
            tempTextPaint.setTextSize(++ size);
        } while(tempTextPaint.measureText(text) < canvasWidth);

        tempTextPaint.setTextSize(size);


        float textWidth = tempTextPaint.measureText(text);
        //if cX and cY are the origin coordinates of the your rectangle
        //cX-(textWidth/2) = The x-coordinate of the origin of the text being drawn
        //cY+(textSize/2) =  The y-coordinate of the origin of the text being drawn

        canvas.drawText(text, cX - (textWidth / 2), cY + (textSize / 2), tempTextPaint);*/
    }

    /**
     * @return text height
     */
    private float getTextHeight(String text, Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }
    // events when touching the screen

    public boolean onTouchEvent(MotionEvent event) {
        drag(event, LimbikaView.this);
        if (isEnabled())
            gestureDetector.onTouchEvent(event);
        return true;
    }

  /*  @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * canvasWidth / canvasHeight;
        setMeasuredDimension(width, height);
    }*/


    //LISTENERS
    public interface SingleTapListener {
        void onSingleTap(View v);
    }

    public interface LongTapListener {
        void onLongTap(View v);
    }

    public interface DragListener {
        void onDrag(float x, float y);
    }

    public interface DoubleTapListener {
        void onDoubleTap(float x, float y);
    }

    public interface RotationListener {
        void onRotate(int rotation);//current rotation in degrees
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public void onLongPress(MotionEvent e) {
            if (longTapListener != null)
                longTapListener.onLongTap(LimbikaView.this);
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (doubleTapListener != null) {
                doubleTapListener.onDoubleTap(x, y);
            }

            return true;
        }
    }

}