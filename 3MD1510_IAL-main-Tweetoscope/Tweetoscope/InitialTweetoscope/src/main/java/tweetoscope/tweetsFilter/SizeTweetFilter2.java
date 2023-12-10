package tweetoscope.tweetsFilter;

import com.twitter.clientlib.model.Tweet;

/**
 * Size Tweet filter : the Tweet must have a minimal length
 * 
 * @author Julien MICHEL
 *
 */

public class SizeTweetFilter2 extends TweetFilter2 {

	/**
	 * Number of minimal characters in the tweet text
	 */
	protected int n;

	/**
	 * Creates a filter that tests whether the tweet has a minimal length
	 * 
	 * @param number of minimal characters in the tweet text
	 */
	
	public SizeTweetFilter2(String bootstrapServers, String inputTopicName, String outputTopicName, int n) {
		super(bootstrapServers, inputTopicName, outputTopicName);
		this.n = n;
		this.run();
	}

	@Override
	public boolean match(Tweet tweet) {
		return tweet.getText().length() >= n+100;
	}

}
