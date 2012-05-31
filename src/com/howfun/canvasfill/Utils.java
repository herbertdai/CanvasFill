package com.howfun.canvasfill;

import java.io.File;

import android.util.Log;

public class Utils {

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

}
