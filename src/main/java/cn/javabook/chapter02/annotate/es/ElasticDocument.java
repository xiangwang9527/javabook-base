package cn.javabook.chapter02.annotate.es;

/**
 * elastic文档对象
 *
 */
@Document(index = "document", type = "_doc", shards = 1, replicas = 0)
public class ElasticDocument {
    private static final long serialVersionUID = 2879048112350101009L;

    // 文档编码
    @DocField(name = "guid", type = FieldType.Keyword)
    protected String guid = "";

    // 标题
    @DocField(name = "title", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    protected String title = "";

    // 文档创建时间（资源实际创建时间）
    @DocField(name = "createtime", type = FieldType.Long)
    protected long createtime;

    // 文档更新时间（资源实际更新时间）
    @DocField(name = "updatetime", type = FieldType.Long)
    protected long updatetime;

    public ElasticDocument() {
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return String.format("{\"guid\":\"%s\", \"title\":\"%s\", \"createtime\":%d, " +
                        "\"updatetime\":%d}", guid, title, createtime, updatetime);
    }
}
