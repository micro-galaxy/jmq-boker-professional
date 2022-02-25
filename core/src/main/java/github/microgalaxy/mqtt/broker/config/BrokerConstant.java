package github.microgalaxy.mqtt.broker.config;

/**
 * @author Microgalaxy
 */
public abstract class BrokerConstant {

    public static final String BROKER_KEY = "brokerId";
    public static final String BROKER_CLUSTER_KEY = "clusterKey";
    public static final String BROKER_MQTT_PORT_KEY = "mqttPort";
    public static final String BROKER_NODE_IP_KEY = "nodeIp";

    public abstract static class ShareSubscribe {
        public static final String SUBSCRIBE_SHARE_PREFIX = "$share";

        public static final String SUBSCRIBE_TIER_SPLIT = "/";

        public static final String SUBSCRIBE_ONE_TIER = "+";

        public static final String SUBSCRIBE_MULTIPLE_TIER = "#";
    }

    public abstract static class CacheKey {
        public static final String BROKER_MESSAGE_ID_CATCH = "messageIdCache";

        public static final String BROKER_CLIENT_SESSION_CATCH = "clientSessionCache";

        public static final String BROKER_DUP_PUBLISH_MESSAGE_CATCH = "dupPublishMessageCache";

        public static final String BROKER_DUP_PUB_REL_MESSAGE_CATCH = "dupPubRelMessageCache";

        public static final String BROKER_RETAIN_MESSAGE_CATCH = "retainMessageCache";

        public static final String BROKER_CLIENT_SUBSCRIBE_CATCH = "clientSubscribeCache";

        public static final String BROKER_CLIENT_SHARE_SUBSCRIBE_CATCH = "clientShareSubscribeCache";

    }

    public abstract static class SqlUtil {
        public static final String WHERE = " WHERE ";

        public static final String SELECT = "SELECT * FROM ";

        public static final String LIMIT = " LIMIT ";

        public static final String AND = " AND ";

        public static final String TRUE = " 1 = 1 ";

        public static final String EQUALS = " = ";

        public static final String OFFSET = " OFFSET ";

        public static final String STRING_CHARACTER = "'";
    }
}
