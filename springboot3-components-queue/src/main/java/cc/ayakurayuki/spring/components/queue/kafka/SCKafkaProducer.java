package cc.ayakurayuki.spring.components.queue.kafka;

import jakarta.annotation.Nonnull;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @author Crop
 */
@Slf4j
public class SCKafkaProducer {

  private final SCKafkaProperties config;

  private final KafkaProducer<String, String> producer;

  public SCKafkaProducer(@Nonnull SCKafkaProperties properties) {
    Objects.requireNonNull(properties.getServers(), "kafka servers cannot be empty!");
    Objects.requireNonNull(properties.getTopic(), "kafka topic cannot be empty!");

    this.config = properties;

    Properties props = new Properties();
    // set bootstrap server endpoints
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServers());
    // set key/value serializer
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    // set the maximum wait time millis
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 600);
    // set the client internal retry times
    props.put(ProducerConfig.RETRIES_CONFIG, 5);
    // set the client internal reconnect interval millis
    props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);

    this.producer = new KafkaProducer<>(props);
  }

  public void produceSafe(String value) {
    try {
      this.produce(value);
    } catch (ExecutionException | InterruptedException e) {
      log.error("an exception occurred when producing message!! config: [%s], value: [%s]".formatted(this.config, value), e);
    }
  }

  public void produceSafe(String key, String value) {
    try {
      this.produce(key, value);
    } catch (ExecutionException | InterruptedException e) {
      log.error("an exception occurred when producing message!! config: [%s], key: [%s], value: [%s]".formatted(this.config, key, value), e);
    }
  }

  public void produce(String value) throws ExecutionException, InterruptedException {
    this.produce(null, value);
  }

  public void produce(String key, String value) throws ExecutionException, InterruptedException {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(config.getTopic(), key, value);
    Future<RecordMetadata> metadataFuture = producer.send(producerRecord);
    metadataFuture.get();
  }

  public Future<RecordMetadata> produceAsync(String value) {
    return this.produceAsync(null, value);
  }

  public Future<RecordMetadata> produceAsync(String key, String value) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(config.getTopic(), key, value);
    return producer.send(producerRecord);
  }

}
