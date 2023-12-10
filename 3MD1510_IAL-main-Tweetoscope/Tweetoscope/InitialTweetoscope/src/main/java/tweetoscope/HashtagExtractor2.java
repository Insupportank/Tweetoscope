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
package tweetoscope;

import java.util.List;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.twitter.clientlib.model.Tweet;
import com.twitter.twittertext.Extractor;
import tweetoscope.serialization.TweetDeserializer;

/**
 * Reacts to the reception of a new Tweet in the topic filtered_tweet 
 * by parsing its text to look for hashtags. For each hashtag found, 
 * if any, send them to the topic hashtags. Tweets
 * are received from the filtered tweet topics and
 * hashtags are send to the hashtags topics via Kstream
 * 
 * @author Julien MICHEL
 *
 */
public class HashtagExtractor2 {

	/**
	 * Creates a hashtagExtractor element (provoking infinite execution).
	 * 
	 * @param arg first argument is a list of Kafka bootstrap servers, second
	 *            argument is the name of the source Kafka topic, third argument is
	 *            the name of the destination Kafka topic
	 */
	public static void main(String[] arg) {
		new HashtagExtractor2(arg[0], arg[1], arg[2]);
	}


	/**
	 * Twitter lib utility object
	 */
	final Extractor twitterTextExtractor = new Extractor();

	HashtagExtractor2(String bootstrapServers, String inputTopicName, String outputTopicName) {
		
		KafkaConsumer<Void, Tweet> consumer = new KafkaConsumer<Void, Tweet>(
				configureKafkaConsumer(bootstrapServers));
		// reads from any partition of the topic
		consumer.subscribe(Collections.singletonList(inputTopicName));

		KafkaProducer<Void, String> producer = new KafkaProducer<Void, String>(
				configureKafkaProducer(bootstrapServers));
		
		try {
			Duration timeout = Duration.ofMillis(1000);
			while (true) {
				// reads events
				ConsumerRecords<Void, Tweet> tweets = consumer.poll(timeout);
				for (ConsumerRecord<Void, Tweet> tweet : tweets) {
					// extracts the hashtags in the Tweet
					List<String> hashtags = twitterTextExtractor.extractHashtags(tweet.value().getText());

					// passes down the hashtags to the topic
					for (String hashtag : hashtags) {
						producer.send(new ProducerRecord<Void, String>(outputTopicName, null, hashtag));
					
				}
			}
		}
		} catch (Exception e) {
			System.out.println("something went wrong... " + e.getMessage());
		} finally {
			consumer.close();
			producer.close();
		}

	}
	
	/**
	 * Prepares configuration for the Kafka producer <Void, String>
	 * 
	 * @return configuration properties for the Kafka producer
	 */
	private Properties configureKafkaProducer(String bootstrapServers) {
		Properties producerProperties = new Properties();

		producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.VoidSerializer.class.getName());
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.StringSerializer.class.getName());

		return producerProperties;
	}

	/**
	 * Prepares configuration for the Kafka consumer <Void, String> All filters
	 * share the "the_filters" consumer group.
	 * 
	 * @return configuration properties for the Kafka consumer
	 */
	private Properties configureKafkaConsumer(String bootstrapServers) {
		Properties consumerProperties = new Properties();

		consumerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.VoidDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				TweetDeserializer.class);
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "the_extractors");
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // from beginning

		return consumerProperties;
	}


}