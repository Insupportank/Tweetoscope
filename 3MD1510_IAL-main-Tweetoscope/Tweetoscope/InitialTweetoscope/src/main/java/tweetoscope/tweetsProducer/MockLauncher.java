package tweetoscope.tweetsProducer;

/**
 * Creates a tweet producer based on the arguments input in the command line.
 * 
 * @author Julien MICHEL
 *
 */
public class MockLauncher {
	public static void main(String[] args) {
		if (args[2].equals("random")){
			new MockTwitterStreamRandom(args[0], args[1]);
		}
		if (args[2].equals("recorded")) {
			new MockTwitterStreamRecorded(args[0], args[1], args[3]);
		}
		if (args[2].equals("scenario")) {
			new MockTwitterStreamScenario(args[0], args[1]);
		}
	}
}
