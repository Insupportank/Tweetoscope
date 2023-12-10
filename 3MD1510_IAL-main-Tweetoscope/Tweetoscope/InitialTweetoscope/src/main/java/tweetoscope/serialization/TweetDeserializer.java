package tweetoscope.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import com.twitter.clientlib.model.Tweet;
import com.google.gson.Gson;
/**
 * Creates a deserializer to listen to tweets in topics.
 * 
 * @author Julien MICHEL
 *
 */
public class TweetDeserializer implements Deserializer<Tweet> {

	@Override
	public Tweet deserialize(String topic, byte[] data) {
		if (data == null)
			return null;
		String jsonString = new String(data);
		Gson gson = new Gson();
		return gson.fromJson(jsonString, Tweet.class);
	}
}
