package com.example.karan.bookdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;



public class Product_detail extends AppCompatActivity implements MyServer {

    private ImageView iv;
    private TextView tv,op,yp,sellerid,dis;
    private TextView pisbn,pcond,pauth,ppub,ppages,pdesc,pedi,features;
    int discount;
    int yourprice;
    int originalprice,pages;
    String sellername,title;
    SharedPreferences sharedPreferences,sharedPreferences2;
    boolean status;
    private String isbn,author,edition,condition,publisher,desc,genre,b_id;
    private RequestQueue mqueue;
    private android.app.AlertDialog dialog;
    private boolean is_desc_clicked = false;
    private LinearLayout collapsablelayout,cflayout;
    private RecyclerView sim_recyclerView,cf_recycleView;
    private Radpater sim_radpater,cf_radpater;
    private List<listinfo> SimilarBookdata,CFBookData;
    private RatingBar rb;
    private ProgressBar progressBar,cfPrgressBar;
    private static final String Url = MyServerUrl+"topN";
    private static final String Recent_Viewed = "Recenetly_viewed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        iv = (ImageView) findViewById(R.id.imageView4);
        tv = (TextView) findViewById(R.id.bktitle);
        op = (TextView) findViewById(R.id.op);
        yp = (TextView) findViewById(R.id.yp);
        sellerid = (TextView) findViewById(R.id.sellerid);
        dis = (TextView) findViewById(R.id.dis);

        pisbn = (TextView) findViewById(R.id.pisbn);
        pauth = (TextView) findViewById(R.id.pauthor);
        ppub = (TextView) findViewById(R.id.ppub);
        ppages = (TextView) findViewById(R.id.ppages);
        pcond = (TextView) findViewById(R.id.pcondition);
        pdesc = (TextView) findViewById(R.id.pdesc);
        pedi = (TextView) findViewById(R.id.pedition);

        rb = (RatingBar) findViewById(R.id.ratingbar);
        features = (TextView) findViewById(R.id.features);
        collapsablelayout = (LinearLayout) findViewById(R.id.collapsable);
        cflayout = (LinearLayout) findViewById(R.id.cflayout);
        progressBar = (ProgressBar) findViewById(R.id.similar_progress);
        cfPrgressBar = (ProgressBar) findViewById(R.id.cf_progress);

        SimilarBookdata = new ArrayList<>();
        CFBookData = new ArrayList<>();

