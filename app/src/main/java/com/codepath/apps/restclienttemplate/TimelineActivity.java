package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {


   private final int REQUEST_CODE = 20;
    private TwitterClient client;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private FloatingActionButton fActionButton;

    private SwipeRefreshLayout swipeContainer;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timeline );

        android.support.v7.widget.Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        getSupportActionBar().setTitle( "" );

        client = TwitterApp.getRestClient( this );

        swipeContainer = findViewById( R.id.swipeContainer );
        fActionButton = findViewById( R.id.fActionButton );

        fActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( TimelineActivity.this, ComposeActivity.class );
                startActivity( i );
            }
        } );



         //Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



    // Find the recycleView
        rvTweets = findViewById( R.id.rvTweets );
        // initialize a list of tweet and adapter from the data source
        tweets = new ArrayList<>( );
        adapter = new TweetAdapter( this, tweets );
        //Recycle View setup: layout manager and setting the adapter
        rvTweets.setLayoutManager( new LinearLayoutManager(this ) );
        rvTweets.setAdapter( adapter );
        populatHomeTimeline();

        swipeContainer.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d( "TwitterClient", "Content is being refresh" );
                populatHomeTimeline();
            }
        } );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.compose) {

            Intent i = new Intent( this, ComposeActivity.class );

            startActivityForResult( i, REQUEST_CODE );

            return true;
        }
        return super.onOptionsItemSelected( item );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
        Tweet tweet = Parcels.unwrap(data.getParcelableExtra( "tweet" ) );

        tweets.add( 0, tweet );
        adapter.notifyItemInserted( 0 );
        }
    }




    private void populatHomeTimeline() {
        client.getHomeTimeline( new JsonHttpResponseHandler( ){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Log.d("TwitterClient", response.toString());
                List<Tweet> tweetToAdd = new ArrayList<>();
                // Iterate through the list of tweet
                for (int i = 0; i < response.length(); i++){
                    try {
                        // Convert each Json Object into a tweet object
                        JSONObject jsonTweetObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson( jsonTweetObject );
                        //Add the tweet into our date source
                        tweetToAdd.add( tweet );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Clear the existing list
                adapter.clear();

                // Show the data we just received
                adapter.addTweet( tweetToAdd );

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TwitterClient", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("TwitterClient", errorResponse.toString());
            }
        } );
    }
}
