package tweetoscope.tweetsFilter;

import static org.junit.Assert.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import com.twitter.clientlib.model.Tweet;

public class SizeTweetFilter2Test {

	@Test
	public void test() {
		Tweet[] tweets = new Tweet[2];
		
		tweets[0] = new Tweet();
		tweets[0].setId("001");
		tweets[0].setCreatedAt(OffsetDateTime.of(2022, 6, 20, 11, 46, 23, 0, ZoneOffset.UTC));
		tweets[0].setText("Petit tweet #court");
		tweets[0].setAuthorId("31");
		tweets[0].setConversationId("01");
		tweets[0].getGeo();
		tweets[0].setLang("fr");
		
		tweets[1] = new Tweet();
		tweets[1].setId("002");
		tweets[1].setCreatedAt(OffsetDateTime.of(2022, 6, 20, 11, 48, 20, 0, ZoneOffset.UTC));
		tweets[1].setText("Ceci est un tweet plus long #long");
		tweets[1].setAuthorId("31");
		tweets[1].setConversationId("01");
		tweets[1].getGeo();
		tweets[1].setLang("fr");

		SizeTweetFilter2 filter = new SizeTweetFilter2("localhost:9092,localhost:9093", "tweets", "filtered_tweet", 20);
		assertEquals(false, filter.match(tweets[0]));
		assertEquals(true, filter.match(tweets[1]));
	}

}
