package github.microgalaxy.mqtt.broker.security;

import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.security.GridSecurityProcessor;
import org.apache.ignite.internal.processors.security.SecurityContext;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.plugin.*;
import org.apache.ignite.plugin.security.SecurityException;
import org.apache.ignite.plugin.security.*;
import org.apache.ignite.spi.IgniteNodeValidationResult;
import org.apache.ignite.spi.discovery.DiscoveryDataBag;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * broker 内部API安全
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class BrokerSecurity implements PluginProvider<PluginConfiguration>, GridSecurityProcessor, IgnitePlugin {
    @Autowired
    private BrokerProperties brokerProperties;

    @Override
    public String name() {
        return "Jmq broker security plugin";
    }

    @Override
    public String version() {
        return "v1.0.0";
    }

    @Override
    public String copyright() {
        return "Copyright © Jmq. All Rights Reserved";
    }

    @Override
    public <T extends IgnitePlugin> T plugin() {
        return (T) this;
    }

    @Override
    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) throws IgniteCheckedException {

    }

    @Override
    public <T> @Nullable T createComponent(PluginContext ctx, Class<T> cls) {
        if (cls.isAssignableFrom(GridSecurityProcessor.class)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public CachePluginProvider createCacheProvider(CachePluginContext ctx) {
        return null;
    }

    @Override
    public void start(PluginContext ctx) throws IgniteCheckedException {

    }

    @Override
    public void start() throws IgniteCheckedException {

    }

    @Override
    public void stop(boolean cancel) throws IgniteCheckedException {

    }

    @Override
    public void onKernalStart(boolean active) throws IgniteCheckedException {

    }

    @Override
    public void onKernalStop(boolean cancel) {

    }

    @Override
    public void collectJoiningNodeData(DiscoveryDataBag dataBag) {

    }

    @Override
    public void collectGridNodeData(DiscoveryDataBag dataBag) {

    }

    @Override
    public void onGridDataReceived(DiscoveryDataBag.GridDiscoveryData data) {

    }

    @Override
    public void onJoiningNodeDataReceived(DiscoveryDataBag.JoiningNodeDiscoveryData data) {

    }

    @Override
    public void printMemoryStats() {

    }

    @Override
    public @Nullable IgniteNodeValidationResult validateNode(ClusterNode node) {
        String rmtClusterKey = node.attribute(BrokerConstant.BROKER_CLUSTER_KEY);
        boolean match = Objects.equals(brokerProperties.getClusterKey(), rmtClusterKey);
        return match ? null : new IgniteNodeValidationResult(node.id(), "Access denied");
    }

    @Override
    public @Nullable IgniteNodeValidationResult validateNode(ClusterNode node, DiscoveryDataBag.JoiningNodeDiscoveryData discoData) {
        return new IgniteNodeValidationResult(node.id(), "Access denied");
    }

    @Override
    public @Nullable DiscoveryDataExchangeType discoveryDataType() {
        return null;
    }

    @Override
    public void onDisconnected(IgniteFuture<?> reconnectFut) throws IgniteCheckedException {

    }

    @Override
    public @Nullable IgniteInternalFuture<?> onReconnected(boolean clusterRestarted) throws IgniteCheckedException {
        return null;
    }

    @Override
    public void onIgniteStart() throws IgniteCheckedException {

    }

    @Override
    public void onIgniteStop(boolean cancel) {

    }

    @Override
    public @Nullable Serializable provideDiscoveryData(UUID nodeId) {
        return null;
    }

    @Override
    public void receiveDiscoveryData(UUID nodeId, Serializable data) {
    }

    @Override
    public void validateNewNode(ClusterNode node) throws PluginValidationException {
    }

    //Node join auth
    @Override
    public SecurityContext authenticateNode(ClusterNode node, SecurityCredentials cred) throws IgniteException {
        String rmtClusterKey = node.attribute(BrokerConstant.BROKER_CLUSTER_KEY);
        boolean match = Objects.equals(brokerProperties.getClusterKey(), rmtClusterKey);
        return new BrokerSecurityContext(match, rmtClusterKey);
    }

    @Override
    public boolean isGlobalNodeAuthentication() {
        return true;
    }

    //Client connection auth
    @Override
    public SecurityContext authenticate(AuthenticationContext ctx) throws IgniteCheckedException {
        return null;
    }

    @Override
    public Collection<SecuritySubject> authenticatedSubjects() throws IgniteCheckedException {
        return null;
    }

    @Override
    public SecuritySubject authenticatedSubject(UUID subjId) throws IgniteCheckedException {
        return null;
    }

    //cathe option auth
    @Override
    public void authorize(String name, SecurityPermission perm, SecurityContext securityCtx) throws SecurityException {
    }

    @Override
    public void onSessionExpired(UUID subjId) {

    }

    @Override
    public boolean enabled() {
        return true;
    }

    private static class BrokerSecurityContext implements SecurityContext, Serializable {
        private final boolean match;
        private final String clusterKey;

        public BrokerSecurityContext(boolean match, String clusterKey) {
            this.match = match;
            this.clusterKey = clusterKey;
        }

        @Override
        public SecuritySubject subject() {
            return new SecuritySubject() {
                @Override
                public SecurityPermissionSet permissions() {
                    return new SecurityPermissionSet() {
                        @Override
                        public boolean defaultAllowAll() {
                            return match;
                        }

                        @Override
                        public Map<String, Collection<SecurityPermission>> taskPermissions() {
                            return Collections.EMPTY_MAP;
                        }

                        @Override
                        public Map<String, Collection<SecurityPermission>> cachePermissions() {
                            return Collections.EMPTY_MAP;
                        }

                        @Override
                        public Map<String, Collection<SecurityPermission>> servicePermissions() {
                            return Collections.EMPTY_MAP;
                        }

                        @Override
                        public @Nullable Collection<SecurityPermission> systemPermissions() {
                            return Collections.EMPTY_LIST;
                        }
                    };
                }

                @Override
                public UUID id() {
                    return null;
                }

                @Override
                public SecuritySubjectType type() {
                    return SecuritySubjectType.REMOTE_NODE;
                }

                @Override
                public Object login() {
                    return clusterKey;
                }

                @Override
                public InetSocketAddress address() {
                    return null;
                }
            };
        }

        @Override
        public boolean taskOperationAllowed(String taskClsName, SecurityPermission perm) {
            return match;
        }

        @Override
        public boolean cacheOperationAllowed(String cacheName, SecurityPermission perm) {
            return match;
        }

        @Override
        public boolean serviceOperationAllowed(String srvcName, SecurityPermission perm) {
            return match;
        }

        @Override
        public boolean systemOperationAllowed(SecurityPermission perm) {
            return match;
        }
    }
}
