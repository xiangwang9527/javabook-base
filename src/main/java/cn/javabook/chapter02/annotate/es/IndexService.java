package cn.javabook.chapter02.annotate.es;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 创建索引
 *
 */
@Service
public class IndexService {
    @Resource
    private ElasticDao elasticDao;

    /**
     * 索引初始化
     *
     */
    @PostConstruct
    private void initIndex() {
        boolean flag = false;
        // 创建一个名为TestIndex的索引
        if (!elasticDao.indexExist("TestIndex")) {
            flag = elasticDao.createIndex("TestIndex", ElasticDocument.class);
            if (flag) {
                System.out.println("create TestIndex success");
            } else {
                System.out.println("create TestIndex failure");
            }
        } else {
            System.out.println("TestIndex has exist");
        }
    }
}
