package com.example.jk.musicfromheart;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrainMod extends AppCompatActivity implements View.OnClickListener {
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btnS;
    private MediaPlayer mediaPlayer;
    //
   public static final int PRESSONE = 1;
    public  static final int PRESSTWO = 2;
    public static final int PRESSThREE= 3;
    public  static final int PRESSFOUR= 4;
    public static final int PRESSFIVE = 5;
    public  static final int PRESSSIX= 6;
    public static final int PRESSSEVEN = 7;
   public  static final  int PRESSA = 9;
   public  static  final  int PRESSB = 10;
   public static  final  int JSONREPLY = 11;

    private boolean STOP = false;
    private static final int INDEXERRO = 12;
    private  static final int  JSONDATAERRO = 13;
    private  static  final  int COMPLETE = 14;
    private TextView songs;
    private TextView score;
    private  TextView online;
    private GestureDetector gestureDetector;
   private final List<String> items = new ArrayList<String>();  //存储文件目录
    private   StringBuilder musicContent  =  new StringBuilder("");
    private  String jsonResponse;
    private  String fileName = "";
    Thread thread ;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            btn1.setPressed(false);
            btn2.setPressed(false);
            btn3.setPressed(false);
            btn4.setPressed(false);
            btn5.setPressed(false);
            btn6.setPressed(false);
            btn7.setPressed(false);
            switch (msg.what){
                case PRESSONE: btn1.setPressed(true); break;
                case PRESSTWO: btn2.setPressed(true); break;
                case  PRESSThREE: btn3.setPressed(true); break;
                case  PRESSFOUR:   btn4.setPressed(true); break;
                case  PRESSFIVE:   btn5.setPressed(true); break;
                case  PRESSSIX :     btn6.setPressed(true ); break;
                case  PRESSSEVEN:  btn7.setPressed(true); break;
                case  JSONREPLY:    jsonResponse = (String)msg.obj; break;
                case  INDEXERRO :    Toast.makeText(TrainMod.this,"cannot connect to internet to get the index",Toast.LENGTH_SHORT).show(); break;
                case JSONDATAERRO:  Toast.makeText(TrainMod.this,"cannt get the connect to internet to get the music  data!",Toast.LENGTH_SHORT).show(); break;
                case COMPLETE:       saveToLocal(musicContent.toString(),fileName);break;
                default:  break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_mod);
        songs = (TextView)findViewById(R.id.textView_Songs);
        online = (TextView)findViewById(R.id.textView_Online);
        score = (TextView)findViewById(R.id.textVIew_Score);
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
        //点击songs 跳出一个dialog 选择歌曲TrainMod
       //点击里面的歌曲目录中的歌曲，将读取里面的内容到musicContent
        getMusicIndexFormNet(); //提前联网获取数据
        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(thread != null)
                   STOP = true;
                //产生一个dialog  dialog中放置了一个listview  listview的adapter包含相应文件夹里面的文件名称
                final View layout = getLayoutInflater().inflate(R.layout.songs_dialog, (ViewGroup) findViewById(R.id.dialog));
                final Dialog dialog = new Dialog(TrainMod.this);
                dialog.setContentView(layout);
                dialog.setTitle("Choose Music");
                // Log.d("tag_duan", "clicked!");
                ListView listView = (ListView) dialog.findViewById(R.id.listview);
                File fileL = new File("");
                try {
                    fileL = new File(getFilesDir()+"");
                }catch (Exception e){
                    Log.d("tag_duan",",cant get the files");
                }
                Log.d("tag_duan", fileL.getPath());
                final File[] files = fileL.listFiles();
                if(files != null) {
                    items.removeAll(items);
                    //检测文件目录下是否有文件
                     for (File f : files) {
                       items.add(f.getName());
                        Log.d("tag_duan", f.getName());
                 }
                    ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(TrainMod.this,
                            android.R.layout.simple_dropdown_item_1line, items);
                    listView.setAdapter(array_adapter);
                    Log.d("tag_duan", "adapterSet");
                    dialog.show();
                    //本地歌曲设置listview点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //读取文件内容
                            try {
                                Log.d("tag_duan", items.get(position));
                                musicContent = new StringBuilder("");// 清除之前已经保存的数据；
                               // File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + items.get(position));
                                File file = new File(getFilesDir()+"/"+items.get(position));
                                InputStreamReader fr = new InputStreamReader(new FileInputStream(file));
                                BufferedReader bfr = new BufferedReader(fr);
                                String line;
                                while ((line = bfr.readLine()) != null) {
                                    musicContent = musicContent.append(line);
                                    //   Log.d("tag_duan", line);
                                }
                                bfr.close();
                                if(musicContent.length() == 0)
                                  Toast.makeText(TrainMod.this, "this music has not content",Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("tag_duan", "file not find");
                            }
                            dialog.dismiss();
                        }
                    });
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            File f = new File(getFilesDir()+"/"+items.get(position));
                            if(f.exists())
                                f.delete();
                            return false;
                        }
                    });
                }
            }
        });
        //点击online 读取服务器上的歌曲目录
        //选择歌曲
    online.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(thread != null)
                STOP = true;
            final View layout = getLayoutInflater().inflate(R.layout.songs_dialog, (ViewGroup) findViewById(R.id.dialog));
            final Dialog dialog = new Dialog(TrainMod.this);
            dialog.setContentView(layout);
            dialog.setTitle("Online Music");
            ListView listView = (ListView) dialog.findViewById(R.id.listview);
            //从网络上读取目录文件
            getMusicIndexFormNet();
            //解析JSON数据
            //设置listview
            if(jsonResponse != null && ! jsonResponse .equals("")) {
                final String [] musicIndexArray;
                musicIndexArray = parseJSON(jsonResponse);
                ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(TrainMod.this,
                        android.R.layout.simple_dropdown_item_1line, musicIndexArray);
                listView.setAdapter(array_adapter);
                dialog.show();

                //listview 设置监听
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //点击之后建立连接，从网络获取文件内容
                        getMusicContent(musicIndexArray[position]);
                        fileName =  musicIndexArray[position];
                        dialog.dismiss();
                    }
                });
            }
        }
    });


        //采用多线程，使得根据内容实现节拍效果（根据内容，提示按钮）
        //长按S按钮 即可开始练习模式
      btnS = (Button)findViewById(R.id.MS);
        btnS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                STOP = false;
                Log.d("tag_duan", "Long Clicked");
                    thread = new Thread(runnable);
                    thread.start();
                return false;
            }
        });

        //滑动切换效果
        RelativeLayout r1 = (RelativeLayout)findViewById(R.id.r1 );
        r1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        gestureDetector = new GestureDetector(TrainMod.this, new GestureDetector.OnGestureListener() {
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
                if(e2.getX() - e1.getX() > 10){

                    Intent intent = new Intent(TrainMod.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
    }// create end
    //获取服务器上的歌曲目录
    private  void getMusicIndexFormNet(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new  URL("http://115.28.179.124/musicIndex.json");
                    HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(4000);
                    connection.setConnectTimeout(500);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader( new InputStreamReader(in));
                    StringBuilder response= new StringBuilder();
                    String line;
                    while((line = reader.readLine() )!= null){
                        response.append(line);    //获取到了json格式文件内容
                    }
                    reader.close();
                    Message msg = Message.obtain();
                    msg.what = JSONREPLY;
                    msg.obj = response.toString();//通过handler将内容传递回来
                    handler.sendMessage(msg);
                    connection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = INDEXERRO;
                    handler.sendMessage(msg);//通过handler处理无法获取网络时的信息
                    Log.d("tag_duan", "cant get the index form internet");
                }
            }
        });
        thread.start();
    }
    // 解析json 目录文件
    private String [] parseJSON(String jsonData){
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            String [] musicNameArray = new String[jsonArray.length()];
            for(int i = 0;i < jsonArray.length();i++){
                Log.d("tag_duan","json Array length is :"+jsonArray.length()+"");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String musicName = jsonObject.getString("musicName");
                Log.d("tag_duan","musicName is :"+musicName);
                musicNameArray [i] = musicName;
            }
            return musicNameArray;
        }catch (Exception e){
            e.printStackTrace();
            Log.d("tag_duan","cant parse the jsonData");
        }
     return null;
    }
    //从网络上获取文件内容
    private void  getMusicContent(final String musicName){
              Thread thread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                      try {
                          URL url = new URL("http://115.28.179.124/" + musicName+".html");
                          musicContent = new StringBuilder("");  //刷新掉原有的数据
                          HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                          connection.setRequestMethod("GET");
                          connection.setConnectTimeout(4000);
                          connection.setReadTimeout(500);
                          InputStream in  =   connection.getInputStream();
                          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                          String line;
                          while((line = reader.readLine())!= null){
                              musicContent.append(line);
                          }
                          connection.disconnect();
                          Message msg = Message.obtain();
                          msg.what = COMPLETE;
                          handler.sendMessage(msg);
                      }catch (Exception e){
                          e.printStackTrace();
                          Log.d("tag_duan", "cant link to " + musicName);
                          Message msg = Message.obtain();
                          msg.what = JSONDATAERRO;
                          handler.sendMessage(msg);
                      }
                  }
              });
        thread.start();
    }
    //runnable + handler具体实现延时 提示点击按钮
    Runnable runnable = new Runnable(){
        public void run() {
             String line = musicContent.toString();
           // Log.d("tag_duan",musicContent.toString());

            char[] s = line.toCharArray();
            Log.d("tag_duan", s.length + "");
         //   Log.d("tag_duan", Arrays.toString(s));

      for (char m : s) {
          while (!STOP) {
          Log.d("tag_duan", m + "");
          Message msg = Message.obtain();
          switch (m) {
              case '1':
                  msg.what = PRESSONE;
                  handler.sendMessage(msg);
                  break;
              case '2':
                  msg.what = PRESSTWO;
                  handler.sendMessage(msg);
                  break;
              case '3':
                  msg.what = PRESSThREE;
                  handler.sendMessage(msg);
                  break;
              case '4':
                  msg.what = PRESSFOUR;
                  handler.sendMessage(msg);
                  break;
              case '5':
                  msg.what = PRESSFIVE;
                  handler.sendMessage(msg);
                  break;
              case '6':
                  msg.what = PRESSSIX;
                  handler.sendMessage(msg);
                  break;
              case '7':
                  msg.what = PRESSSEVEN;
                  handler.sendMessage(msg);
                  break;
              case 'a':
                  try {
                      Thread.sleep(800);
                      msg.what = PRESSA;
                      handler.sendMessage(msg);
                  } catch (Exception e) {
                      e.printStackTrace();
                      Log.d("tag_duan", "thread sleep failed");
                  }
                  break;
              case 'b':
                  try {
                      Thread.sleep(400);
                      msg.what = PRESSB;
                      handler.sendMessage(msg);
                  } catch (Exception e) {
                      e.printStackTrace();
                      Log.d("tag_duan", "thread sleep failed");
                  }
                  break;
              default:
                  break;
          }
          break;
      }
  }
        }
    };

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
            mediaPlayer = MediaPlayer.create(TrainMod.this, resouce);
            // mediaPlayer.prepare();  //不用，MediaPlayer.create里面已经调用过了
            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(TrainMod.this, "a mistake happen in playing", Toast.LENGTH_SHORT).show();
        }
    }
    //将内容保存到本地
    private void saveToLocal(String musicContent,String fileName){
        // 点击歌曲名称之后，保存到本地
        try {
            FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(musicContent.toString());
            Log.d("tag_duan", "write complete");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("tag_duan", "cant write to the File");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void  onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}// class end
