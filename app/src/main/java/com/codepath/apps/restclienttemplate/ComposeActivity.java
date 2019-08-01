package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGH = 140;


    private EditText etCompose;
    private Button btnCompose;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_compose );

        client = TwitterApp.getRestClient( this );
        etCompose = findViewById( R.id.etCompose );
        btnCompose = findViewById( R.id.btnCompose );

        // Set click listener on button

        btnCompose.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    // TODO: error-handling
                    Toast.makeText( ComposeActivity.this, "Tweet is empty", Toast.LENGTH_SHORT ).show();
                }
                if(tweetContent.length() > MAX_TWEET_LENGH){
                    Toast.makeText( ComposeActivity.this, "Tweet is to long!", Toast.LENGTH_LONG ).show();
                }
                Toast.makeText( ComposeActivity.this, tweetContent, Toast.LENGTH_LONG ).show();
                // get API request

                client.composeTweet( tweetContent, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("TwitterClient", "Successfully post Tweet" + response.toString());
                        try {
                            Tweet tweet = Tweet.fromJson( response );
                            Intent data = new Intent( );
                            data.putExtra( "tweet", Parcels.wrap(tweet) );
                            setResult( RESULT_OK, data);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e( "TwitterClient", "Failed to post Tweet" + responseString);
                    }
                } );
            }
        } );
    }
}
