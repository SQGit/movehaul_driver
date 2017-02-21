package net.sqindia.movehaul.driver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rey.material.widget.Button;
import com.rey.material.widget.LinearLayout;
import com.sloop.fonts.FontsManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * Created by SQINDIA on 11/8/2016.
 */
public class ProfileActivityBus extends Activity {

    public final static int REQUEST_PROFILE = 1;
    public final static int REQUEST_VEC_INSIDE = 2;
    public final static int REQUEST_VEC_FRONT = 3;
    public final static int REQUEST_VEC_RC = 5;
    public final static int REQUEST_VEC_INS = 6;
    LinearLayout btn_back, lt_vec_rc, lt_vec_ins;
    ImageView iv_profile, iv_vec_inside, iv_vec_front, iv_vec_rc, iv_vec_ins;
    ArrayList<String> selectedPhotos = new ArrayList<>();
    String str_profile_img, str_vec_back, str_vec_front, str_vec_rc, str_vec_ins, str_contact, str_secondary, str_address;
    View view_rc, view_ins;
    TextInputLayout til_contact, til_secondary, til_address;
    EditText et_contact, et_secondary, et_address;
    Button btn_update;
    Typeface tf;
    Snackbar snackbar;
    TextView tv_snack, tv_profile_name;
    Config config;
    String vec_type;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog mProgressDialog;
    String id, token, url_data;
    TextView tv_bk_txt;
    ImageView iv_prf_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_bus);

        FontsManager.initFormAssets(this, "fonts/lato.ttf");
        FontsManager.changeFonts(this);
        tf = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");

        config = new Config();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivityBus.this);
        editor = sharedPreferences.edit();


        vec_type = sharedPreferences.getString("vec_type", "");

        mProgressDialog = new ProgressDialog(ProfileActivityBus.this);
        mProgressDialog.setTitle("Loading..");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        tv_bk_txt = (TextView) findViewById(R.id.textview);

        btn_update = (Button) findViewById(R.id.button_update);
        iv_profile = (ImageView) findViewById(R.id.imageview_profile);

        iv_vec_front = (ImageView) findViewById(R.id.imageview_vechile_front);
        iv_vec_inside = (ImageView) findViewById(R.id.imageview_vechile_inside);
        iv_vec_rc = (ImageView) findViewById(R.id.imageview_vechile_rc);
        iv_vec_ins = (ImageView) findViewById(R.id.imageview_vechile_ins);
        tv_profile_name = (TextView) findViewById(R.id.textview_profile_name);

        btn_back = (LinearLayout) findViewById(R.id.layout_back);
        lt_vec_rc = (LinearLayout) findViewById(R.id.layout_vechile_rc);
        lt_vec_ins = (LinearLayout) findViewById(R.id.layout_vechile_insurence);

        view_rc = findViewById(R.id.view_rc);
        view_ins = findViewById(R.id.view_insurence);

        til_contact = (TextInputLayout) findViewById(R.id.til_contactnumber);
        til_secondary = (TextInputLayout) findViewById(R.id.til_secondary);
        til_address = (TextInputLayout) findViewById(R.id.til_deliveryaddress);

        til_contact.setTypeface(tf);
        til_secondary.setTypeface(tf);
        til_address.setTypeface(tf);

        et_contact = (EditText) findViewById(R.id.edittext_contact);
        et_secondary = (EditText) findViewById(R.id.edittext_secondary);
        et_address = (EditText) findViewById(R.id.edittext_deliveryaddress);
        iv_prf_bg = (ImageView) findViewById(R.id.prof_bg);


        str_contact = sharedPreferences.getString("driver_mobile", "");
        str_secondary = sharedPreferences.getString("driver_mobile2", "");

        et_contact.setText(str_contact.substring(3, str_contact.length()));
        tv_profile_name.setText(sharedPreferences.getString("driver_name", ""));
        et_secondary.setText(str_secondary.substring(3, str_secondary.length()));

        if (!(sharedPreferences.getString("driver_address", "").equals(""))) {
            et_address.setText(sharedPreferences.getString("driver_address", ""));
        }

        et_address.requestFocus();

        if (!config.isConnected(ProfileActivityBus.this)) {
            snackbar.show();
            tv_snack.setText("Please Connect Internet and Try again");
        }

        snackbar = Snackbar
                .make(findViewById(R.id.top), "Network Error! Please Try Again Later.", Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        tv_snack = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        tv_snack.setTextColor(Color.WHITE);
        tv_snack.setTypeface(tf);

        id = sharedPreferences.getString("id", "");
        token = sharedPreferences.getString("token", "");

        Log.e("tag", "id:" + id + token);


        if (!sharedPreferences.getString("bus_front", "").equals("")) {

            Log.e("tag", "busrond");

            String img = sharedPreferences.getString("bus_front", "");
            String img2 = sharedPreferences.getString("bus_inside", "");
            String img3 = sharedPreferences.getString("bus_rc", "");
            String img4 = sharedPreferences.getString("bus_ins", "");


            Glide.with(ProfileActivityBus.this).load(Config.WEB_URL + "vehicle_details/" + img).error(R.drawable.bus_front).into(iv_vec_front);
            Glide.with(ProfileActivityBus.this).load(Config.WEB_URL + "vehicle_details/" + img2).error(R.drawable.bus_inside).into(iv_vec_inside);
            Glide.with(ProfileActivityBus.this).load(Config.WEB_URL + "vehicle_details/" + img3).into(iv_vec_rc);
            Glide.with(ProfileActivityBus.this).load(Config.WEB_URL + "vehicle_details/" + img4).into(iv_vec_ins);

        }


        if (!sharedPreferences.getString("driver_image", "").equals("")) {

            String img = sharedPreferences.getString("driver_image", "");

            Glide.with(ProfileActivityBus.this).load(Config.WEB_URL + "driver_details/" + img).into(iv_profile);

        }

        Log.e("tag", "type:" + vec_type);

        if (vec_type.equals("Bus")) {
            tv_bk_txt.setText("Inside");
            iv_vec_inside.setImageResource(R.drawable.bus_inside);
            iv_prf_bg.setBackgroundResource(R.drawable.bus_profile_bg);
            url_data = "busdriver";

        } else if (vec_type.equals("Road")) {
            tv_bk_txt.setText("Side");
            iv_vec_inside.setImageResource(R.drawable.road_side);
            iv_prf_bg.setBackgroundResource(R.drawable.road_assis_bg);
            url_data = "assistance";
        }


        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerIntent intent = new PhotoPickerIntent(ProfileActivityBus.this);
                intent.setPhotoCount(1);
                intent.setColumn(4);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_PROFILE);
            }
        });

        iv_vec_inside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerIntent intent = new PhotoPickerIntent(ProfileActivityBus.this);
                intent.setPhotoCount(1);
                intent.setColumn(4);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_VEC_INSIDE);
            }
        });

        iv_vec_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerIntent intent = new PhotoPickerIntent(ProfileActivityBus.this);
                intent.setPhotoCount(1);
                intent.setColumn(4);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_VEC_FRONT);
            }
        });


        lt_vec_rc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhotoPickerIntent intent = new PhotoPickerIntent(ProfileActivityBus.this);
                intent.setPhotoCount(1);
                intent.setColumn(3);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_VEC_RC);
            }
        });

        lt_vec_ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhotoPickerIntent intent = new PhotoPickerIntent(ProfileActivityBus.this);
                intent.setPhotoCount(1);
                intent.setColumn(3);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_VEC_INS);
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              str_contact = et_contact.getText().toString().trim();
                                              str_secondary = et_secondary.getText().toString().trim();
                                              str_address = et_address.getText().toString().trim();


                                              if (!(str_contact.isEmpty() || str_contact.length() < 9)) {
                                                  if (!(str_secondary.isEmpty() || str_secondary.length() < 9)) {
                                                      if (!(str_address.isEmpty() || str_address.length() < 5)) {
                                                          if (str_profile_img != null || !(sharedPreferences.getString("driver_image", "").equals(""))) {
                                                              if (str_vec_back != null || !(sharedPreferences.getString("truck_back", "").equals(""))) {
                                                                  if (str_vec_front != null || !(sharedPreferences.getString("truck_front", "").equals(""))) {
                                                                      if (str_vec_rc != null || !(sharedPreferences.getString("bus_rc", "").equals(""))) {
                                                                          if (str_vec_ins != null || !(sharedPreferences.getString("bus_ins", "").equals(""))) {

                                                                              if (str_profile_img != null) {
                                                                                  new profile_update().execute();
                                                                              } else {
                                                                                  if (!(sharedPreferences.getString("driver_address", "").equals(et_address.getText().toString()))) {
                                                                                      new profile_update().execute();
                                                                                  } else {

                                                                                      if (str_vec_back != null || str_vec_front != null || str_vec_rc != null || str_vec_ins != null) {
                                                                                          new vechile_update().execute();
                                                                                      } else {
                                                                                          Log.e("tag", "ss");
                                                                                      }

                                                                                      // new vechile_update().execute();
                                                                                  }
                                                                              }

                                                                          } else {
                                                                              snackbar.show();
                                                                              tv_snack.setText("Upload Vechile Insurence");
                                                                          }
                                                                      } else {
                                                                          snackbar.show();
                                                                          tv_snack.setText("Upload Vechile RC");
                                                                      }


                                                                  } else {
                                                                      snackbar.show();
                                                                      tv_snack.setText("Upload Vechile Front Image");
                                                                  }


                                                              } else {
                                                                  snackbar.show();
                                                                  tv_snack.setText("Upload Vechile Inside Image");
                                                              }
                                                          } else {
                                                              snackbar.show();
                                                              tv_snack.setText("Upload Profile Image");
                                                          }
                                                      } else {

                                                          snackbar.show();
                                                          tv_snack.setText("Enter Valid Address");
                                                          et_address.requestFocus();

                                                      }
                                                  } else {
                                                      snackbar.show();
                                                      tv_snack.setText("Enter Valid Secondary Number");
                                                      et_secondary.requestFocus();
                                                  }

                                              } else {

                                                  snackbar.show();
                                                  tv_snack.setText("Enter Valid Contact Number");
                                                  et_contact.requestFocus();

                                              }


                                          }
                                      }

        );


    }

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_PROFILE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            Log.d("tag", "img: " + selectedPhotos.get(0));
            str_profile_img = selectedPhotos.get(0);
            //Picasso.with(ProfileActivity.this).load(new File(str_profile_img)).into(iv_profile);
            Glide.with(ProfileActivityBus.this).load(new File(str_profile_img)).into(iv_profile);
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_VEC_INSIDE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            Log.d("tag", "img: " + selectedPhotos.get(0));
            str_vec_back = selectedPhotos.get(0);
            Glide.with(ProfileActivityBus.this).load(new File(str_vec_back)).into(iv_vec_inside);
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_VEC_FRONT) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            Log.d("tag", "img: " + selectedPhotos.get(0));
            str_vec_front = selectedPhotos.get(0);
            Glide.with(ProfileActivityBus.this).load(new File(str_vec_front)).into(iv_vec_front);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_VEC_RC) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            Log.d("tag", "img: " + selectedPhotos.get(0));
            str_vec_rc = selectedPhotos.get(0);
            Glide.with(ProfileActivityBus.this).load(new File(str_vec_rc)).centerCrop().into(iv_vec_rc);
            view_rc.setVisibility(View.GONE);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_VEC_INS) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            Log.d("tag", "img: " + selectedPhotos.get(0));
            str_vec_ins = selectedPhotos.get(0);
            Glide.with(ProfileActivityBus.this).load(new File(str_vec_ins)).centerCrop().into(iv_vec_ins);
            view_ins.setVisibility(View.GONE);
        }
    }


    public class profile_update extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("tag", "reg_preexe");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String json = "", jsonStr = "";

            try {

                //driver/driverupdate
                String responseString = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Config.WEB_URL + url_data + "/driverupdate");
                Log.e("tag", "ss:" + Config.WEB_URL + "busdriver/driverupdate");

                httppost.setHeader("driver_mobile_pri", "+91" + str_contact);
                httppost.setHeader("driver_mobile_sec", "+91" + str_secondary);
                httppost.setHeader("driver_address", str_address);
                //{"id":"10000","sessiontoken":"fkjdshfjdsfhkjdfkgdgfdgfuau"

                httppost.setHeader("id", id);
                httppost.setHeader("sessiontoken", token);


                try {

                    JSONObject jsonObject = new JSONObject();


                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (str_profile_img != null) {
                        entity.addPart("driverimage", new FileBody(new File(str_profile_img), "image/jpeg"));
                        Log.e("tag", "img: if ");
                        httppost.setEntity(entity);
                    } else {
                        Log.e("tag", "img: else ");
                    }


                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.e("tag", response.getStatusLine().toString());
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(r_entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
                return responseString;


            } catch (Exception e) {
                Log.e("InputStream0", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag", "tag" + s);


            mProgressDialog.dismiss();


            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    String status = jo.getString("status");

                    Log.d("tag", "<-----Status----->" + status);

                    if (status.equals("true")) {


                        String msg = jo.getString("driverimage");
                        String mobile = jo.getString("driver_mobile_pri");
                        String mobile2 = jo.getString("driver_mobile_sec");
                        String address = jo.getString("driver_address");

                        editor.putString("driver_image", msg);
                        editor.putString("driver_mobile", mobile);
                        editor.putString("driver_mobile", mobile2);
                        editor.putString("driver_address", address);
                        editor.commit();


                        if (str_vec_back != null || str_vec_front != null || str_vec_rc != null || str_vec_ins != null) {
                            new vechile_update().execute();
                        }


                    } else {
                        snackbar.show();
                        tv_snack.setText("Network Error! Please Try Again Later.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag", "nt" + e.toString());
                    snackbar.show();
                    tv_snack.setText("Network Error! Please Try Again Later.");
                }
            } else {
                snackbar.show();
                tv_snack.setText("Network Error! Please Try Again Later.");
            }

        }

    }


    public class vechile_update extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("tag", "reg_preexe");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String json = "", jsonStr = "";

            try {

                //driver/driverupdate
                String responseString = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Config.WEB_URL + url_data + "/vehicleupdate");
                Log.e("tag", "ss:" + Config.WEB_URL + "busdriver/vehicleupdate");


                httppost.setHeader("id", id);
                httppost.setHeader("sessiontoken", token);


                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (str_vec_front != null) {
                        entity.addPart("vehiclefront", new FileBody(new File(str_vec_front), "image/jpeg"));
                    }
                    if (str_vec_back != null) {
                        entity.addPart("vehicleback", new FileBody(new File(str_vec_back), "image/jpeg"));
                    }

                    if (str_vec_rc != null) {
                        entity.addPart("vehicletitle", new FileBody(new File(str_vec_rc), "image/jpeg"));
                    }
                    if (str_vec_ins != null) {
                        entity.addPart("vehicleinsurance", new FileBody(new File(str_vec_ins), "image/jpeg"));
                    }


                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.e("tag", response.getStatusLine().toString());
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(r_entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
                return responseString;


            } catch (Exception e) {
                Log.e("InputStream1", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag", "tag" + s);


            mProgressDialog.dismiss();


            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    String status = jo.getString("status");
                    Log.d("tag", "<-----Status----->" + status);

                    if (status.equals("true")) {


                        editor.putString("profile", "success");
                        editor.putString("bus_front", jo.getString("vehiclefront"));
                        editor.putString("bus_inside", jo.getString("vehicleback"));
                        editor.putString("bus_rc", jo.getString("vehicletitle1"));
                        editor.putString("bus_ins", jo.getString("vehicleinsurance1"));
                        editor.commit();

                        finish();
                    } else {
                        snackbar.show();
                        tv_snack.setText("Network Error! Please Try Again Later.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("tag", "nt" + e.toString());
                    snackbar.show();
                    tv_snack.setText("Network Error! Please Try Again Later.");
                }
            } else {
                snackbar.show();
                tv_snack.setText("Network Error! Please Try Again Later.");
            }

        }

    }


}