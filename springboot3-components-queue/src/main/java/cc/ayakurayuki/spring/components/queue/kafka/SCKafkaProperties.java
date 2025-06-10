package cc.ayakurayuki.spring.components.queue.kafka;

import lombok.Data;

/**
 * @author Crop
 */
@Data
public class SCKafkaProperties {

  private String  topic;
  private String  group;
  private String  servers;
  private boolean skipFails = true;

}
