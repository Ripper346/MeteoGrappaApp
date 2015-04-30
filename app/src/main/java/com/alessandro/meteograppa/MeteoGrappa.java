package com.alessandro.meteograppa;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Path;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeteoGrappa extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public final String URL = "http://ripper346.asuscomm.com:20000/MeteoGrappaServer/";
    private ViewGroup[] tabs;
    private Spinner[] graphSettings;
    private String[] typeValueGraph;
    private String graphUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graphUrl = URL + "graph.jsp";

        setContentView(R.layout.activity_meteo_grappa);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        setTabsNavigation();
        refresh();
    }

    /**
     * Initialize the screen of graphs.
     * Set the spinners array of variable and type and add a listener on them.
     * Set the array values corresponding to the type spinner.
     * Initialize the graph browser.
     */
    private void initGraphPage() {
        graphSettings = new Spinner[2];
        graphSettings[0] = (Spinner) findViewById(R.id.variable);
        graphSettings[1] = (Spinner) findViewById(R.id.type);
        addListenerOnSpinnerItemSelection();
        Resources res = getResources();
        typeValueGraph = res.getStringArray(R.array.type_value_array);
        graphBrowserInit();
    }

    /**
     * Initialize the browser for showing the graphs.
     * Disabled scrolling and navigate to the default page.
     */
    private void graphBrowserInit() {
        WebView browser = (WebView) findViewById(R.id.graphBrowser);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl(graphUrl);
    }

    /**
     * Sets the array for control the navigation links.
     */
    private void setTabsNavigation() {
        tabs = new ViewGroup[2];
        tabs[0] = (ViewGroup) findViewById(R.id.home);
        tabs[1] = (ViewGroup) findViewById(R.id.grafici);
        tabs[1].setVisibility(View.INVISIBLE);
        tabs[0].setVisibility(View.VISIBLE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
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
            getMenuInflater().inflate(R.menu.meteo_grappa, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_meteo_grappa, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MeteoGrappa) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * Load the content data
     */
    public void refresh() {
        new ManageData().execute(this);
        initGraphPage();
    }

    /**
     * Show the page selected from the link on the navigation tapped.
     *
     * @param position id of the navigation link tapped
     */
    public void watch(int position) {
        if (tabs != null)
            for (int i = 0; i < tabs.length; i++) {
                if (i == position)
                    tabs[i].setVisibility(View.VISIBLE);
                else
                    tabs[i].setVisibility(View.INVISIBLE);
            }
    }

    /**
     * Add a listener on the graph page spinners
     */
    public void addListenerOnSpinnerItemSelection() {
        if (graphSettings != null)
            for (int i = 0; i < graphSettings.length; i++)
                graphSettings[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    /**
                     * Navigate on the graph selected.
                     */
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        WebView graphBrowser = (WebView) findViewById(R.id.graphBrowser);
                        graphUrl = URL + "graph.jsp?type=" + String.valueOf(graphSettings[0].getSelectedItemId())
                                + "&each=" + typeValueGraph[(int) graphSettings[1].getSelectedItemId()];
                        graphBrowser.loadUrl(graphUrl);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // Nothing to do
                    }
                });
    }

    public void setGraphUrl(String graphUrl) {
        this.graphUrl = graphUrl;
    }
}
