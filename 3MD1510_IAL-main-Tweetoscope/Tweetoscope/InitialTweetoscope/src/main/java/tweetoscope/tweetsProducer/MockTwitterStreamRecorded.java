/*
Copyright 2022 Virginie Galtier

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>
 */
package tweetoscope.tweetsProducer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.clientlib.model.Tweet;
import tweetoscope.serialization.TweetSerializer;

/**
 * Mimics the TwitterStreamSampleReaderSingleton class. To be used when the
 * Twitter sampled stream rate limit is exceeded for instance. Replays Tweets
 * recorded previously from the Twitter API.
 * 
 * @author Olivier HAZARD
 *
 */
public final class MockTwitterStreamRecorded extends OfflineTweetsProducer {
	/**
	 * File that holds the recorded Tweets
	 */
	protected String fileName;
	/**
	 * Kafka producer to send the tweets recorded from a test base to the topic tweets
	 **/
	private KafkaProducer<Void, Tweet> kafkaProducer;
	/**
	 * List of Kafka bootstrap servers. Example: localhost:9092,another.host:9092
	 **/
	private String bootstrapServers;
	/**
	 * Name of the destination Kafka topic
	 **/
	private String topicName;
	/**
	 * Creates a MockTwitterStreamRecorded.
	 */
	public MockTwitterStreamRecorded(String fileName, String bootstrapServers, String topicName) {
		// Looks like useless but we keep the super
		super();
		
		this.fileName = fileName;
		this.bootstrapServers = bootstrapServers;
		this.topicName = topicName;
		
		try {
			// creates the Kafka producer with the appropriate configuration
			kafkaProducer = new KafkaProducer<Void, Tweet>(configureKafkaProducer());
			run();
		} catch (Exception e) {
			System.err.println("something went wrong... " + e.getMessage());
		} finally {
			kafkaProducer.close();
		}
	}

	/**
	 * Reads Tweets from a file and forwards them to the Java Flow subscribers.
	 */
	@Override
	public void run() {
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(fileName)));
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (json, type,
							context) -> OffsetDateTime.parse(json.getAsString()))
					.create();
			JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
			JsonArray prerecordedtweets = jsonObject.getAsJsonArray("tweets");
			for (JsonElement je : prerecordedtweets) {
				Tweet tweet = gson.fromJson(je, Tweet.class);
				kafkaProducer.send(new ProducerRecord<Void, Tweet>(topicName, null, tweet));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Prepares configuration for the Kafka producer <Void, String>
	 * 
	 * @return configuration properties for the Kafka producer
	 */
	private Properties configureKafkaProducer() {
		Properties producerProperties = new Properties();
		producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.VoidSerializer");
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				TweetSerializer.class);
		return producerProperties;
	}
}