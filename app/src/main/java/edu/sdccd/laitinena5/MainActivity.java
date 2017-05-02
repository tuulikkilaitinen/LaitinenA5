// MainActivity.java
// Sets MainActivity's layout
package edu.sdccd.laitinena5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

   private static final int FILE_SELECT_CODE = 2;
   // configures the screen orientation for this app
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      // determine screen size
      int screenSize =
         getResources().getConfiguration().screenLayout &
            Configuration.SCREENLAYOUT_SIZE_MASK;

      // use landscape for extra large tablets; otherwise, use portrait
      if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      else
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      System.out.println ("MainActivity: onactivityresult called.");

      System.out.println (resultCode);
      switch (requestCode) {
         case FILE_SELECT_CODE:
            if (resultCode == RESULT_OK) {

               //get the URI of the selected file
               Uri uri = data.getData();
               Bitmap mergeBitmap = null;
               //get the path
               //String path = File
               //String path = uri.getPath();
               //String path = getRealPathFromURI(getApplicationContext(), uri);
               System.out.println (uri);
               //set background image
               try {
                  mergeBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
               } catch (IOException e) {
                  e.printStackTrace();
               }
               getDoodleFragment().getDoodleView().setBgImage(mergeBitmap);

            }
            break;
         default:
            break;
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   private String getRealPathFromURI(Context context, Uri contentUri) {
      Cursor cursor = null;
      try {
         String[] proj = { MediaStore.Images.Media.DATA };
         cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         return cursor.getString(column_index);
      } finally {
         if (cursor != null) {
            cursor.close();
         }
      }
   }
   // gets a reference to the MainActivityFragment
   private MainActivityFragment getDoodleFragment() {

      return (MainActivityFragment) getSupportFragmentManager().findFragmentById(
              R.id.doodleFragment);
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

