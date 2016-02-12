package com.example.jk.musicfromheart;


import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btnS;
    private MediaPlayer mediaPlayer;
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        //获取屏幕分辨率
       Display mdisplay =  getWindowManager().getDefaultDisplay();
        Log.d("tag_duan",mdisplay.getWidth()+"");
        Log.d("tag_duan",mdisplay.getHeight()+"");
        */
        btn1 = (Button) findViewById(R.id.M1);
        btn2 = (Button) findViewById(R.id.M2);
        btn3 = (Button) findViewById(R.id.M3);
        btn4 = (Button) findViewById(R.id.M4);
        btn5 = (Button) findViewById(R.id.M5);
        btn6 = (Button) findViewById(R.id.M6);
        btn7 = (Button) findViewById(R.id.M7);
        btnS = (Button) findViewById(R.id.MS);
        mediaPlayer = new MediaPlayer();
        // 按钮监听
       btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btnS.setOnClickListener(this);
        //右边滑动启动练习模式
        RelativeLayout r1 = (RelativeLayout)findViewById(R.id.r1 );
        r1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getX() - e2.getX() > 10){
                    Intent intent = new Intent(MainActivity.this,TrainMod.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
    } // create end
 @Override
 public void onClick(View v){
     switch (v.getId()){
         case (R.id.M1):        play(R.raw.a);break;
         case (R.id.M2):        play(R.raw.b);break;
         case (R.id.M3):        play(R.raw.c);break;
         case (R.id.M4):        play(R.raw.d);break;
         case (R.id.M5):        play(R.raw.e);break;
         case (R.id.M6):        play(R.raw.f);break;
         case (R.id.M7):        play(R.raw.g);break;
         default:                  break;
     }
 }

   private void play(int resouce){
        try{
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(MainActivity.this,resouce);
           // mediaPlayer.prepare();  //不用，MediaPlayer.create里面已经调用过了
            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "a mistake happen in playing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void  onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            //mediaPlayer = null;
        }
    }
}//calss end
