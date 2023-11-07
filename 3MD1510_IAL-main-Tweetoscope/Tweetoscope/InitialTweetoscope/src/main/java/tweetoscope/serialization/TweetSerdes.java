package tweetoscope.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import com.twitter.clientlib.model.Tweet;

public class TweetSerdes implements Serde<Tweet>{
	private TweetSerializer serializer;
	private TweetDeserializer deserializer;
	public TweetSerdes() {
		this.serializer = new TweetSerializer();
		this.deserializer = new TweetDeserializer();
	}
	@Override
	public Serializer<Tweet> serializer(){
		return serializer;
	}
	
	@Override
	public Deserializer<Tweet> deserializer(){
		return deserializer;
	}
}
