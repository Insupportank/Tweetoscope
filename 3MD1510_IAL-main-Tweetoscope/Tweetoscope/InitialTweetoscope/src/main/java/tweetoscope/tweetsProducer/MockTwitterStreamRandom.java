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

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import com.twitter.clientlib.model.Tweet;
import tweetoscope.serialization.TweetSerializer;
/**
 * Mimics the TwitterStreamSampleReaderSingleton class. To be used when the
 * Twitter sampled stream rate limit is exceeded for instance. Creates a
 * continuous stream of random Tweets.
 * 
 * @author Virginie Galtier
 *
 */
public final class MockTwitterStreamRandom extends OfflineTweetsProducer {
	/*
	 * Kafka producer
	 */
	private KafkaProducer<Void, Tweet> kafkaProducer;
	/*
	 * List of Kafka bootstrap servers. Example: localhost:9092,another.host:9092
	 */
	private String bootstrapServers;
	/*
	 * Name of the destination Kafka topic
	 */
	private String topicName;

	/**
	 * Creates the proxy (provoking infinite execution).
	 * 
	 * @param args first argument is a list of Kafka bootstrap servers, second
	 *             argument is the name of the destination Kafka topic
	 */

	/**
	 * Creates a new MockTwitterStreamRandom.
	 * 
	 */
	public MockTwitterStreamRandom(String bootstrapServers, String topicName) {
		// Looks like useless but we keep the super
		super();
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
	 * Posts random Tweets to the
	 * {@link distributed_tweetoscope.TweetoscopeAppConfig#TWEETS_TOPIC_NAME} Kafka
	 * topic. A random Tweet text is a concatenation of hashtags chosen from a
	 * reduced set. The Tweet is also assigned a language among a reduced list.
	 */
	@Override
	public void run() {
		String[] hashtags = { "fun", "bitCoin", "climate", "crypto", "CS", "Metz", "weather", "summer", "holidays",
				"health", "running", "sport" };
		String[] languages = { "fr", "en", "ru", "es", "it" };

		Tweet tweet;
		int nb = 0;
		String text;
		Random r = new Random();
		while (true) {
			// crafts a random Tweet
			nb++;
			tweet = new Tweet();
			text = "Tweet " + nb;
			for (int i = 0; i < (int) (4 * Math.random()); i++) {
				double d;
				do {
					d = r.nextGaussian();
					d = (int) (hashtags.length / 2 + d * hashtags.length / 2);
				} while (d < 0 || d > hashtags.length - 1);
				text += "#" + hashtags[(int) d] + " ";
			}
			tweet.setId("" + nb);
			tweet.setText(text);
			tweet.setLang(languages[(int) (Math.random() * languages.length)]);
			
			// publishes the Tweet
			kafkaProducer.send(new ProducerRecord<Void, Tweet>(topicName, null, tweet));
			
			// waits for a while
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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