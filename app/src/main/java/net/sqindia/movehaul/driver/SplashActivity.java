package net.sqindia.movehaul.driver;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sloop.fonts.FontsManager;
import com.wang.avi.AVLoadingIndicatorView;

public class SplashActivity extends Activity {
    Button btn_register, btn_login;
    ImageView truck_icon, logo_icon, bg_icon;
    LinearLayout lt_bottom;
    boolean isBottom = true;
    int is = 0;
    Config config;
    AVLoadingIndicatorView av_loader;

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        return width;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = display.getHeight();
        return height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        FontsManager.initFormAssets(this, "fonts/lato.ttf");       //initialization
        FontsManager.changeFonts(this);


        config = new Config();

        getDeviceWidth(this);
        Log.e("tag", "width0" + getDeviceWidth(this));
        final float width = getDeviceWidth(this);
        float widthby = (float) (getDeviceWidth(this) / 3.4);
        Log.e("tag", "width1" + widthby);


        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        truck_icon = (ImageView) findViewById(R.id.truck_icon);
        bg_icon = (ImageView) findViewById(R.id.bg_icon);
        logo_icon = (ImageView) findViewById(R.id.logo_ico);
        lt_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        av_loader = (AVLoadingIndicatorView) findViewById(R.id.loader);

        av_loader.setVisibility(View.GONE);


        truck_icon.animate().translationX(width / (float) 1.65).setDuration(1700).withLayer();

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1500);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(1700);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        bg_icon.setAnimation(fadeIn);
        logo_icon.setAnimation(fadeIn);

        final float height = getDeviceHeight(this);

        final TranslateAnimation anim_btn_b2t = new TranslateAnimation(0, 0, height + lt_bottom.getHeight(), lt_bottom.getHeight());
        anim_btn_b2t.setDuration(1400);
        lt_bottom.setAnimation(anim_btn_b2t);


        final TranslateAnimation anim_btn_t2b = new TranslateAnimation(0, 0, lt_bottom.getHeight(), height + lt_bottom.getHeight());
        anim_btn_t2b.setDuration(1700);
        anim_btn_t2b.setFillAfter(false);

        final TranslateAnimation anim_truck_c2r = new TranslateAnimation(0, width, 0, 0);
        anim_truck_c2r.setDuration(2000);
        anim_truck_c2r.setFillAfter(false);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new check_internet().execute();
            }
        }, 1300);




        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                lt_bottom.startAnimation(anim_btn_t2b);
                truck_icon.startAnimation(anim_truck_c2r);
                bg_icon.setAnimation(fadeOut);

                // truck_icon.animate().translationX(width+400).setDuration(1700).withLayer();
                // bg_icon.animate().translationY(100).setDuration(1700);
                // lt_bottom.animate().translationY(400).setDuration(1700).withLayer();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent isd = new Intent(SplashActivity.this, LoginActivity.class);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim1, R.anim.anim2).toBundle();
                        startActivity(isd, bndlanimation);


                        /*Intent intent = new Intent(Splash_screen.this, LoginActivity.class);
                        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(bg_icon, 0,
                                0, truck_icon.getWidth(), truck_icon.getHeight());
                        startActivity(intent, options.toBundle());*/

                    }
                }, 1000);

            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lt_bottom.startAnimation(anim_btn_t2b);
                truck_icon.startAnimation(anim_truck_c2r);
                bg_icon.setAnimation(fadeOut);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent isd = new Intent(SplashActivity.this, RegisterActivity.class);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim1, R.anim.anim2).toBundle();
                        startActivity(isd, bndlanimation);

                    }
                }, 1000);

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("tag", "ds+" + is);
    }


    public class check_internet extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("tag","reg_preexe");
          //  av_loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                boolean isconnected = config.isConnected(SplashActivity.this);

                if(isconnected){
                    return "true";
                }
                else{
                    return "false";
                }

            } catch (Exception e) {
                Log.e("InputStream", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag","net:"+s);

            if(s.equals("true")){
                av_loader.setVisibility(View.GONE);
            }
            else if(s.equals("false")){
               av_loader.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"splash",Toast.LENGTH_LONG).show();
            }



        }

    }

}