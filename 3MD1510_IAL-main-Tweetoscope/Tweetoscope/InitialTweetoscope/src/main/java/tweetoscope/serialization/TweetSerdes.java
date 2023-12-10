package tweetoscope.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import com.twitter.clientlib.model.Tweet;

/**
 * Creates a serializer-deserializer for the Tweet object based on 
 * the serializer and the deserializer created for this object
 * 
 * @author Julien MICHEL
 *
 */
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
