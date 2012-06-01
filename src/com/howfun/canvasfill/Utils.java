package com.howfun.canvasfill;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

public class Utils {

   public static final String ROOT = "/mnt/sdcard/";
   private static final String LOG_TAG = "Utils";
   

   public static void log(String tag, String string) {
      Log.e("CanvasFill", ">>>>>>." + tag + ">>>>.."+ string);
      
   }

   public static boolean deleteFile(String fileName) {
      File file = new File(fileName);
      if (file.isFile() && file.exists()) {
         file.delete();
         return true;
      } else {
         return false;
      }
   }
   public static Bitmap CaptureView(View v) {

      Bitmap bitmap = null;

      Bitmap mBitmap2 = null;

      View view = (View) v;
      if (view != null) {
         if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
         }
         if (!view.isDrawingCacheEnabled()) {
            Log.e("Capture", "drawing cache not enabled");
         }
         bitmap = view.getDrawingCache();
         if (bitmap == null) {
            Log.e("Capture", "bitmap is null");
            return null;
         }
         mBitmap2 = bitmap.copy(Bitmap.Config.RGB_565, true);
         view.setDrawingCacheEnabled(false);
         view.setDrawingCacheEnabled(true);
      }
      return mBitmap2;
   }
   public static void saveBitmapToFile(Bitmap croppedImage, String outputPath) {
      if (croppedImage == null) {
         return;
      }

      Utils.log(LOG_TAG, "card w, h= " + croppedImage.getWidth() + ","
            + croppedImage.getHeight());

      if (outputPath == null || ("").equals(outputPath)) {
         Utils.log("saveBitmapToFile path = ", "null");
         return;
      }

      File file = new File(outputPath);
      if (file.exists()) {
         file.delete();
      }
      FileOutputStream fos;

      try {
         fos = new FileOutputStream(file);
         if (fos != null) {
            croppedImage.compress(Bitmap.CompressFormat.PNG, 99, fos);
         }
         fos.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
