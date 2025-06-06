package cc.ayakurayuki.spring.components.opentelemetry;

public class OpenTelemetryProperties {

  /**
   * Port of collector which accept jaeger gRPC protocol.
   */
  private int port = 14250;

  private JaegerRemoteSampler jaegerRemoteSampler = new JaegerRemoteSampler();
  private BatchSpanProcessor  batchSpanProcessor  = new BatchSpanProcessor();

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public JaegerRemoteSampler getJaegerRemoteSampler() {
    return jaegerRemoteSampler;
  }

  public void setJaegerRemoteSampler(JaegerRemoteSampler jaegerRemoteSampler) {
    this.jaegerRemoteSampler = jaegerRemoteSampler;
  }

  public BatchSpanProcessor getBatchSpanProcessor() {
    return batchSpanProcessor;
  }

  public void setBatchSpanProcessor(BatchSpanProcessor batchSpanProcessor) {
    this.batchSpanProcessor = batchSpanProcessor;
  }

  public static class JaegerRemoteSampler {

    private int    pollingIntervalInMillis = 60000;
    private double initialSamplingRate     = 0.001;

    public int getPollingIntervalInMillis() {
      return pollingIntervalInMillis;
    }

    public void setPollingIntervalInMillis(int pollingIntervalInMillis) {
      this.pollingIntervalInMillis = pollingIntervalInMillis;
    }

    public double getInitialSamplingRate() {
      return initialSamplingRate;
    }

    public void setInitialSamplingRate(double initialSamplingRate) {
      this.initialSamplingRate = initialSamplingRate;
    }

  }

  public static class BatchSpanProcessor {

    private int maxQueueSize          = 100;
    private int scheduleDelayInMillis = 1000;

    public int getMaxQueueSize() {
      return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
      this.maxQueueSize = maxQueueSize;
    }

    public int getScheduleDelayInMillis() {
      return scheduleDelayInMillis;
    }

    public void setScheduleDelayInMillis(int scheduleDelayInMillis) {
      this.scheduleDelayInMillis = scheduleDelayInMillis;
    }

  }

}
