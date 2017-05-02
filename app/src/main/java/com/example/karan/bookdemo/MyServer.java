package com.example.karan.bookdemo;

/**
 * Created by KARAN on 30-10-2016.
 */
public interface MyServer  {

    String genimotion = "10.0.3.2";
    String router = "172.20.10.5";//"10.0.0.2";
    String emulator = "10.0.2.2";

    String Localhost = genimotion;
    String MyServerUrl = "http://"+Localhost+":3000/api/"; //"http://kmodi4.net76.net/BookDemo/";


}
