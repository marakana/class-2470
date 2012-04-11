package com.marakana.android.rss;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.marakana.java.android.parser.FeedParser;
import com.marakana.java.android.parser.FeedParserFactory;
import com.marakana.java.android.parser.ParserType;
import com.marakana.java.android.parser.Post;

public class RssDemoActivity extends Activity {
	private static final String feedUrl = "http://marakana.com/s/feed.rss";
	private static Toast noDataToast;
	private ListView list;
	private ArrayAdapter<Post> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Workaround for Intel proxy for emulator
		// System.setProperty("http.proxyHost", "proxy.rr.intel.com");
		// System.setProperty("http.proxyPort", "911");

		// Setup UI
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		list = (ListView) findViewById(R.id.list);
		noDataToast = Toast.makeText(this, "No data at this point", Toast.LENGTH_LONG);
		
		// Setup the adapter
		adapter = new ArrayAdapter<Post>(this, android.R.layout.simple_list_item_1);
		adapter.setNotifyOnChange(true);
		list.setAdapter(adapter);

		// Parse the feed
		new FeedParserTask().execute(feedUrl);
	}

	/**
	 * AsycTask that processes parsing of the feed on separate worker thread.
	 * Input is the feed URL string, progress is ignored and output is number of
	 * new feeds.
	 */
	class FeedParserTask extends AsyncTask<String, Void, List<Post>> {

		/** Happens on the UI thread before the background task starts. */
		@Override
		protected void onPreExecute() {
			// Start the progress bar
			setProgressBarIndeterminateVisibility(true);
		}

		/** Background work to be done on a separate thread. */
		@Override
		protected List<Post> doInBackground(String... params) {
			// Get the feed from the parser factory

			try {
				FeedParser feed = FeedParserFactory.getParser(params[0],
						ParserType.SAX);
				return feed.parse();
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * Work that happens once we are done with the background task. It
		 * executes on the main/UI thread.
		 */
		@Override
		protected void onPostExecute(List<Post> posts) {
			// Stop the progress bar
			setProgressBarIndeterminateVisibility(false);

			if (posts == null) {
				noDataToast.show();
				return;
			}
			
			// Update the adapter
			adapter.clear();
			adapter.addAll(posts);
		}

	}
}
