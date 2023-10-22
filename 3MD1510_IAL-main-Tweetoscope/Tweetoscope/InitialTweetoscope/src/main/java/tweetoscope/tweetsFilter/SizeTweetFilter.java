package tweetoscope.tweetsFilter;

import com.twitter.clientlib.model.Tweet;

public class SizeTweetFilter extends TweetFilter {

	/**
	 * target language to match (examples: "fr", "en"...)
	 */
	protected int n;

	/**
	 * Creates a filter that tests whether the "language" tag of a Tweet (if it is
	 * set) equals a given code.
	 * 
	 * @param language target language to match (example: "en")
	 */
	public SizeTweetFilter(int n) {
		this.n = n;
	}

	@Override
	protected boolean match(Tweet tweet) {
		return tweet.getText().length() >= n;
	}

}
