package com.movhaul.driver;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.rey.material.widget.LinearLayout;
import com.sloop.fonts.FontsManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sqindia on 22-10-2016.
 * login via email
 */

public class Login_Email extends Activity {
    LinearLayout btn_back;
    EditText edtxt_email;
    String str_email;
    TextInputLayout flt_email;
    Button btn_submit;
    Snackbar snackbar;
    ProgressDialog mProgressDialog;
    TextView tv_snack;
    Config config;
    Typeface tf;
    Dialog dialog2;
    Button d2_btn_ok;
    TextView d2_tv_dialog1,d2_tv_dialog2,d2_tv_dialog3,d2_tv_dialog4;
    ImageView btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_phone);
        FontsManager.initFormAssets(this, "fonts/lato.ttf");       //initialization
        FontsManager.changeFonts(this);

        flt_email = (TextInputLayout) findViewById(R.id.float_email);
        btn_back = (LinearLayout) findViewById(R.id.layout_back);
        edtxt_email = (EditText) findViewById(R.id.editTextEmail);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        config = new Config();
        tf = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        flt_email.setTypeface(tf);

        mProgressDialog = new ProgressDialog(Login_Email.this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.wait));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        snackbar = Snackbar
                .make(findViewById(R.id.top), R.string.network, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        tv_snack = (android.widget.TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        tv_snack.setTextColor(Color.WHITE);
        tv_snack.setTypeface(tf);

        if (!config.isConnected(Login_Email.this)) {
            snackbar.show();
            tv_snack.setText(R.string.connect);
        }


        dialog2 = new Dialog(Login_Email.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.driver_bidding_confirm);
        d2_btn_ok = (Button) dialog2.findViewById(R.id.button_ok);
        btn_close = (ImageView) dialog2.findViewById(R.id.button_close);
        d2_tv_dialog1 = (TextView) dialog2.findViewById(R.id.textView_1);
        d2_tv_dialog2 = (TextView) dialog2.findViewById(R.id.textView_2);
        d2_tv_dialog3 = (TextView) dialog2.findViewById(R.id.textView_3);
        d2_tv_dialog4 = (TextView) dialog2.findViewById(R.id.textView_4);
        d2_tv_dialog1.setTypeface(tf);
        d2_tv_dialog2.setTypeface(tf);
        d2_tv_dialog3.setTypeface(tf);
        d2_tv_dialog4.setTypeface(tf);
        d2_btn_ok.setTypeface(tf);
        d2_tv_dialog1.setText(R.string.ad);
        d2_tv_dialog2.setText(R.string.asdf);
        d2_tv_dialog3.setText(R.string.cea);
        d2_tv_dialog4.setVisibility(View.GONE);
        btn_close.setVisibility(View.GONE);
        d2_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            }
        });




        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_email = edtxt_email.getText().toString().trim();

                if (!(str_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(str_email).matches())) {
                   /* Intent i = new Intent(Login_Email.this, LoginOtpActivity.class);
                    startActivity(i);
                    finish();*/
                    new forgot_mobile().execute();
                } else {
                   // edtxt_email.setError("Enter a valid email address!");
                    snackbar.show();
                    tv_snack.setText(R.string.va_em);
                    edtxt_email.requestFocus();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_Email.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }



    /// if user forgot phone user login via mail
    public class forgot_mobile extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("tag","reg_preexe");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String json = "", jsonStr = "";

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("driver_email", str_email);
                json = jsonObject.toString();
                return jsonStr = HttpUtils.makeRequest(Config.WEB_URL + "driveremailotp", json);

            } catch (Exception e) {
                Log.e("InputStream", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag","tag"+s);
            mProgressDialog.dismiss();


            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    String status = jo.getString("status");
                    String msg = getString(R.string.vae);
                    if(jo.has("message")) {
                        msg = jo.getString("message");
                    }
                    Log.d("tag", "<-----Status----->" + status);
                    if (status.equals("true")) {
                        String type = jo.getString("vehicle_type");
                        Log.e("tag","vec:"+type);

                        // String sus_txt = "Thank you for Signing Up MoveHaul.";

                        //Toast.makeText(getApplicationContext(),sus_txt,Toast.LENGTH_LONG).show();

                        Intent i = new Intent(Login_Email.this, LoginOtpActivity.class);
                        i.putExtra("for","email");
                        i.putExtra("data",str_email);
                        i.putExtra("prefix","nil");
                        i.putExtra("vec_type",type);
                        startActivity(i);
                        finish();


                    } else if (status.equals("false")) {


                        if(jo.has("message")) {
                            msg = jo.getString("message");
                        }


                        if (msg.contains("Register with Movehaul first to Generate OTP")) {

                           // Toast.makeText(getApplicationContext(),"Mobile Number Not Registered",Toast.LENGTH_LONG).show();

                            snackbar.show();
                            tv_snack.setText(R.string.no_nm);

                        }
                        else if (msg.contains("Error Occured[object Object]")) {

                            Intent i = new Intent(Login_Email.this, LoginOtpActivity.class);
                            i.putExtra("for","email");
                            i.putExtra("data",str_email);
                            i.putExtra("prefix","nil");
                            startActivity(i);
                            finish();

                        }

                        else if( msg.contains("{\"driver_verification\":\"pending\",\"account_status\":\"inactive\"}")){
                            dialog2.show();
                        }
                        else if(jo.has("driver_verification")){
                            Log.e("tag","ds: "+jo.getString("driver_verification"));
                            Log.e("tag","da: "+jo.getString("account_status"));
                            dialog2.show();
                        }
                        else if(msg.contains("{\"driver_verification\":\"pending\",\"account_status\":\"active\"}")){
                            dialog2.show();
                        }
                        else if(msg.contains("\"message\":{\"driver_verification\":\"pending\",\"account_status\":\"inactive\"")){
                            dialog2.show();
                        }



                        else  {

                           // Toast.makeText(getApplicationContext(),"Please Try Again Later",Toast.LENGTH_LONG).show();
                            snackbar.show();
                            //tv_snack.setText("Mobile Number Not Registered");
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag","nt"+e.toString());
                  //  Toast.makeText(getApplicationContext(),"Network Errror0",Toast.LENGTH_LONG).show();
                    snackbar.show();
                }
            } else {
               // Toast.makeText(getApplicationContext(),"Network Errror1",Toast.LENGTH_LONG).show();
                snackbar.show();
            }

        }

    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Login_Email.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
