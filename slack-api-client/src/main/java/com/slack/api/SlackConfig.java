package com.slack.api;

import com.slack.api.audit.AuditClient;
import com.slack.api.audit.AuditConfig;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.MethodsConfig;
import com.slack.api.rate_limits.RateLimiter;
import com.slack.api.scim.SCIMClient;
import com.slack.api.scim.SCIMConfig;
import com.slack.api.status.v1.LegacyStatusClient;
import com.slack.api.status.v2.StatusClient;
import com.slack.api.util.http.listener.DetailedLoggingListener;
import com.slack.api.util.http.listener.HttpResponseListener;
import com.slack.api.util.http.listener.ResponsePrettyPrintingListener;
import com.slack.api.util.thread.DaemonThreadExecutorServiceProvider;
import com.slack.api.util.thread.ExecutorServiceProvider;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The basic configuration of this SDK. Some settings can be propagated to submodules such as Bolt.
 */
@Data
public class SlackConfig {

    /**
     * The default instance is immutable. It's not allowed to modify the value runtime for any reasons.
     */
    public static final SlackConfig DEFAULT = new SlackConfig() {

        void throwException() {
            throw new UnsupportedOperationException("This config is immutable");
        }

        @Override
        public void setFailOnUnknownProperties(boolean failOnUnknownProperties) {
            throwException();
        }

        @Override
        public void setPrettyResponseLoggingEnabled(boolean prettyResponseLoggingEnabled) {
            throwException();
        }

        @Override
        public void setLibraryMaintainerMode(boolean libraryMaintainerMode) {
            throwException();
        }

        @Override
        public void setTokenExistenceVerificationEnabled(boolean tokenExistenceVerificationEnabled) {
            throwException();
        }

        @Override
        public void setHttpClientResponseHandlers(List<HttpResponseListener> httpClientResponseHandlers) {
            throwException();
        }

        @Override
        public void setAuditEndpointUrlPrefix(String auditEndpointUrlPrefix) {
            throwException();
        }

        @Override
        public void setMethodsEndpointUrlPrefix(String methodsEndpointUrlPrefix) {
            throwException();
        }

        @Override
        public void setScimEndpointUrlPrefix(String scimEndpointUrlPrefix) {
            throwException();
        }

        @Override
        public void setStatusEndpointUrlPrefix(String statusEndpointUrlPrefix) {
            throwException();
        }

        @Override
        public void setLegacyStatusEndpointUrlPrefix(String legacyStatusEndpointUrlPrefix) {
            throwException();
        }

        @Override
        public void setStatsEnabled(boolean statsEnabled) {
            throwException();
        }

        @Override
        public void setMethodsConfig(MethodsConfig methodsConfig) {
            throwException();
        }

        @Override
        public void setAuditConfig(AuditConfig auditConfig) {
            throwException();
        }

        @Override
        public void setSCIMConfig(SCIMConfig sCIMConfig) {
            throwException();
        }

        @Override
        public void setProxyUrl(String proxyUrl) {
            throwException();
        }

        @Override
        public void setProxyHeaders(Map<String, String> proxyHeaders) {
            throwException();
        }

        @Override
        public void setHttpClientCallTimeoutMillis(Integer httpClientCallTimeoutMillis) {
            throwException();
        }

        @Override
        public void setHttpClientWriteTimeoutMillis(Integer httpClientWriteTimeoutMillis) {
            throwException();
        }

        @Override
        public void setHttpClientReadTimeoutMillis(Integer httpClientReadTimeoutMillis) {
            throwException();
        }

        @Override
        public void setExecutorServiceProvider(ExecutorServiceProvider executorServiceProvider) {
            throwException();
        }

        @Override
        public void setRateLimiterBackgroundJobIntervalMillis(Long rateLimiterBackgroundJobIntervalMillis) {
            throwException();
        }
    };

    public SlackConfig() {
        getHttpClientResponseHandlers().add(new DetailedLoggingListener());
        getHttpClientResponseHandlers().add(new ResponsePrettyPrintingListener());
    }

    /**
     * The underlying HTTP client's read timeout (in milliseconds). The default is 10 seconds.
     * https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/read-timeout-millis/
     */
    private Integer httpClientReadTimeoutMillis;

