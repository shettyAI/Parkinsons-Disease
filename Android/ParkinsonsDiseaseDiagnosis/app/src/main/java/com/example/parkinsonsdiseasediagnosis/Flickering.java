package com.example.parkinsonsdiseasediagnosis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Flickering extends Activity {

    DrawingView dv ;
    private Paint mPaint;
    private Boolean oldState = true;

    ArrayList<float []> list = new ArrayList<>();

    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;
    ArrayList<float[]> staticList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this);
        staticList = (ArrayList<float[]>) getIntent().getExtras().getSerializable("static");
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Canvas  mCanvas1;
        private Canvas  mCanvasT;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            Log.d("YO","onSizeChange");
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas1 = new Canvas(mBitmap);
            mCanvasT = new Canvas(mBitmap);
            Paint p = new Paint();
            p.setTextSize(100);
            mCanvasT.drawText("Dynamic Spiral Test",20,150,p);
            Log.d("YO",w+","+h);
            int W = mBitmap.getWidth();
            int H = mBitmap.getHeight();
            Log.d("YO",W+","+H);
            int x = 0, y = 0;
            Double r = new Double((int)(W/30));
            Double theta = new Double(0);
            Double count = new Double(0);
            while (count <110) {
                x = (int)(W / 2 + r * Math.cos(Math.PI * theta / 180));
                y = (int)(H / 2 - r * Math.sin(Math.PI * theta / 180));
                if(x>=W || y>H)
                    continue;
                mCanvas1.drawCircle(x,y,15,p);
                r += 0.38;
                count += 0.09;
                theta += 0.9;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            float[] f = {x,y,System.currentTimeMillis()};
            list.add(f);
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
                float[] f = {x,y, System.currentTimeMillis()};
                list.add(f);
            }
            Random rand = new Random();
            if(rand.nextInt(50)>10) {
                Paint p = new Paint();
                p.setColor(Color.WHITE);
                int W = mBitmap.getWidth();
                int H = mBitmap.getHeight();
                Log.d("YO",W+","+H);
                int X = 0, Y = 0;
                Double r = new Double((int)(W/30));
                Double theta = new Double(0);
                Double count = new Double(0);
                while (count <110) {
                    X = (int)(W / 2 + r * Math.cos(Math.PI * theta / 180));
                    Y = (int)(H / 2 - r * Math.sin(Math.PI * theta / 180));
                    if(x>=W || y>H)
                        continue;
                    mCanvas1.drawCircle(X,Y,15,p);
                    r += 0.38;
                    count += 0.09;
                    theta += 0.9;
                }
            }
            else {
                Paint p = new Paint();
//                p.setColor(Color.WHITE);
                int W = mBitmap.getWidth();
                int H = mBitmap.getHeight();
                Log.d("YO",W+","+H);
                int X = 0, Y = 0;
                Double r = new Double((int)(W/30));
                Double theta = new Double(0);
                Double count = new Double(0);
                while (count <110) {
                    X = (int)(W / 2 + r * Math.cos(Math.PI * theta / 180));
                    Y = (int)(H / 2 - r * Math.sin(Math.PI * theta / 180));
                    if(x>=W || y>H)
                        continue;
                    mCanvas1.drawCircle(X,Y,15,p);
                    r += 0.38;
                    count += 0.09;
                    theta += 0.9;
                }
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Do you want to submit?")
                    .setTitle("Accept and submit for testing?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("qwe",staticList.size()+"");
//                    staticList.addAll(list);
//                    Log.d("qwe",staticList.size()+"");
//                    for(float[] i : staticList)
//                        Log.d("qwe",i.toString());
                    Intent i = new Intent(Flickering.this,PointStabilityTest.class);
                    i.putExtra("static",staticList);
                    i.putExtra("dynamic",list);
                    startActivity(i);
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Flickering.this,Flickering.class);
                    startActivity(i);
                    dialog.dismiss();
                    finish();
                }
            });


            builder.show();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}