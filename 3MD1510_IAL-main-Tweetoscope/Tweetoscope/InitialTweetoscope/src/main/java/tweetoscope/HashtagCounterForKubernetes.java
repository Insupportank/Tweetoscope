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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.util.stream.Stream;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
/**
 * 
 * Reacts to the reception of a new hashtag by updating how many times it has
 * been seen so far, and sending to its subscribers the updated list of the most
 * popular ones.
 * <p>
 * Hashtags are received via Java Flow from the upstream component
 * {@link distributed_tweetoscope.HashtagExtractor}. Downstream component
 * ({@link distributed_tweetoscope.Visualiazor}) is notified of the new leader
 * board data via Java Flow.
 * 
 * @author Virginie Galtier
 *
 */
public class HashtagCounterForKubernetes {
	/**
	 * Number of lines to include on the leader board
	 */
	protected int nbLeaders;
	/**
	 * Key of the single row of the {@code dataset} that contains the occurrences of
	 * hashtags
	 */
	protected final static String ROW_KEY = "hashtag";

	/**
	 * Map <Hashtag text - number of occurrences>
	 */
	protected Map<String, Integer> hashtagOccurrenceMap;

	/**
	 * List of most popular hashtags, used to checked if the list is changed after a
	 * new hashtag is received
	 */
	protected Map<String, Integer> previousLeaderMap;

	/**
	 * 
	 * @param nbLeaders number of hashtags to include on the leader board
	 */
	public static void main(String[] arg) {
		new HashtagCounterForKubernetes(Integer.parseInt(arg[0]), arg[1], arg[2]);
	}
	
	public HashtagCounterForKubernetes(int nbLeader, String bootstrapServers, String inputTopicName) {
		this.nbLeaders = nbLeader;
		KafkaConsumer<Void, String> consumer = new KafkaConsumer<Void, String>(
				configureKafkaConsumer(bootstrapServers));
		consumer.subscribe(Collections.singletonList(inputTopicName));
		hashtagOccurrenceMap = new HashMap<String, Integer>();

		try {
			Duration timeout = Duration.ofMillis(1000);
			while (true) {
				// reads events
				ConsumerRecords<Void, String> hashtags = consumer.poll(timeout);
				for (ConsumerRecord<Void, String> hashtagObj : hashtags) {
					String hashtag = hashtagObj.value();
					String key = "#" + hashtag;
					if (hashtagOccurrenceMap.containsKey(key)) {
						hashtagOccurrenceMap.replace(key, 1 + hashtagOccurrenceMap.get(key));
					} else {
						hashtagOccurrenceMap.put(key, 1);
					}

					// sorts by number of occurrences and keeps only the top ones
					Map<String, Integer> topHashtagsMap = hashtagOccurrenceMap.entrySet().stream()
							.sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(nbLeaders)
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					
					if (previousLeaderMap == null || !previousLeaderMap.equals(topHashtagsMap)) {
						Stream<Entry<String, Integer>> sortedTopHashtags = topHashtagsMap.entrySet().stream()
								.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
						sortedTopHashtags.forEach(t -> {
							System.out.print(t.getKey().toString());
							System.out.print(": ");
							System.out.println(t.getValue());
						});
						previousLeaderMap = topHashtagsMap;
					}

					
			}
		}
		} catch (Exception e) {
			System.out.println("something went wrong... " + e.getMessage());
		} finally {
			consumer.close();
		}
	}
	
	private Properties configureKafkaConsumer(String bootstrapServers) {
		Properties consumerProperties = new Properties();

		consumerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.VoidDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.StringDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "the_extractors");
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // from beginning

		return consumerProperties;
	}
}