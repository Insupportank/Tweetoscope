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
package tweetoscope.tweetsFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import com.twitter.clientlib.model.Tweet;

/**
 * Reacts to the reception of a new Tweet, if the Tweet matches the filter
 * condition, downstream subscribers are notified, otherwise the process is
 * silent. Tweets are received from
 * {@link distributed_tweetoscope.tweetsProducer.TweetsProducer} via Java Flow.
 * Filtered Tweets are passes down to the
 * {@link distributed_tweetoscope.HashtagCounter} via Java Flow.
 * 
 * @author Virginie Galtier
 *
 */
public abstract class TweetFilter2 {

	/*
	 * List of Kafka bootstrap servers. Example: localhost:9092,another.host:9092
	 * 
	 * @see:
	 * https://jaceklaskowski.gitbooks.io/apache-kafka/content/kafka-properties-
	 * bootstrap-servers.html
	 */
	private String bootstrapServers;
	/*
	 * Name of the source Kafka topic
	 */
	private String inputTopicName;
	/*
	 * Name of the destination Kafka topic
	 */
	private String outputTopicName;

	/**
	 * Creates a filter element (provoking infinite execution).
	 * 
	 * @param arg first argument is a list of Kafka bootstrap servers, second
	 *            argument is the name of the source Kafka topic, third argument is
	 *            the name of the destination Kafka topic
	 */
	protected abstract boolean match(String tweet);
	/**
	 * Creates a Kafka consumer and a Kafka producer, the consumer reads a wikimedia
	 * change event from a Kafka topic the locale of the event is extracted the
	 * producer publishes the region (until the filter element is interrupted).
	 * 
	 * @param bootstrapServers   list of Kafka bootstrap servers. Example:
	 *                           localhost:9092,another.host:9092
	 * @param inputTopicName name of the source Kafka topic
	 * @param outputTopicName    name of the destination Kafka topic
	 */
	TweetFilter2(String bootstrapServers, String inputTopicName, String outputTopicName) {
		this.bootstrapServers = bootstrapServers;
		this.inputTopicName = inputTopicName;
		this.outputTopicName = outputTopicName;

		Topology tweetsTopology = createTweetsTopology();
		KafkaStreams tweetStream = new KafkaStreams(tweetsTopology,
				configureTweetStream());
		tweetStream.start();
	}

	/**
	 * Prepares configuration for the Kafka stream
	 * 
	 * @return configuration properties for the Kafka stream
	 */
	private Properties configureTweetStream() {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "wikipediaRegion");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		// The semantics of caching is that data is flushed to the state store and
		// forwarded to the next downstream processor node
		// whenever the earliest of
		// commit.interval.ms or cache.max.bytes.buffering (cache pressure) hits.
		properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Void().getClass().getName());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return properties;
	}

	private Topology createTweetsTopology() {
		StreamsBuilder streamsBuilder = new StreamsBuilder();

		KStream<Void, String> wikimediaStream = streamsBuilder.stream(inputTopicName);

		// Filters out Wikipedia events
		// ----------------------------
		Predicate<Void, String> predicateWikipediaOnly = new Predicate<Void, String>() {
			public boolean test(Void key, String value) {
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(value, JsonObject.class);
				JsonObject jsonObjectMeta = jsonObject.getAsJsonObject("meta");
				String domain = jsonObjectMeta.getAsJsonPrimitive("domain").getAsString();
				return domain.contains("wikipedia");
			}
		};
		KStream wikipediaStream = wikimediaStream.filter(predicateWikipediaOnly);

		// Keeps only the locale part of the 'domain' field
		// -------------------------------------------------
		ValueTransformerSupplier<String, String> valueTransformerKeepOnlyLocale = new ValueTransformerSupplier<String, String>() {
			public ValueTransformer<String, String> get() {
				return new ValueTransformer<String, String>() {
					public void init(ProcessorContext context) {
						// TODO Auto-generated method stub
					}

					public String transform(String value) {
						Gson gson = new Gson();
						JsonObject jsonObject = gson.fromJson(value, JsonObject.class);
						JsonObject jsonObjectMeta = jsonObject.getAsJsonObject("meta");
						String domain = jsonObjectMeta.getAsJsonPrimitive("domain").getAsString();
						// domain = en.wikipedia.org
						return domain.split("\\.")[0]; // '.' is a special character in Java regex, must be escaped
					}

					public void close() {
						// TODO Auto-generated method stub
					}
				};
			}
		};
		KStream<Void, String> regionStream = wikipediaStream.transformValues(valueTransformerKeepOnlyLocale);

		// Outputs to the appropriate Kafka topic
		// --------------------------------------
		regionStream.to(outputTopicName);

		return streamsBuilder.build();
	}
}
