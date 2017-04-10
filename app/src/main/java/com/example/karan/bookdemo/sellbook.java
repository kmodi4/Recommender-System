package com.example.karan.bookdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class sellbook extends AppCompatActivity implements MyServer {

    private EditText isbn, btitle, auth, bedition,
            bcondi, publisher, pages, oriprice, yprice, desc, phno;
    private static final String uploadUrl = MyServerUrl+"addBook";
    RequestQueue mQueue11;
    private ProgressDialog pDialog;
    String manual="";
    int Req_Code=100,Result_Code=50;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMG = 1;
    private ImageView imageView;
    String encodedString;
    String fileName;
    Bitmap myImg;
    Button submit;
    TextWatcher isbnWatcher;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String username,genre="other";
    String imagelink = "";

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
        submit = (Button) findViewById(R.id.sub);

        sharedPreferences = getSharedPreferences("UserDetail", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("sellerid","");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyconnect();
            }
        });




         isbnWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // textView.setVisibility(View.VISIBLE);
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 10 || s.length() == 13) {
                     fetchdetails(isbn.getText().toString());
                }
            }
        };

        isbn.addTextChangedListener(isbnWatcher);


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
               imageDialog();
            }
        });

        isbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isbnDialog();
            }
        });

    }

    public void isbnDialog(){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("ISBN Number Option");
        myAlertDialog.setMessage("Select Input From");
        myAlertDialog.setPositiveButton("Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        myAlertDialog.setNegativeButton("Scanner", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent i2 = new Intent(sellbook.this,ZxingDemo.class);
                startActivityForResult(i2,Req_Code);
            }
        });
        myAlertDialog.show();
    }

    public void imageDialog(){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Image Option");
        myAlertDialog.setMessage("Select Image From");
        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    loadImagefromGallery();
            }
        });
        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  loadfromcamera();
            }
        });
        myAlertDialog.show();
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void loadfromcamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myImg = null;
        if (requestCode == Req_Code) {

            if (resultCode == Result_Code) {
                if (data.getExtras().containsKey("decode")) {
                    isbn.setText(data.getExtras().getString("decode"));
                }
            }

        }
                else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                    myImg = (Bitmap) data.getExtras().get("data");
                try {
                    imageView.setImageBitmap(myImg);
                }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(),"Coudn't fetch image",Toast.LENGTH_SHORT).show();
                }

                } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                try {
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    String picturePath = "";
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();
                    }

                    String fileNameSegments[] = picturePath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];
                    myImg = BitmapFactory.decodeFile(picturePath);
                    imageView.setImageBitmap(myImg);

                }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(),"Coudn't fetch image",Toast.LENGTH_SHORT).show();
                }
                }
            else {
                Toast.makeText(getApplicationContext(),"Coudn't fetch image",Toast.LENGTH_SHORT).show();
            }
               if (myImg != null) {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {

                               ByteArrayOutputStream stream = new ByteArrayOutputStream();
                               myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
                               byte[] byte_arr = stream.toByteArray();
                               encodedString = Base64.encodeToString(byte_arr, 0);

                       }
                   }).start();

               }
            }


    public void fetchdetails(String isbnNo){

        startprogress();

        String myurl = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbnNo;

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                myurl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            JSONArray array = response.getJSONArray("items");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                                String title = volumeInfo.getString("title");
                                btitle.setText(title);

                                JSONArray authors = volumeInfo.getJSONArray("authors");
                                String author = authors.getString(0);
                                auth.setText(author);

                                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                                imagelink = imageLinks.getString("smallThumbnail");
                                if (!(imagelink.equals(""))){

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    myImg = Glide.with(sellbook.this).load(imagelink).asBitmap().into(-1,-1).get();
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                                    byte[] byte_arr = stream.toByteArray();
                                                    encodedString = Base64.encodeToString(byte_arr, 0);

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            imageView.setImageBitmap(myImg);
                                                        }
                                                    });
                                                } catch (InterruptedException | ExecutionException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();

                                }

                                int pagecount = volumeInfo.getInt("pageCount");
                                pages.setText(String.valueOf(pagecount));
                                String publish = volumeInfo.getString("publisher");
                                publisher.setText(publish);
                                String description = volumeInfo.getString("description");
                                desc.setText(description);
                                String cat = "other";
                                if(volumeInfo.getJSONArray("categories")!=null)
                                    cat  = volumeInfo.getJSONArray("categories").getString(0);
                                genre = cat;
                            }

                            } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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

            if (getstr(oriprice).equals("") || getstr(yprice).equals("") || getstr(isbn).equals("") ||  getstr(btitle).equals("") ||
                    getstr(auth).equals("") ||  getstr(bedition).equals("")  ||  getstr(bcondi).equals("") ||  getstr(publisher).equals("")
                    ||  getstr(pages).equals("") ||  getstr(desc).equals("") || encodedString.equals("") ) {
                Toast.makeText(getApplicationContext(),"Empty Fileds",Toast.LENGTH_LONG).show();

            } else {
                startprogress();
                JSONObject jo = new JSONObject();
                try {

                    jo.put("Username", username);
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
                    jo.put("genre",genre);
                        jo.put("base64", encodedString);
                        jo.put("fromgoogle",false);

                        Log.e("json:",jo.toString());

            }catch(JSONException e){
                e.printStackTrace();
            }

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                    uploadUrl,
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
                                    i.putExtra("refresh",true);
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
                    Log.e("err", error.getMessage());
                    Toast.makeText(getApplicationContext(), "Unknow Error", Toast.LENGTH_SHORT).show();

                }

            });


            mQueue11.add(myReq);
        }
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
