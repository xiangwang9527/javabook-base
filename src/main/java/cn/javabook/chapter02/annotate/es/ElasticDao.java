package cn.javabook.chapter02.annotate.es;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * ElasticDao
 *
 */
@Component
public class ElasticDao {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 索引是否存在
     *
     */
    public boolean indexExist(final String index) {
        try {
            return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("index exist exception");
        }
        return false;
    }

    /**
     * 解析类注解，获取包括父类字段在内的所有字段
     * 因为解析的时候，会把父类及自身的一些额外字段给解析进去
     * 如logger、serialVersionUID等
     * 所以需要把这些无用的字段排除掉
     * 这里不存在继承，所以直接调用clazz.getDeclaredFields()
     * 另外，如果存在继承关系，该怎么处理呢？（可以作为思考练习）
     *
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        return new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
    }

    /**
     * 创建索引，前面都是为了实现它作准备
     * 这里会通过注解，一路解析文档的字段，拼接成可执行的脚本交给elasticsearch的api去执行
     *
     */
    public boolean createIndex(final String index, final Class<?> clazz) {
        try {
            Document document = (Document) clazz.getAnnotation(Document.class);
            int shards = document.shards();
            int replicas = document.replicas();
            if (indexExist(index)) {
                return false;
            }

            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", shards)
                    .put("index.number_of_replicas", replicas)
            );
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("   \"properties\": {\n");

            List<Field> list = getAllDeclaredFields(clazz);
            int length = list.size();
            for (int i = 0; i < length; i++) {
                DocField docField = list.get(i).getAnnotation(DocField.class);
                if (null == docField) {
                    continue;
                }
                builder.append("      \"").append(docField.name()).append("\" : {\n");
                builder.append("         \"type\" : \"").append(docField.type().value).append("\"");
                if (docField.index()) {
                    builder.append(", \n");
                    builder.append("         \"index\" : ").append(docField.index());
                }
                if (docField.fielddata()) {
                    builder.append(", \n");
                    builder.append("         \"fielddata\" : ").append(docField.fielddata());
                }
                if (docField.store()) {
                    builder.append(", \n");
                    builder.append("         \"store\" : ").append(docField.store());
                }
                if (StringUtils.isNotBlank(docField.analyzer())) {
                    builder.append(", \n");
                    builder.append("         \"analyzer\" : \"").append(docField.analyzer()).append("\"");
                }
                if (StringUtils.isNotBlank(docField.format())) {
                    builder.append(", \n");
                    builder.append("         \"format\" : \"").append(docField.format()).append("\"");
                }
                if (StringUtils.isNotBlank(docField.searchAnalyzer())) {
                    builder.append(", \n");
                    builder.append("         \"search_analyzer\" : \"").append(docField.searchAnalyzer()).append("\"");
                }
                if (StringUtils.isNotBlank(docField.pattern())) {
                    builder.append(", \n");
                    builder.append("         \"pattern\" : \"").append(docField.pattern()).append("\"");
                }
                if (StringUtils.isNotBlank(docField.normalizer())) {
                    builder.append(", \n");
                    builder.append("         \"normalizer\" : \"").append(docField.normalizer()).append("\"");
                }
                if (i == length -1) {
                    builder.append("\n      }\n");
                } else {
                    builder.append("\n      }, \n");
                }
            }
            builder.append("   }\n");
            builder.append("}\n");
            request.mapping(JSON.parseObject(builder.toString()).toJSONString(), XContentType.JSON);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            return acknowledged;
        } catch (IOException e) {
            System.out.println("create index exception");
        }
        return false;
    }
}
