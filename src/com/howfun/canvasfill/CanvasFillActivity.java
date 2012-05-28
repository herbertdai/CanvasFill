package com.howfun.canvasfill;

/**
 * Canvas fill gardget.
 * Print the scope of pictures with custom characters.
 * @author herbert dwy
 * @date 2012.5.28
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class CanvasFillActivity extends Activity {

   private static final int CELL_W = 6;
   private static final int CELL_H = 6;
   private static final String LOG_TAG = "CanvasFillActivity";

   private Bitmap mCanvasBitmap;
   private int outW;
   private int outH;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      // String path = "/mnt/sdcard/testfill.png";
      String path = "/mnt/sdcard/bg2011112607.jpg";
      mCanvasBitmap = BitmapFactory.decodeFile(path);

      outW = mCanvasBitmap.getWidth() / CELL_W + 1;
      outH = mCanvasBitmap.getHeight() / CELL_H + 1;
      Log.e(LOG_TAG, "outw outH = " + outW + ", " + outH);

      int[][] meanArray = GenFillArray(mCanvasBitmap);

      printToCanvas(meanArray);

   }

   private void printToCanvas(int[][] meanArray) {
      for (int i = 0; i < outH; i++) {
         for (int j = 0; j < outW; j++) {
            if (meanArray[i][j] != 6)
               System.out.print(meanArray[i][j]);
            else
               System.out.print(" ");
            System.out.print(" ");

         }
         System.out.println();
      }

   }

   private int[][] GenFillArray(Bitmap bitmap) {
      int out[][] = new int[outH][outW];

      for (int i = 0; i < bitmap.getHeight() / CELL_H; ++i) {
         for (int j = 0; j < bitmap.getWidth() / CELL_W; ++j) {

            // Get cell pixels line by line.
            int[] pixels = new int[CELL_W * CELL_H];
            bitmap.getPixels(pixels, 0, CELL_W, j * CELL_W, i * CELL_H, CELL_W,
                  CELL_H);
            GetGrays(pixels);
            // Get mean
            int mean = getMean(pixels);
            // Get brush index
            int brushIndex = getBrushIndex(mean);
            // Put into OutputArray
            out[i][j] = brushIndex;
         }
      }

      return out;

   }

   private int getBrushIndex(int x) {
      if (0 <= x && x <= 41)
         return 1;
      if (41 < x && x <= 83)
         return 2;
      if (83 < x && x <= 124)
         return 3;
      if (124 < x && x <= 165)
         return 4;
      if (165 < x && x <= 206)
         return 5;
      if (206 < x && x <= 247)
         return 6;
      else
         return 6;
   }

   private void GetGrays(int[] pixels) {
      for (int i = 0; i < pixels.length; i++) {

         // if (pixels[i] != 0)
         // Log.e(LOG_TAG, String.format("color = %x", pixels[i]));

         pixels[i] = rgb2gray(pixels[i]);

         // if (pixels[i] != 0)
         // Log.e(LOG_TAG, "gray = " + pixels[i]);
      }
   }

   private int getMean(int[] pixels) {
      int mean = 0;
      for (int x = 0; x < CELL_W * CELL_H; x++) {
         mean += pixels[x];
      }
      mean /= pixels.length;
      return mean;

   }

   private int rgb2gray(int argb) {
      int _alpha = (argb >> 24) & 0xFF;
      int _red = (argb >> 16) & 0xFF;
      int _green = (argb >> 8) & 0xFF;
      int _blue = (argb) & 0xFF;
      double gray = 0.3 * _red + 0.59 * _green + 0.11 * _blue;
      return (int) gray;
   }

   public void onDestroy() {
      super.onDestroy();
      if (mCanvasBitmap != null) {
         if (!mCanvasBitmap.isRecycled()) {
            mCanvasBitmap.recycle();
            mCanvasBitmap = null;
         }
      }
   }
}