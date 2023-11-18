# Tweetoscope23_GROUP-02_BRODERICK_HAZARD_MICHEL

## Description
This project aims to read and use tweets stream.

## Add your files

- [ ] Use ssh key `cat ~/.ssh/id_rsa.pub` and paste it in the gitlab student interface.
- [ ] Clone repository `git clone git@gitlab-student.centralesupelec.fr:julien.michel/tweetoscope23_group-02_broderick_hazard_michel.git`

- [ ] Use cool git commands
```
git status
git pull
git commit -m "message"
git push (-u origin {branch_name})
git branch (-M {branch_name})
git checkout {branch_name}
git merge {branch_name}
git stash
git stash pop
```

## Run project in eclipse

- [ ] Open project `3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope` in eclipse
- [ ] Open run configuration and add in the arguments tab: `-s {random|scenario} -f {none|language|size} -n {int}`
- [ ] Open a terminal `bash run_kafka.bash` to launch Zookeeper and the brokers. It also creates the topics we need : 'tweets', 'filteredTweets' and 'Hashtags'.
- [ ] Right click on `src/main/java/tweetoscope/TweetoscopeApp.java` and run as java application

## Build and run
- [ ] Build the pom.xml `mvn -f 3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope/pom.xml clean package`
- [ ] Run the producer `java -jar 3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope/target/TweetProducer-jar-with-dependencies.jar localhost:9092,localhost:9093 tweets name option` with name equals
to random, recorded or scenario and for recorded, option equals to `3MD1510_IAL-main-Tweetoscope/Tweetoscope/TestBases/miniTestBase.txt`, `3MD1510_IAL-main-Tweetoscope/Tweetoscope/TestBases/scenarioTestBase.txt` or `3MD1510_IAL-main-Tweetoscope/Tweetoscope/TestBases/largeTestBase.txt`
- [ ] Run the filter `java -jar 3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope/target/TweetFilter-jar-with-dependencies.jar localhost:9092,localhost:9093 tweets filtered_tweet name option` with name equals
to empty, size or lang option equals to an int defining the min size for sizeFilter or a string defining the language to filter for the langFilter
- [ ] Run the hashtag extractor `java -jar 3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope/target/HashtagExtractor-jar-with-dependencies.jar localhost:9092,localhost:9093 filtered_tweet hashtags`
- [ ] Run the hashtag counter and visualizor `java -jar 3MD1510_IAL-main-Tweetoscope/Tweetoscope/InitialTweetoscope/target/HashtagExtractor-jar-with-dependencies.jar nbLeaders localhost:9092,localhost:9093 hashtags` with nbLeaders an int equals to the number of hashtags visible on the leaderboard
