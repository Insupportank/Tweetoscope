echo "delete tweets topic if it exists"
$KAFKA_HOME/bin/kafka-topics.sh --delete --if-exists --bootstrap-server localhost:9092 --topic tweets
echo "create tweets topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 2 --config retention.ms=3600000 --topic tweets

echo "delete filtered_tweet topic if it exists"
$KAFKA_HOME/bin/kafka-topics.sh --delete --if-exists --bootstrap-server localhost:9092 --topic filtered_tweet
echo "create filtered_tweet topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 2 --config retention.ms=3600000 --topic filtered_tweet

echo "delete hashtags topic if it exists"
$KAFKA_HOME/bin/kafka-topics.sh --delete --if-exists --bootstrap-server localhost:9092 --topic hashtags
echo "create hashtags topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 2 --config retention.ms=3600000 --topic hashtags

