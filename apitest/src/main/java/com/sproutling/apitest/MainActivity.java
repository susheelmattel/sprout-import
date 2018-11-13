package com.sproutling.apitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mCreateUserButton, mLoginButton, mCreateChildButton, mListChildrenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateUserButton = (Button) findViewById(R.id.create_user);
        mCreateUserButton.setOnClickListener(mOnClickListener);
        mLoginButton = (Button) findViewById(R.id.login);
        mLoginButton.setOnClickListener(mOnClickListener);
        mCreateChildButton = (Button) findViewById(R.id.create_child);
        mCreateChildButton.setOnClickListener(mOnClickListener);
        mListChildrenButton = (Button) findViewById(R.id.list_children);
        mListChildrenButton.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.create_user:
//                    createUser();
//                    break;
//                case R.id.login:
//                    login();
//                    break;
//                case R.id.create_child:
//                    createChild();
//                    break;
//                case R.id.list_children:
//                    listChildren();
//                    break;
            }
        }
    };

//    void createUser() {
//        new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                try {
//                    SSManagement.createUser("brady.lin@fuhu.com", "Brady", "Lin", "Guardian", "12345678");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
//    }
//
//    void login() {
//        new AsyncTask<Void, Void, SSManagement.UserAccount>() {
//            @Override
//            protected SSManagement.UserAccount doInBackground(Void... params) {
//                try {
//                    return SSManagement.loginByPassword("brady.lin@fuhu.com", "12345678");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(SSManagement.UserAccount userAccount) {
//                if (userAccount != null) {
//                    AccountManagement.getInstance(MainActivity.this).writeToPreferences(userAccount);
//                } else {
//                }
//            }
//        }.execute();
//    }
//
//    void createChild() {
//        new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                try {
//
//                    SSManagement.createChild(AccountManagement.getInstance(MainActivity.this).getAccount().accessToken, "Brady Jr.", "Lin", SSChild.GENDER_BOY, "2014-08-08T15:40:51-07:00", "2014-08-12T15:40:51-07:00");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
//    }
//
//    void listChildren() {
//        new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                try {
//                    SSManagement.listChildren(AccountManagement.getInstance(MainActivity.this).getAccount().accessToken);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
//    }
}
