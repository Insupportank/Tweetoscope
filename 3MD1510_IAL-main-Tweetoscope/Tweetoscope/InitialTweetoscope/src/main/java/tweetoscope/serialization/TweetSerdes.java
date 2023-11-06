package tweetoscope.serialization;
import tweetoscope.serialization.TweetSerializer;
import tweetoscope.serialization.TweetDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import com.twitter.clientlib.model.Tweet;
public class TweetSerdes {
	private TweetSerdes() {}
	
	public static Serde<Tweet> Tweet(){
		TweetSerializer serializer = new TweetSerializer();
		TweetDeserializer deserializer = new TweetDeserializer();
		return Serdes.serdeFrom(serializer, deserializer);
		
	}
}
