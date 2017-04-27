// DoodleView.java
// Main View for the Doodlz app.
package edu.sdccd.laitinena5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// custom View for drawing
public class DoodleView extends View {
    // used to determine whether user moved a finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap mergeBitmap; // Background for background color/image
    private Canvas mergeBitmapCanvas; //use to draw backgroudn to the bitmap
    private Canvas fgBitmapCanvas; // used to to draw on the bitmap
    private Bitmap fgBitmap; // Foreground bitmap drawing area for displaying or saving
    private Paint paintScreen; // used to draw bitmap onto screen
    private Paint paintLine; // used to draw lines onto bitmap
    private Paint bgPaint; // used to handle background bitmap information

    // Maps of current Paths being drawn and Points in those Paths
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap =  new HashMap<>();

    //Bitmap mMergedLayersBitmap=null; //Note: this bitmap here contains the whole of the drawing (background+foreground) to be saved.
   // Canvas mMergedLayersCanvas=null;

    //Bitmap mBitmap = null; //bitmap onto which we draw our stuff
    //Canvas mCanvas = null; //Main canvas. Will be linked to a .bmp file
    private final int defaultBGColor = Color.WHITE;
    //int mBackgroundColor = 0xFF000000; //default background color
    //Paint mDefaultPaint = new Paint();

    Paint mDrawPaint = new Paint(); //used for painting example foreground stuff... We draw line segments.

    private double eventPressure = 0.52;

    // DoodleView constructor initializes the DoodleView
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs); // pass context to View's constructor

        initPaints();


    }

    private void initPaints() {

        paintScreen = new Paint(); // used to display bitmap onto screen

        // set the initial display settings for the painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(5); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
        //setLayerType(View.LAYER_TYPE_SOFTWARE, paintLine); not helping

        //create and init Paint to hold info for bg bitmap
        bgPaint = new Paint();
        bgPaint.setColor(this.defaultBGColor); //default background color is white
        //setLayerType(View.LAYER_TYPE_SOFTWARE, bgPaint); not helping
    }

    // creates Bitmap and Canvas based on View's size
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {

        /*
        //create foreground bitmap
        fgBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(fgBitmap);
        fgBitmap.eraseColor(Color.WHITE); // erase the Bitmap with white

        //create background bitmap
        bgBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bgBitmapCanvas = new Canvas(bgBitmap);
        //set background paint to hold white
        bgPaint.setColor(Color.WHITE);
        bgBitmap.eraseColor(bgPaint.getColor()); // erase background Bitmap with white
*/

        initBitmaps();

    }

    private void initBitmaps () {
        mergeBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mergeBitmapCanvas = new Canvas(mergeBitmap);

        fgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        fgBitmapCanvas = new Canvas(fgBitmap);
    }

    // clear the painting from foreground and background bitmaps
    public void clear() {

        for (Integer key : pathMap.keySet()) {
            //canvas.drawPath(pathMap.get(key), paintLine); // draw line
            Path path = pathMap.get(key);
            path.reset(); // resets the Path because a new touch has started
        }
        pathMap.clear(); // remove all paths
        previousPointMap.clear(); // remove all previous points

        //paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        //fgBitmap.eraseColor(Color.WHITE); // clear the foreground bitmap with default color
        //fgBitmap.eraseColor(Color.WHITE);
        //setBGColor(Color.WHITE);
        //set background paint to hold white
        //bgPaint.setColor(Color.WHITE);
        //mergeBitmap.eraseColor(bgPaint.getColor()); // erase background Bitmap with white

        initPaints();
        initBitmaps();

        invalidate(); // refresh the screen
    }

    // set the background color
    public void setBGColor(int color) {

        //set background paint to hold selected color

        bgPaint.setColor(color);


        invalidate(); //refresh the screen
    }

    public int getBGColor() {
        return bgPaint.getColor();
    }

    // set the painted line's color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the painted line's color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    // set the painted line's width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the painted line's width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    // perform custom drawing when the DoodleView is refreshed on screen
    @Override
    protected void onDraw(Canvas canvas)  {

        //change background color to canvas
        //canvas.drawColor(this.backgroundColor);
        /*

        // draw the foregound screen
        canvas.drawBitmap(fgBitmap, 0, 0, paintScreen);

        // for each path currently being drawn
        for (Integer key : pathMap.keySet())
            canvas.drawPath(pathMap.get(key), paintLine); // draw line
            */
        int bgColor = bgPaint.getColor();
        mergeBitmapCanvas.drawColor(bgColor);
        mergeBitmapCanvas.drawBitmap(fgBitmap, 0, 0, bgPaint);

        canvas.drawBitmap(mergeBitmap, 0, 0, bgPaint);

        for (Integer key : pathMap.keySet())
              canvas.drawPath(pathMap.get(key), paintLine); // draw line
    }

    // handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            this.eventPressure = event.getAxisValue(MotionEvent.AXIS_PRESSURE);
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
            System.out.println ("Start: "+event.getAxisValue(MotionEvent.AXIS_PRESSURE));
        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            System.out.println ("After: "+event.getAxisValue(MotionEvent.AXIS_PRESSURE));
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            System.out.println ("During "+event.getAxisValue(MotionEvent.AXIS_PRESSURE));
            touchMoved(event);
        }

        invalidate(); // redraw
        return true;
    }

    // called when the user touches the screen
    private void touchStarted(float x, float y, int lineID) {
        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path

        paintLine.setStrokeWidth((float)transferPressureToLineWidth(this.eventPressure));

        // if there is already a path for lineID
        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID); // get the Path
            path.reset(); // resets the Path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point
        }
        else {
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
        }

        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {
        // for each of the pointers in the given MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {
            // get the pointer ID and pointer index
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                // get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point associated with
                // this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private double transferPressureToLineWidth (double eventPressure)
    {

        double pressure;


        //pressure = eventPressure * 100 - 62 + 18;
        pressure = eventPressure * 100 - 45;
        System.out.println (pressure);
        if (pressure > 20) {
            pressure += 30;
        }
        else if (pressure > 18) {
            pressure += 25;
        }
        else if (pressure > 15) {
            pressure += 15;
        }
        else if (pressure > 13) {
            pressure += 5;
        }
        else {
            pressure -= 10;
        }
        System.out.println (pressure);
        if (pressure < 0) {
            pressure = 0;
        }
        return pressure;
    }

    // called when the user finishes a touch
    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID); // get the corresponding Path
        fgBitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset(); // reset the Path
    }

    // save the current image to the Gallery
    public void saveImage() {
        // use "Doodlz" followed by current time as the image name
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";

        // insert the image on the device
        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), fgBitmap, name,
                "Doodlz Drawing");

        if (location != null) {
            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getContext(),
                    R.string.message_saved,
                    Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
        else {
            // display a message indicating that there was an error saving
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_saving, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
    }

    // print the current image
    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            // use Android Support Library's PrintHelper to print image
            PrintHelper printHelper = new PrintHelper(getContext());

            // fit image in page bounds and print the image
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", fgBitmap);
        }
        else {
            // display message indicating that system does not allow printing
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_printing, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
    }
}

/**************************************************************************
 * (C) Copyright 1992-2016 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/

