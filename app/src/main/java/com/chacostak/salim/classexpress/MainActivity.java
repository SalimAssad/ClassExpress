package com.chacostak.salim.classexpress;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.chacostak.salim.classexpress.Configuration.Settings_activity;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    boolean isHomeAddedToBackStack = false;

    DB_Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            helper = new DB_Helper(this, DB_Helper.DB_Name, 1);
            helper.getWritableDatabase();
        }catch(Exception e){

        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch(position) {
            case 0:
                transaction.replace(R.id.container, new Fragment_home()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = false;
                onSectionAttached(0);
                break;
            case 1:
                transaction.replace(R.id.container, new Fragment_courses()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(1);
                break;
            case 2:
                transaction.replace(R.id.container, new Fragment_homeworks()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(2);
                break;
            case 3:
                transaction.replace(R.id.container, new Fragment_exams()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(3);
                break;
            case 4:
                transaction.replace(R.id.container, new Fragment_teachers()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(4);
                break;
            case 5:
                transaction.replace(R.id.container, new Fragment_vacations()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(5);
                break;
            case 6:
                transaction.replace(R.id.container, new Fragment_calendar()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(6);
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;
            case 5:
                mTitle = getString(R.string.title_section6);
                break;
            case 6:
                mTitle = getString(R.string.title_section7);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings_activity.class);
                startActivity(intent);

                return true;
            case R.id.action_calendar:
                getFragmentManager().beginTransaction().replace(R.id.container, new Fragment_calendar()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                isHomeAddedToBackStack = true;
                onSectionAttached(6);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(isHomeAddedToBackStack) {
            isHomeAddedToBackStack = false;
            mNavigationDrawerFragment.selectItem(0);
        }else
            super.onBackPressed();
    }
}
