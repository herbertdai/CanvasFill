package com.howfun.canvasfill;

/**
  * Custom view to draw special brushes on.
  * @author herbertd @ www.adaiw.com (A night programmer)
  * @time 2012.5.29:12:35AM
  *
  */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyCanvas extends View {

   private int[][] mFills = null;
   private int mFillW;
   private int mFillH;
   private Bitmap bitmap1;
   private Bitmap bitmap2;
   private Bitmap bitmap3;
   private Bitmap bitmap4;
   private Bitmap bitmap5;
   private Bitmap bitmap6;
   
   private Bitmap[] mBrushes; 

   public MyCanvas(Context context, AttributeSet attrs) {
      super(context, attrs);
      // TODO Auto-generated constructor stub
      bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic1);
      bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic2);
      bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.ic3);
      bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.ic4);
      bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.ic5);
      bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.ic5);
      mBrushes = new Bitmap[]{bitmap1, bitmap2, bitmap3, bitmap4, bitmap5, bitmap6};
   }

   @Override
   public void onDraw(Canvas canvas) {

      Paint paint = new Paint();
      if (mFills != null) {
         for (int i = 0; i < mFillH; i++) {
            for (int j = 0; j < mFillW; j++) {

               int curBrush = mFills[i][j];
               if (curBrush != 0) {
                  canvas.drawBitmap(mBrushes[curBrush - 1], mBrushes[curBrush - 1].getWidth() * j,
                        bitmap1.getHeight() * i, paint);
               } else {
                  
               }
            }
         }
      }

      super.onDraw(canvas);

   }

   public void setFillArray(int[][] fills, int w, int h) {
      mFills = fills;
      mFillW = w;
      mFillH = h;
      this.invalidate();
   }

}
