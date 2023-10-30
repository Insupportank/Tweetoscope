#!/bin/bash

# this script will setup the zookeeper, broker and each topic for each micro services.
# for aaron: export KAFKA_HOME=~/Documents/CS/kafka_2.13-3.5.1

WORKING_DIR=$(pwd)
xfce4-terminal --default-working-directory=$WORKING_DIR \
# Start the zookeeper server
--tab --title=Zookeeper --hold --command="/bin/bash -c '$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties'" \

# Start the 2 brokers after 3s
--tab --title=KafkaBroker_0 --hold --command="/bin/bash -c 'sleep 3s ; $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server_0.properties ; $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server_1.properties ; /bin/bash'" \
--tab --title=KafkaBroker_1 --hold --command="/bin/bash -c 'sleep 3s ; $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server_0.properties ; $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server_1.properties ; /bin/bash'" \

# Create the topics
--tab --title=Topics --hold --command="/bin/bash -c 'sleep 5s ; $KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092,localhost:9093 --replication-factor 1 --partitions 1 --topic tweets ; $KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092,localhost:9093 --replication-factor 1 --partitions 1 --topic filtered_tweet ;  $KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server localhost:9092,localhost:9093 --replication-factor 1 --partitions 1 --topic hashtags ; /bin/bash'" \

# Create Producers/Consumers
--tab --title=WikimediaConsumer --hold --command="/bin/bash -c 'sleep 7s ; $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic wikimedia --from-beginning ; /bin/bash'" \
--tab --title=RegionConsumer --hold --command="/bin/bash -c 'sleep 7s ; $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic region --from-beginning ; /bin/bash'" \