    /**
     * The underlying HTTP client's write timeout (in milliseconds). The default is 10 seconds.
     * https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/write-timeout-millis/
     */
    private Integer httpClientWriteTimeoutMillis;

    /**
     * The underlying HTTP client's call timeout (in milliseconds).
     * By default, there is no timeout for complete calls while there is for connect/write/read actions within a call.
     * https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/call-timeout-millis/
     */
    private Integer httpClientCallTimeoutMillis;

    /**
     * The proxy server URL supposed to be used for all api calls.
     */
    private String proxyUrl = initProxyUrl();

    // This method runs only once when instantiating this object.
    // If you want to reflect dynamically updated system properties or env variables,
    // create a new instance by invoking the default constructor.
    private static String initProxyUrl() {
        String host = System.getProperty("http.proxyHost");
        if (host != null) {
            String user = System.getProperty("http.proxyUser");
            String password = System.getProperty("http.proxyPassword");
            if (user != null && password != null) {
                String port = System.getProperty("http.proxyPort");
                if (port != null) {
                    return "http://" + user + ":" + password + "@" + host + ":" + port;
                } else {
                    return "http://" + user + ":" + password + "@" + host;
                }
            }
            String port = System.getProperty("http.proxyPort");
            if (port != null) {
                return "http://" + host + ":" + port;
            } else {
                return "http://" + host;
            }
        }
        String httpsEnvProxyUrl = System.getenv("HTTPS_PROXY");
        if (httpsEnvProxyUrl != null && !httpsEnvProxyUrl.trim().isEmpty()) {
            return httpsEnvProxyUrl;
        }
        String httpEnvProxyUrl = System.getenv("HTTP_PROXY");
        if (httpEnvProxyUrl != null && !httpEnvProxyUrl.trim().isEmpty()) {
            return httpEnvProxyUrl;
        }
        return null;
    }

    /**
     * Additional headers for proxy (e.g., Proxy-Authorization)
     */
    private Map<String, String> proxyHeaders;

    private boolean prettyResponseLoggingEnabled = false;

    /**
     * Don't enable this flag in production. This flag enables some validation features for development.
     */
    private boolean libraryMaintainerMode = false;

    public void setLibraryMaintainerMode(boolean libraryMaintainerMode) {
        this.libraryMaintainerMode = libraryMaintainerMode;
        this.synchronizeLibraryMaintainerMode();
    }

    /**
     * If you would like to detect unknown properties by throwing exceptions, set this flag as true.
     */
    private boolean failOnUnknownProperties = false;

    /**
     * Slack Web API client verifies the existence of tokens before sending HTTP requests to Slack servers.
     */
    private boolean tokenExistenceVerificationEnabled = false;

    private List<HttpResponseListener> httpClientResponseHandlers = new ArrayList<>();

    private String auditEndpointUrlPrefix = AuditClient.ENDPOINT_URL_PREFIX;

    private String methodsEndpointUrlPrefix = MethodsClient.ENDPOINT_URL_PREFIX;

    private String scimEndpointUrlPrefix = SCIMClient.ENDPOINT_URL_PREFIX;

    private String statusEndpointUrlPrefix = StatusClient.ENDPOINT_URL_PREFIX;

    private String legacyStatusEndpointUrlPrefix = LegacyStatusClient.ENDPOINT_URL_PREFIX;

    @Builder.Default
    private ExecutorServiceProvider executorServiceProvider = DaemonThreadExecutorServiceProvider.getInstance();

    @Builder.Default
    private Long rateLimiterBackgroundJobIntervalMillis = RateLimiter.DEFAULT_BACKGROUND_JOB_INTERVAL_MILLIS;

    public void setRateLimiterBackgroundJobIntervalMillis(Long rateLimiterBackgroundJobIntervalMillis) {
        if (rateLimiterBackgroundJobIntervalMillis == 0) {
            throw new IllegalArgumentException(
                    "0 millisecond is not a valid value for rateLimiterBackgroundJobIntervalMillis");
        }
        this.rateLimiterBackgroundJobIntervalMillis = rateLimiterBackgroundJobIntervalMillis;
        this.synchronizeMetricsDatabases();
    }

    @Builder.Default
    private boolean statsEnabled = true;