        MyVolley.init(this.getApplicationContext());
        mqueue = MyVolley.getRequestQueue();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Details");
        }

        Bundle b = getIntent().getExtras();
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        status = sharedPreferences.getBoolean("LStatus",false);
        if(status){
            cflayout.setVisibility(View.VISIBLE);
        }
        if (b != null) {
            final String url = b.getString("image");
            title = b.getString("title");
            sellername = b.getString("seller");
            sellerid.setText(sellername);
            Glide.with(Product_detail.this).load(url).crossFade().into(iv);
            tv.setText(title);
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.sellerview);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status) {
                    Intent i = new Intent(Product_detail.this, SellerProfile.class);
                    i.putExtra("seller", sellername);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Login to get Access",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*dialog = new SpotsDialog(this, R.style.Custom2);
        dialog.show();*/
        VolleySpecs();
        pdesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_desc_clicked){
                    pdesc.setMaxLines(3);
                    is_desc_clicked = false;
                }
                else{
                    pdesc.setMaxLines(100);
                    is_desc_clicked = true;
                }
            }
        });
        collapsablelayout.setVisibility(View.GONE);
        features.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collapsablelayout.getVisibility()==View.GONE)
                {
                    expand();
                    features.setText("Hide Features ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        features.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.up_arrow,0);
                    }
                }
                else
                {
                    collapse();
                    features.setText("Show Features ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        features.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.down_arrow,0);
                    }
                }
            }
        });

        sharedPreferences2 = getSharedPreferences("UserDetail",Context.MODE_PRIVATE);
        sim_recyclerView = (RecyclerView) findViewById(R.id.similarview);
        sim_radpater = new Radpater(Product_detail.this, SimilarBookdata);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(sim_radpater);
        sim_recyclerView.setAdapter(new SlideInRightAnimationAdapter(alphaAdapter));
        //LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        sim_recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        sim_recyclerView.setHasFixedSize(true);
        fetchSimilarItems();

        cf_recycleView = (RecyclerView) findViewById(R.id.cfview);
        cf_radpater = new Radpater(Product_detail.this, CFBookData);
        AlphaInAnimationAdapter alphaAdapter1 = new AlphaInAnimationAdapter(cf_radpater);
        cf_recycleView.setAdapter(new SlideInRightAnimationAdapter(alphaAdapter1));
        //LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        cf_recycleView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        cf_recycleView.setHasFixedSize(true);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                if(fromUser) {
                    new AlertDialog.Builder(Product_detail.this)
                            .setTitle("Rating")
                            .setMessage("Are you sure you want to Update the Rating?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    Log.d("rating", String.valueOf(rating));
                                    updateRating(rating);

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

    }



    class WrapContentLinearLayoutManager extends LinearLayoutManager{

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            }catch (IndexOutOfBoundsException e){
                Log.e("erLL",e.getMessage());
            }


        }
    }

    public void myDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Contact Option");
        myAlertDialog.setMessage("choose  From");
        myAlertDialog.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent i1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8460529123"));
                if (ActivityCompat.checkSelfPermission(Product_detail.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                startActivity(i1);

            }
        });
        myAlertDialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Toast.makeText(getApplicationContext(),"Chat Option",Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(Product_detail.this,ChatList.class);
                i2.putExtra("seller",sellername);
                startActivity(i2);
            }
        });
        myAlertDialog.show();
    }

    public void contactmethod(View view){
        if (status) {
            myDialog();
        }
        else {
            Toast.makeText(getApplicationContext(),"Login to get Access",Toast.LENGTH_SHORT).show();
        }
    }

    public void StopProgress(){
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }



    public void VolleySpecs(){
        String updateUrl = MyServerUrl+"getBook";//"Specs.php";

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = null;
        try {
            jsonObject.put("username",sellername);
            jsonObject.put("title",title);
            jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonArrayRequest mreq1 = new JsonArrayRequest(Request.Method.POST,updateUrl,jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

              // StopProgress();

                if (response!=null){
                        for (int i=0; i< response.length(); i++){
                            JSONObject jo;
                            try {
                                jo = response.getJSONObject(i);
                                b_id = jo.getString("book_id");
                                isbn = jo.getString("isbn");
                                author = jo.getString("author");
                                edition = jo.getString("edition");
                                condition = jo.getString("condition");
                                publisher = jo.getString("publisher");
                                pages = jo.getInt("pages");
                                desc = jo.getString("desc");
                                genre = jo.getString("genre");
                                yourprice = jo.getInt("yourprice");
                                originalprice = jo.getInt("originalprice");
                                discount = ((originalprice - yourprice) * 100) / originalprice;
                                if(jo.has("rating")){
                                    rb.setRating((float)jo.getDouble("rating"));
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    pisbn.setText(isbn);
                    pauth.setText(author);
                    ppub.setText(publisher);
                    pcond.setText(condition);
                    ppages.setText(String.valueOf(pages));
                    pdesc.setText(desc);
                    pedi.setText(edition);
                    yp.setText(String.valueOf(yourprice));
                    op.setText(String.valueOf(originalprice));
                    dis.setText(String.valueOf(discount) + "%");
                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                    String jsonArray  = sharedPreferences2.getString(Recent_Viewed,"[]");
                    String json = "{\"title\":\""+title+"\""+",\"username\":\""+sellername+"\"}";
                    StringBuilder sb = new StringBuilder(jsonArray);
                    JSONArray ja = new JSONArray();
                    if(jsonArray.equals("[]")){
                        jsonArray = jsonArray.substring(0,1)+json+jsonArray.substring(jsonArray.length()-1,jsonArray.length());
                    }
                    else{
                        json = json+",";
                        int index = jsonArray.indexOf('{');
                        jsonArray = jsonArray.substring(0,1)+json+jsonArray.substring(index,jsonArray.length());
                    }
                    try {
                        ja = new JSONArray(jsonArray);

                        Set<recent_items> set = new LinkedHashSet<>();
                        for(int i=0;i<ja.length();i++){
                            recent_items ri = new recent_items();
                            ri.title = ja.getJSONObject(i).getString("title");
                            ri.username = ja.getJSONObject(i).getString("username");
                            set.add(ri);
                        }
                        List<recent_items> list = new ArrayList<>(set);
                       ja = new JSONArray();
                        for(int i=0;i<list.size();i++){
                            JSONObject jo = new JSONObject();
                            jo.put("title",list.get(i).title);
                            jo.put("username",list.get(i).username);
                           ja.put(jo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    editor.putString(Recent_Viewed, ja.toString());
                    editor.apply();
                    if(status){
                        //updatePreference();
                        implicitRatings();
                        implict_mf();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"can't get details",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // StopProgress();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null ) {
                    Log.d("status",String.valueOf(networkResponse.statusCode));
                }
                Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_SHORT).show();
                Log.d("err",error.toString());
            }
        });
        mqueue.add(mreq1);
    }

    public void updatePreference(){
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_id",sharedPreferences2.getString("user_id",""));
            jo.put("genre",genre);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(MyServerUrl+"userPreference", jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                       if(response!=null)
                            Log.d("preResp",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.d("errPrefrence",error.toString());
            }
        });

        mqueue.add(req);
    }

    public void implicitRatings(){
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_id",sharedPreferences2.getString("user_id",""));
            jo.put("b_id",b_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, MyServerUrl + "implicitRatings", jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response!=null){
                    Log.d("i_rate",response.toString());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("err",error.toString());
            }
        });
        mqueue.add(req);
    }

    public void implict_mf(){
        JSONObject jo = new JSONObject();
        Log.d("mf","called");
        try {
            jo.put("user_id",Integer.parseInt(sharedPreferences2.getString("user_id","0")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,MyServerUrl+"matrixFactorization", jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                    if(response!=null){
                        List<Integer> topN = new ArrayList<>();
                        try {
                            JSONArray ja = response.getJSONArray("topN");
                            JSONObject jo;
                            for(int i=0;i<ja.length();i++){
                                try {
                                    jo = ja.getJSONObject(i);
                                    listinfo current = new listinfo();
                                    current.title = jo.getString("title");
                                    if(current.title==title){
                                        continue;
                                    }
                                    current.url = jo.getString("imgUrl");
                                    StringBuilder sb = new StringBuilder(current.url);
                                    sb.replace(nthsearch(current.url,'/',2),nthsearch(current.url,':',2)-1,Localhost);
                                    current.url = sb.toString();
                                    current.seller = jo.getString("Username");
                                    current.originalprice = jo.getInt("originalprice");
                                    current.yourprice = jo.getInt("yourprice");
                                    CFBookData.add(current);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Radpater rt = new Radpater(Product_detail.this, CFBookData);
                            cf_recycleView.swapAdapter(rt,false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                cfPrgressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("err_mf",error.toString());
                cfPrgressBar.setVisibility(View.GONE);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(60*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mqueue.add(req);
    }

    class recent_items{

        String title,username;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof recent_items))
                return false;
            final recent_items rs = (recent_items)o;
            return title.equals(rs.title);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new String[]{title});
        }

        @Override
        public String toString() {
            String str = "{title:"+title+""+",username:"+username+"}";
            return str;
        }
    }

    public void fetchSimilarItems(){
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        try {
            jo.put("title",title);
            ja.put(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest mreq = new JsonArrayRequest(Request.Method.POST, Url, ja, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                    if(response.length()>0){
                        JSONObject jo;
                        for(int i=0;i<response.length();i++){
                            try {
                                jo = response.getJSONObject(i);
                                listinfo current = new listinfo();
                                current.title = jo.getString("title");
                                current.url = jo.getString("imgUrl");
                                StringBuilder sb = new StringBuilder(current.url);
                                sb.replace(nthsearch(current.url,'/',2),nthsearch(current.url,':',2)-1,Localhost);
                                current.url = sb.toString();
                                current.seller = jo.getString("Username");
                                current.originalprice = jo.getInt("originalprice");
                                current.yourprice = jo.getInt("yourprice");
                                SimilarBookdata.add(current);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Radpater rt = new Radpater(Product_detail.this, SimilarBookdata);
                        sim_recyclerView.swapAdapter(rt,false);
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        mqueue.add(mreq);

    }

    public static int nthsearch(String str, char ch, int n){
        int pos=0;
        if(n!=0){
            for(int i=1; i<=n;i++){
                pos = str.indexOf(ch, pos)+1;
            }
            return pos;
        }
        else{
            return 0;
        }
    }

    public void updateRating(float rating){
        JSONObject jo = new JSONObject();

        try {
            jo.put("title",title);
            jo.put("username",sellername);
            jo.put("rating",(double)rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest mreq = new JsonObjectRequest(Request.Method.POST,MyServerUrl+"updateRating",jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                   if(response!=null){
                       try {
                           if(response.getBoolean("success")){
                               Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(mreq);
    }

    private void expand()
    {
        collapsablelayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        collapsablelayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, collapsablelayout.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse() {
        int finalHeight = collapsablelayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                collapsablelayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end)
    {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = collapsablelayout.getLayoutParams();
                layoutParams.height = value;
                collapsablelayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
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
