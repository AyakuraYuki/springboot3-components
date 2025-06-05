package cc.ayakurayuki.spring.components.utility.snowflake;

/**
 * @author Ayakura Yuki
 */
public class SnowflakeFactory implements Snowflake {

  // start timestamp millis, once set, do NOT change
  private final long twepoch;

  // struct: 1 bit of placeholder, plus 41 bits of timestamp, plus 10 bits of worker machine ID, and plus 12 bits of the sequence

  // 12 bits of the sequence
  private final long sequenceBits       = 12L;
  // 10 bits of machine ID, seperated by 3 bits of datacenter ID and 7 bits of worker ID
  private final long machineBits        = 10L;
  private final long workerIdBits       = 7L;
  private final long datacenterIdBits   = machineBits - workerIdBits;
  // max id
  private final long maxWorkerId        = ~(-1L << workerIdBits);  // -1L ^ (-1L << workerIdBits)
  private final long maxDatacenterId    = ~(-1L << datacenterIdBits);  // -1L ^ (-1L << datacenterIdBits)
  // shift
  private final long workerIdShift      = sequenceBits;
  private final long datacenterIdShift  = sequenceBits + workerIdBits;
  private final long timestampLeftShift = sequenceBits + machineBits;
  private final long sequenceMask       = ~(-1L << sequenceBits);  // -1L ^ (-1L << sequenceBits)

  // machine variables
  private final long workerId;
  private final long datacenterId;
  private       long lastTimestamp = -1L;
  private       long sequence;

  public SnowflakeFactory(long twepoch, long workerId, long datacenterId, long sequence) {
    // sanity check of worker id
    if (workerId > maxWorkerId || workerId < 0) {
      throw new IllegalArgumentException("worker id cannot be greater than %d or less than 0".formatted(maxWorkerId));
    }
    if (datacenterId > maxDatacenterId || datacenterId < 0) {
      throw new IllegalArgumentException("datacenter id cannot be greater than %d or less than 0".formatted(maxDatacenterId));
    }

    long now = System.currentTimeMillis();
    if (twepoch < 0 || twepoch > now) {
      throw new IllegalArgumentException("twepoch must be between 0 and %d".formatted(now));
    }

    this.twepoch = twepoch;
    this.workerId = workerId;
    this.datacenterId = datacenterId;
    this.sequence = sequence;
  }

  public long getWorkerIdBits() {
    return workerIdBits;
  }

  public long getDatacenterIdBits() {
    return datacenterIdBits;
  }

  public long getSequence() {
    return sequence;
  }

  @Override
  public long next() {
    long timestamp = timeGen();

    if (timestamp < lastTimestamp) {
      throw new RuntimeException("Clock moved backwards. Refusing to generate id for %d milliseconds".formatted(lastTimestamp - timestamp));
    }

    if (lastTimestamp == timestamp) {
      // control the range in [0, 4095] by using mask
      sequence = (sequence + 1) & sequenceMask;
      if (sequence == 0) {
        timestamp = tilNextMillis(lastTimestamp);
      }
    } else {
      sequence = 0;
    }

    lastTimestamp = timestamp;

    return ((timestamp - twepoch) << timestampLeftShift) |
        (datacenterId << datacenterIdShift) |
        (workerId << workerIdShift) |
        sequence;
  }

  private long tilNextMillis(long lastTimestamp) {
    long timestamp = timeGen();
    while (timestamp <= lastTimestamp) {
      timestamp = timeGen();
    }
    return timestamp;
  }

  private long timeGen() {
    return System.currentTimeMillis();
  }

}
