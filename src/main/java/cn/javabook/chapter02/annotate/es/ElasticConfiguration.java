package cn.javabook.chapter02.annotate.es;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置类
 *
 */
@Configuration
public class ElasticConfiguration {
    @Value("${spring.elastic.rhlc.schema}")
    private String schema;
    @Value("${spring.elastic.rhlc.hosts}")
    private String hosts;
    @Value("${spring.elastic.rhlc.username}")
    private String username;
    @Value("${spring.elastic.rhlc.password}")
    private String password;
    @Value("${spring.elastic.rhlc.connectTimeOut}")
    private int connectTimeOut;
    @Value("${spring.elastic.rhlc.socketTimeOut}")
    private int socketTimeOut;
    @Value("${spring.elastic.rhlc.connectionRequestTimeOut}")
    private int connectionRequestTimeOut;

    @Bean
    public RestHighLevelClient client() {
        String[] hosts = this.hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            httpHosts[i] = new HttpHost(hosts[i].split(":")[0], Integer.parseInt(hosts[i].split(":")[1]), schema);
        }

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(httpHosts).setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeOut);
            requestConfigBuilder.setSocketTimeout(socketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
            return requestConfigBuilder;
        }).setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.disableAuthCaching();
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        });

        return new RestHighLevelClient(builder);
    }
}
