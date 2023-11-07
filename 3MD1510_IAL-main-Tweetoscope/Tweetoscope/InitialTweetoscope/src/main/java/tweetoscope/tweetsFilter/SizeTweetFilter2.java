package tweetoscope.tweetsFilter;

import com.twitter.clientlib.model.Tweet;

public class SizeTweetFilter2 extends TweetFilter2 {

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
	public static void main(String[] args) {
		new SizeTweetFilter2(args[0], args[1], args[2], Integer.parseInt(args[3]));
	}
	
	public SizeTweetFilter2(String bootstrapServers, String inputTopicName, String outputTopicName, int n) {
		super(bootstrapServers, inputTopicName, outputTopicName);
		this.n = n;
		this.run();
	}

	@Override
	protected boolean match(Tweet tweet) {
		return tweet.getText().length() >= n;
	}

}
