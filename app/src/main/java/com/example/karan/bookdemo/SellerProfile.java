package com.example.karan.bookdemo;


import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class SellerProfile extends AppCompatActivity implements MyServer {
    String seller = "";
    String user = "";
    String name,email,adress,about,phNo;
    TextView tv,sname,semail,sphone,sadress,sabout;
    private RequestQueue mqueue;
    private AlertDialog dialog;
    private static  String url = MyServerUrl+"getUser/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        tv = (TextView) findViewById(R.id.username);
        sname = (TextView) findViewById(R.id.sellername);
        semail = (TextView) findViewById(R.id.selleremailid);
        sphone = (TextView) findViewById(R.id.sellerNo);
        sadress = (TextView) findViewById(R.id.sellerAdress);

        MyVolley.init(this);
        mqueue = MyVolley.getRequestQueue();

        Bundle b = getIntent().getExtras();
        if (b!=null){
            if(b.containsKey("seller")){
                seller = b.getString("seller");
                tv.setText(seller);
                url+=seller;
                dialog = new SpotsDialog(this, R.style.Custom2);
                dialog.show();
                VolleyConnect();
            }
            else if (b.containsKey("username")){
                user = b.getString("username");
                url+=user;
                tv.setText(user);
            }

        }
    }

    public void VolleyConnect(){

        JsonObjectRequest mreq = new JsonObjectRequest(url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                     dialog.dismiss();
                    JSONObject jo;
                    try {
                        jo = response;
                         name = jo.getString("Name");
                         email = jo.getString("EmailID");
                         phNo = jo.getString("Phoneno");
                         //adress = jo.getString("");
                        adress = "";
                        sname.setText(name);
                        semail.setText(email);
                        sphone.setText(phNo);
                        sadress.setText("Thaltej,Ahmedabad");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Log.e("VolleyErrrror",error.getMessage());
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Couldn't fetch data",Toast.LENGTH_SHORT).show();
            }
        });

        mqueue.add(mreq);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
