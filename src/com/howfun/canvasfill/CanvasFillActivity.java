package com.howfun.canvasfill;

/**
 * Canvas fill gardget.
 * Print the scope of pictures with custom characters.
 * @author herbert dwy
 * @date 2012.5.28
 */

/**
 * 2012.6.2
 * Output size = raw size / cell size * brush size;
 * TODO: New mode: Imitate desired chars with custom chars.
 * TODO: Click generated pic to open.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class CanvasFillActivity extends Activity {

   private static final int CELL_W = 6;
   private static final int CELL_H = 6;

   private static final int BRUSH_W = 16;
   private static final int BRUSH_H = 16;

   private static final String LOG_TAG = "CanvasFillActivity";
   private static final String TAG = "CavasFill";

   private static final int REQUEST_IMG_PATH_FROM_CAMERA = 0x11;
   private static final int REQUEST_IMG_PATH_FROM_LOCAL = 0x10;
   private static final String TEMP_FILE = Utils.ROOT + "tempCamera.jpg";
   private static final String OUTPUT_FILE = Utils.ROOT + "adaiFill.png";

   private Bitmap mCanvasBitmap;
   private int outW;
   private int outH;

   private Paint paint;
   private int inputH = 400;
   private int inputW = 400;
   private char[] mTextBrushes;
   private Bitmap mOutputBitmap;
   private EditText mShowText;
   private EditText mBrushText;
   private ImageView mOutputImg;
   private Button mGenBtn;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      findViews();
      paint = new Paint();

   }

   private void findViews() {
      mShowText = (EditText) findViewById(R.id.show_text);
      mBrushText = (EditText) findViewById(R.id.brush_text);
      mOutputImg = (ImageView) this.findViewById(R.id.out_img);
      mGenBtn = (Button) findViewById(R.id.gen_btn);

      setOnClickListener();

   }

   private void setOnClickListener() {
      mOutputImg.setOnClickListener(new OnClickListener() {

         @Override
         public void onClick(View v) {
            Intent i = Utils.getImageFileIntent(OUTPUT_FILE);
            if (i != null) {
               startActivity(i);
            }
         }

      });

      mGenBtn.setOnClickListener(new OnClickListener() {

         @Override
         public void onClick(View v) {
            loadBrushes();
            processTextImg();
         }

      });

   }

   private void loadBrushes() {

      /** get brush from text */
      String customText = mBrushText.getText().toString();
      if (customText == null || customText.equals(""))
         customText = "T";
      mTextBrushes = customText.toCharArray();

      for (int i = 0; i < mTextBrushes.length; i++) {
         char tempStr = mTextBrushes[i];
         Utils.log(LOG_TAG, "temp str: " + tempStr);

      }

   }

   private void processTextImg() {
      Bitmap bitmap = Utils.CaptureView(mShowText);

      if (bitmap != null) {
         mCanvasBitmap = bitmap;
         processBitmap(mCanvasBitmap);
      }

   }

   private void processImgFile(String path) {

      mCanvasBitmap = BitmapFactory.decodeFile(path);
      processBitmap(mCanvasBitmap);

   }

   private void processBitmap(Bitmap bitmap) {

      if (bitmap == null) {
         return;
      }
      inputW = bitmap.getWidth();
      inputH = bitmap.getHeight();

      outW = bitmap.getWidth() / CELL_W + 1;
      outH = bitmap.getHeight() / CELL_H + 1;
      Log.e(LOG_TAG, "outw outH = " + outW + ", " + outH);

      int[][] meanArray = GenFillArray(bitmap);

      printToCanvas(meanArray);

   }

   private void printArrayInLog(int[][] meanArray) {
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

   private void printToCanvas(int[][] meanArray) {

      Bitmap.Config config = Config.RGB_565;
      int width = inputW / CELL_W * BRUSH_W;
      int height = inputH / CELL_H * BRUSH_H;
      Utils.log(LOG_TAG, "output bitmap w = " + width + "output h = " + height);

      if (mOutputBitmap != null) {
         mOutputBitmap.recycle();
         mOutputBitmap = null;
      }

      mOutputBitmap = Bitmap.createBitmap(width, height, config);

      Bitmap bitmap = mOutputBitmap;

      // Draw canvas with custom brushes.
      Canvas canvas = new Canvas();
      canvas.setBitmap(bitmap);

      paint.setColor(Color.RED);
      paint.setTextSize(BRUSH_H);

      if (meanArray != null) {
         for (int i = 0; i < outH; i++) {
            for (int j = 0; j < outW; j++) {

               int curBrush = meanArray[i][j];
               if (curBrush != 5) {
                  curBrush %= mTextBrushes.length;
                  canvas.drawText(String.valueOf(mTextBrushes[curBrush]),
                        BRUSH_W * j, BRUSH_H * i, paint);
               }
            }
         }
      }

      // Load in View.
      mOutputImg.setImageBitmap(bitmap);
      // Save to file
      Utils.saveBitmapToFile(bitmap, OUTPUT_FILE);

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
         return 0;
      if (41 < x && x <= 83)
         return 1;
      if (83 < x && x <= 124)
         return 2;
      if (124 < x && x <= 165)
         return 3;
      if (165 < x && x <= 206)
         return 4;
      if (206 < x && x <= 247)
         return 5;
      else
         return 5;
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

      if (mOutputBitmap != null) {
         mOutputBitmap.recycle();
         mOutputBitmap = null;
      }

      // Utils.deleteFile(TEMP_FILE);
   }

   private void addImageFromLocal() {

      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(intent, REQUEST_IMG_PATH_FROM_LOCAL);
   }

   private void addImageFromCamera() {

      String fileName = TEMP_FILE;
      ContentValues values = new ContentValues();
      values.put(Images.Media.TITLE, fileName);
      values.put("_data", fileName);
      values.put(Images.Media.PICASA_ID, fileName);
      values.put(Images.Media.DISPLAY_NAME, fileName);
      values.put(Images.Media.DESCRIPTION, fileName);
      values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, fileName);
      Uri photoUri = getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
      startActivityForResult(i, REQUEST_IMG_PATH_FROM_CAMERA);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
      case REQUEST_IMG_PATH_FROM_LOCAL:
         if (resultCode == RESULT_OK) {
            String imgPath = "";
            Uri uri = data.getData();
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
               cursor = managedQuery(uri, proj, null, null, null);
            } catch (Exception e) {
               e.printStackTrace();
            }

            if (cursor != null) {
               int column_index = cursor
                     .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
               cursor.moveToFirst();
               imgPath = cursor.getString(column_index);
               Utils.log(TAG, "request iamge path from local:" + imgPath);

               processImgFile(imgPath);

            } else {
               new AlertDialog.Builder(this).setTitle("Inavlid image")
                     .setPositiveButton(android.R.string.ok, null).show();
               Utils.log(TAG, "cursor is null");
            }

         }
         break;

      case REQUEST_IMG_PATH_FROM_CAMERA:
         if (resultCode == RESULT_OK) {
            processImgFile(TEMP_FILE);
         }
         break;
      default:
         break;
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.options, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      super.onOptionsItemSelected(item);
      switch (item.getItemId()) {
      case R.id.menu_about:
         Utils.showAbout(this);
         break;
      //
      // case R.id.menu_load_from_camera:
      // addImageFromCamera();
      // break;

      case R.id.menu_gen_from_text:
         loadBrushes();
         processTextImg();
         break;
      default:
         break;
      }
      return true;
   }

}