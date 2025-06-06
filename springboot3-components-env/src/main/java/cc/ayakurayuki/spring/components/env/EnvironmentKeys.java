package cc.ayakurayuki.spring.components.env;

public class EnvironmentKeys {

  /**
   * host machine name
   */
  public static final Key<String> HOSTNAME = Environment.keyWithDefault("hostname", Environments.STRING_MARSHALLER, Environments.HOSTNAME);

  /**
   * host machine ip
   */
  public static final Key<String> HOST = Environment.keyWithDefault("host", Environments.STRING_MARSHALLER, Environments.IP);

  /**
   * instance ID
   */
  public static final Key<String> INSTANCE_ID = Environment.keyWithDefault("HOSTNAME", Environments.STRING_MARSHALLER, Environments.HOSTNAME);

  /**
   * region: sh
   */
  public static final Key<String> REGION = Environment.key("region", Environments.STRING_MARSHALLER);

  /**
   * available zone: sh001
   */
  public static final Key<String> ZONE = Environment.key("zone", Environments.STRING_MARSHALLER);

  /**
   * APP ID：xxx.xxx.xxx
   */
  public static final Key<String> APP_ID = Environment.key("app_id", Environments.STRING_MARSHALLER);

  /**
   * App Group ID
   */
  public static final Key<String> APP_GROUP_ID = Environment.key("app_group_id", Environments.STRING_MARSHALLER);

  /**
   * services tree ID
   */
  public static final Key<Integer> TREE_ID = Environment.key("tree_id", Environments.INT_MARSHALLER);

  /**
   * deployment environment, like dev, fat, uat, pre, prod
   */
  public static final Key<String> DEPLOY_ENV = Environment.key("deploy_env", Environments.STRING_MARSHALLER);

}