    public void setStatsEnabled(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
        this.getMethodsConfig().setStatsEnabled(this.isStatsEnabled());
        this.getSCIMConfig().setStatsEnabled(this.isStatsEnabled());
        this.getAuditConfig().setStatsEnabled(this.isStatsEnabled());
        this.synchronizeMetricsDatabases();
    }

    private MethodsConfig methodsConfig = new MethodsConfig();

    private AuditConfig auditConfig = new AuditConfig();

    private SCIMConfig sCIMConfig = new SCIMConfig();

    public void synchronizeMetricsDatabases() {
        this.synchronizeExecutorServiceProviders();

        if (!methodsConfig.equals(MethodsConfig.DEFAULT_SINGLETON)) {
            if (methodsConfig.isStatsEnabled()) {
                if (methodsConfig.getMetricsDatastore().getRateLimiterBackgroundJobIntervalMillis()
                        != this.getRateLimiterBackgroundJobIntervalMillis()) {
                    methodsConfig.getMetricsDatastore().setRateLimiterBackgroundJobIntervalMillis(
                            this.getRateLimiterBackgroundJobIntervalMillis());
                }
            } else {
                methodsConfig.getMetricsDatastore().setStatsEnabled(false);
            }
        }
        if (!auditConfig.equals(auditConfig.DEFAULT_SINGLETON)) {
            if (auditConfig.isStatsEnabled()) {
                if (auditConfig.getMetricsDatastore().getRateLimiterBackgroundJobIntervalMillis()
                        != this.getRateLimiterBackgroundJobIntervalMillis()) {
                    auditConfig.getMetricsDatastore().setRateLimiterBackgroundJobIntervalMillis(
                            this.getRateLimiterBackgroundJobIntervalMillis());
                }
            } else {
                auditConfig.getMetricsDatastore().setStatsEnabled(false);
            }
        }
        if (!sCIMConfig.equals(sCIMConfig.DEFAULT_SINGLETON)) {
            if (sCIMConfig.isStatsEnabled()) {
                if (sCIMConfig.getMetricsDatastore().getRateLimiterBackgroundJobIntervalMillis()
                        != this.getRateLimiterBackgroundJobIntervalMillis()) {
                    sCIMConfig.getMetricsDatastore().setRateLimiterBackgroundJobIntervalMillis(
                            this.getRateLimiterBackgroundJobIntervalMillis());
                }
            } else {
                sCIMConfig.getMetricsDatastore().setStatsEnabled(false);
            }
        }
    }

    public void synchronizeExecutorServiceProviders() {
        if (!methodsConfig.equals(MethodsConfig.DEFAULT_SINGLETON)
                && methodsConfig.isStatsEnabled()
                && !methodsConfig.getExecutorServiceProvider().equals(executorServiceProvider)) {
            methodsConfig.setExecutorServiceProvider(executorServiceProvider);
            methodsConfig.getMetricsDatastore().setExecutorServiceProvider(executorServiceProvider);
        }
        if (!auditConfig.equals(AuditConfig.DEFAULT_SINGLETON)
                && auditConfig.isStatsEnabled()
                && !auditConfig.getExecutorServiceProvider().equals(executorServiceProvider)) {
            auditConfig.setExecutorServiceProvider(executorServiceProvider);
            auditConfig.getMetricsDatastore().setExecutorServiceProvider(executorServiceProvider);
        }
        if (!sCIMConfig.equals(SCIMConfig.DEFAULT_SINGLETON)
                && sCIMConfig.isStatsEnabled()
                && !sCIMConfig.getExecutorServiceProvider().equals(executorServiceProvider)) {
            sCIMConfig.setExecutorServiceProvider(executorServiceProvider);
            sCIMConfig.getMetricsDatastore().setExecutorServiceProvider(executorServiceProvider);
        }
        this.synchronizeLibraryMaintainerMode();
    }

    public void synchronizeLibraryMaintainerMode() {
        methodsConfig.getMetricsDatastore().setTraceMode(this.isLibraryMaintainerMode());
        auditConfig.getMetricsDatastore().setTraceMode(this.isLibraryMaintainerMode());
        sCIMConfig.getMetricsDatastore().setTraceMode(this.isLibraryMaintainerMode());
    }

}
