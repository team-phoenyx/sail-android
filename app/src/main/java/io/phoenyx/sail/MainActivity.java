package io.phoenyx.sail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import io.phoenyx.sail.fragments.AchievementsFragment;
import io.phoenyx.sail.fragments.GoalsFragment;
import io.phoenyx.sail.fragments.PromisesFragment;
import io.phoenyx.sail.fragments.TimelineFragment;
import io.phoenyx.sail.models.Quote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    DrawerLayout drawer;
    View navHeader;
    ActionBarDrawerToggle toggle;
    String quote;
    String[] activityTitles = new String[]{"Goals", "Achievements", "Promises", "Timeline"};
    TextView quoteTextView;
    Animation fadeoutAnimation, fadeinAnimation;

    SailService sailService;
    DBHandler dbHandler;
    Handler handler;

    public static int navItemIndex = 0;
    private static final String TAG_GOALS = "goals";
    private static final String TAG_ACHIEVEMENTS = "achievements";
    private static final String TAG_PROMISES = "promises";
    private static final String TAG_TIMELINE = "timeline";

    public static String CURRENT_TAG = TAG_GOALS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navHeader = navigationView.getHeaderView(0);
        fadeinAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        fadeoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);

        fadeoutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                quoteTextView.setVisibility(View.INVISIBLE);
                refreshQuotes();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeinAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                quoteTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quoteTextView.startAnimation(fadeoutAnimation);

            }
        });

        handler = new Handler();
        navigationView.setNavigationItemSelectedListener(this);
        quoteTextView = (TextView) navHeader.findViewById(R.id.quoteTextView);

        dbHandler = new DBHandler(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        sailService = RetrofitClient.getClient("http://api.forismatic.com/").create(SailService.class);
        refreshQuotes();

        navigate(navItemIndex);
    }

    private void refreshQuotes() {
        sailService.getQOD().enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    quote = response.body().getQuoteText() + "\n" + "    -" + response.body().getQuoteAuthor();
                } else {
                    quote = "Sorry, quotes are currently unavailable";
                }
                quoteTextView.setText(quote);
                quoteTextView.startAnimation(fadeinAnimation);

            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                quote = "Sorry, quotes are currently unavailable";
                quoteTextView.setText(quote);
                quoteTextView.startAnimation(fadeinAnimation);
            }
        });
    }

    private void navigate(int navItemIndex) {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);

        Fragment fragment = null;
        Class fragmentClass;
        switch (navItemIndex) {
            case 0:
                fragmentClass = GoalsFragment.class;
                break;
            case 1:
                fragmentClass = AchievementsFragment.class;
                break;
            case 2:
                fragmentClass = PromisesFragment.class;
                break;
            case 3:
                fragmentClass = TimelineFragment.class;
                break;
            default:
                fragmentClass = GoalsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment).commitAllowingStateLoss();
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);

        int id = item.getItemId();

        if (id == R.id.nav_goals) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_GOALS;
        } else if (id == R.id.nav_achievements) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_ACHIEVEMENTS;
        } else if (id == R.id.nav_promises) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_PROMISES;
        } else if (id == R.id.nav_timeline) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_TIMELINE;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(navItemIndex);
            }
        }, 100);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
