package com.example.karan.bookdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class sellbook extends AppCompatActivity {

    private EditText isbn, btitle, auth, bedition,
            bcondi, publisher, pages, oriprice, yprice, desc, phno;
    private static final String LOGIN_URL = "http://kmodi4.esy.es/BookDemo/login.php";    //url of your php file
    RequestQueue mQueue11;
    private ProgressDialog pDialog;
    String manual="";
    int Req_Code=100,Result_Code=50;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMG = 1;
    private ImageView imageView;
    String encodedString;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellbook);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar3);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        imageView = (ImageView) findViewById(R.id.bookimg);
        isbn = (EditText) findViewById(R.id.isbn);
        btitle = (EditText) findViewById(R.id.btitle);
        auth = (EditText) findViewById(R.id.auth);
        bedition = (EditText) findViewById(R.id.beditiion);
        bcondi = (EditText) findViewById(R.id.bcondi);
        publisher = (EditText) findViewById(R.id.publisher);
        pages = (EditText) findViewById(R.id.pages);
        oriprice = (EditText) findViewById(R.id.Oprice);
        yprice = (EditText) findViewById(R.id.yprice);
        desc = (EditText) findViewById(R.id.desc);
        phno = (EditText) findViewById(R.id.phno);


        MyVolley.init(this);
        mQueue11 = MyVolley.getRequestQueue();
        Bundle b =getIntent().getExtras();
        if(b!=null){
            if(b.containsKey("set")){
                manual = b.getString("set");
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

      /*  isbn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

               // if(manual.equals("")) {
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    Intent i = new Intent(sellbook.this,ZxingDemo.class);
                    startActivityForResult(i,Req_Code);
               // }
                /*else{
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    manual="";
                }

                return false;
            }
        });*/

        isbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(sellbook.this,ZxingDemo.class);
                startActivityForResult(i,Req_Code);
            }
        });



    }

    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Req_Code) {

            if (resultCode == Result_Code) {
                if (data.getExtras().containsKey("decode")) {
                    isbn.setText(data.getExtras().getString("decode"));
                    Toast.makeText(getApplicationContext(), "Detected Format " + data.getExtras().getString("format"), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            String fileNameSegments[] = picturePath.split("/");
            fileName = fileNameSegments[fileNameSegments.length - 1];
            //fileName="karan.jpeg";

            Bitmap myImg = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, 0);
           // t1.setText(fileName);
        }
    }

    public void startprogress(){
        pDialog = new ProgressDialog(sellbook.this);

        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public String getstr(EditText et) {
        String st1 = et.getText().toString();
        return st1;
    }

    private void volleyconnect() {


        JSONObject jo = new JSONObject();
        try {
            jo.put("isbn", getstr(isbn));
            jo.put("title", getstr(btitle));
            jo.put("author", getstr(auth));
            jo.put("edition", getstr(bedition));
            jo.put("condition", getstr(bcondi));
            jo.put("publisher", getstr(publisher));
            jo.put("pages", Integer.parseInt(getstr(pages)));
            jo.put("originalprice", Integer.parseInt(getstr(oriprice)));
            jo.put("yourprice", Integer.parseInt(getstr(yprice)));
            jo.put("desc", getstr(desc));
            jo.put("phno", Integer.parseInt(getstr(phno)));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                LOGIN_URL,
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            int success = response.getInt("success");
                            if (success == 1) {
                                Intent i = new Intent(sellbook.this, MainActivity.class);
                                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //i.putExtra("name", username);
                                startActivity(i);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Unknow Error", Toast.LENGTH_SHORT).show();

            }

        });


        mQueue11.add(myReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sellbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}