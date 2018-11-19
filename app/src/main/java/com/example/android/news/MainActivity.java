package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int NEWS_LOADER_ID = 1;
    ListView newsListView;
    boolean isConnected;
    private String mUrlRequestGuardianApi = "";
    private TextView mEmptyStateTextView;
    private View circleProgressBar;
    private NewsAdapter mAdapter;
    private SearchView mSearchViewField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkConnection(cm);

        newsListView = (ListView) findViewById(R.id.list);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        circleProgressBar = findViewById(R.id.loading_spinner);

        Button mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchViewField = (SearchView) findViewById(R.id.search_view_field);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint(getString(R.string.hint_search));

        if (isConnected) {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            circleProgressBar.setVisibility(GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        mSearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                checkConnection(cm);
                if (isConnected) {
                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();
                } else {
                    mAdapter.clear();
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews != null ? currentNews.getNewsUrl() : null);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

    }

    private String updateQueryUrl(String searchValue) {

        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("https://content.guardianapis.com/search?q=").append(searchValue).append("&order-date=published&show-section=true&show-fields=headline,thumbnail&show-references=author&show-tags=contributor&page=10&page-size=20&api-key=test");
        mUrlRequestGuardianApi = sb.toString();
        return mUrlRequestGuardianApi;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        if (mSearchViewField.getQuery().length() > 0) {
            updateQueryUrl(mSearchViewField.getQuery().toString());
        } else {
            Toast.makeText(this, "Fetching latest news", Toast.LENGTH_SHORT).show();
            mUrlRequestGuardianApi = "http://content.guardianapis.com/search?q=android&order-by=newest&order-date=published&show-section=true&show-fields=headline,thumbnail&show-references=author&show-tags=contributor&page=1&page-size=20&api-key=test";
        }
        return new NewsLoader(this, mUrlRequestGuardianApi);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newses) {
        View circleProgressBar = findViewById(R.id.loading_spinner);
        circleProgressBar.setVisibility(GONE);
        mEmptyStateTextView.setText(R.string.no_news);
        mAdapter.clear();
        if (newses != null && !newses.isEmpty()) {
            mAdapter.addAll(newses);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    public void restartLoader() {
        mEmptyStateTextView.setVisibility(GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
    }

    public void checkConnection(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;
        } else {
            isConnected = false;
        }
    }
}