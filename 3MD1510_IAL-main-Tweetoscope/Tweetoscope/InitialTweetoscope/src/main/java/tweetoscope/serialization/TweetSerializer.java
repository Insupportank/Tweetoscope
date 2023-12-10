package tweetoscope.serialization;

import java.nio.charset.Charset;
import org.apache.kafka.common.serialization.Serializer;
import com.google.gson.Gson;
import com.twitter.clientlib.model.Tweet;

/**
 * Creates a serializer to send tweets to topics.
 * 
 * @author Julien MICHEL
 *
 */
public class TweetSerializer implements Serializer<Tweet> {
	@Override
	public byte[] serialize(String topic, Tweet data) {
		if (data == null)
			return null;
		Gson gson = new Gson();
		String jsonString = gson.toJson(data);
		return jsonString.getBytes(Charset.forName("ISO-8859-1"));
	}
}
