package tweetoscope.tweetsFilter;

public class FilterLauncher {
	public static void main(String[] args) {
		if (args[3].equals("empty")){
			new EmptyTweetFilter2(args[0], args[1], args[2]);
		}
		if (args[3].equals("size")) {
			new SizeTweetFilter2(args[0], args[1], args[2], Integer.parseInt(args[4]));
		}
		if (args[3].equals("lang")) {
			new LangTweetFilter2(args[0], args[1], args[2], args[4]);
		}
	}

}
