package cc.ayakurayuki.spring.components.queue.kafka;

import java.io.Closeable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * @author Crop
 */
@Data
@Slf4j
public class SCKafkaConsumer implements Closeable {

  private SCKafkaProperties config;

  KafkaConsumer<String, String> consumer;

  public SCKafkaConsumer(SCKafkaProperties properties) {
    Objects.requireNonNull(properties.getServers(), "kafka servers cannot be empty!");
    Objects.requireNonNull(properties.getTopic(), "kafka topic cannot be empty!");
    Objects.requireNonNull(properties.getGroup(), "kafka group cannot be empty!");

    if (consumer != null) {
      log.warn("consumer cannot initial more than once!!");
    }

    this.config = properties;

    Properties props = new Properties();
    // set bootstrap server endpoints
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServers());
    // set the session timeout millis
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Duration.ofSeconds(30).toMillis());
    // set the maximum pool size
    // be careful not to change this value too largely, if too much data is polled and cannot be consumed before the next poll, a load balancing will be triggered, causing lag
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
    // set key/value serializer
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    // set group id for this consumer
    props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroup());

    this.consumer = new KafkaConsumer<>(props);
  }

  public void start() {
    List<String> subscribedTopics = new ArrayList<>();

    // setup subscribes topics, allows multiple topics
    subscribedTopics.add(config.getTopic().trim());

    consumer.subscribe(subscribedTopics);
  }

  public void shutdown() {
    try {
      consumer.close();
    } catch (Exception ignored) {
      // ignore any exception, even if multiple calls
    }
  }

  @Override
  public void close() {
    this.shutdown();
  }

  public void consume(Consumer<ConsumerRecord<String, String>> consume) {
    for (; ; ) {
      try {

        ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofSeconds(1));
        // must consume all records in each batch before the next round, and spend time less than `SESSION_TIMEOUT_MS_CONFIG`
        // it is recommended to create a new thread for consuming records, wait for the async processing result
        for (ConsumerRecord<String, String> record : records) {
          consume.accept(record);
        }

      } catch (Exception e) {

        log.warn("consume error", e);

      } finally {

        // wait for half-second before the next batch
        LockSupport.parkNanos(Duration.ofMillis(500).toNanos());

      }
    }
  }

  public void consumes(Consumer<ConsumerRecords<String, String>> consumes) {
    for (; ; ) {
      try {

        ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofSeconds(1));
        // must consume all records in each batch before the next round, and spend time less than `SESSION_TIMEOUT_MS_CONFIG`
        // it is recommended to create a new thread for consuming records, wait for the async processing result
        consumes.accept(records);

      } catch (Exception e) {

        log.warn("consume error", e);

      } finally {

        // wait for half-second before the next batch
        LockSupport.parkNanos(Duration.ofMillis(500).toNanos());

      }
    }
  }

}
