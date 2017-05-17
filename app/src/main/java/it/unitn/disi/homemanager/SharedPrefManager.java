package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 05/05/17.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;



public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_FACEBOOK_TOKEN = "tagfacebooktoken";
    private static final String TAG_FACEBOOK_ID = "tagfacebookid";
    private static final String TAG_FIREBASE_TOKEN = "tagfirebasetoken";
    private static final String TAG_FACEBOOK_NAME = "tagfacebookname";
    private static final String TAG_DEBIT_CREDIT ="tagdebitcredit" ;
    private static final String TAG_GROUP_ID ="taggroupid" ;

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public static void retrieveDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String token= FirebaseInstanceId.getInstance().getToken();
        editor.putString(TAG_FIREBASE_TOKEN, token);
        editor.apply();
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_FIREBASE_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_FIREBASE_TOKEN, null);
    }

    public boolean saveFacebookToken (String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_FACEBOOK_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getFacebookToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_FACEBOOK_TOKEN, null);
    }

    public boolean saveFacebookID (String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_FACEBOOK_ID, token);
        editor.apply();
        return true;
    }
    public String getFacebookID(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_FACEBOOK_ID, null);
    }

    public boolean saveFacebookName (String name){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_FACEBOOK_NAME, name);
        editor.apply();
        return true;
    }
    public String getFacebookName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_FACEBOOK_NAME, null);
    }
    public boolean saveDebitCredit (int debit_credit){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TAG_DEBIT_CREDIT, String.valueOf(debit_credit));
        editor.apply();
        return true;
    }
    public String getDebitCredit(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_DEBIT_CREDIT, null);
    }
    public boolean saveGroupId (int group_id){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TAG_GROUP_ID, String.valueOf(group_id));
        editor.apply();
        return true;
    }
    public String getGroupId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_GROUP_ID, null);
    }
}