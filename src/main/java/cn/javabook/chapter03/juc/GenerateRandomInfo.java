package cn.javabook.chapter03.juc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 随机姓名产生器
 *
 */
public class GenerateRandomInfo {
    private static final String firstname = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻水云苏潘" +
            "葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳鲍史唐费岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛" +
            "禹狄米贝明臧计成戴宋茅庞熊纪舒屈项祝董粱杜阮席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田胡凌霍万柯卢莫房缪干解应宗丁" +
            "宣邓郁单杭洪包诸左石崔吉龚程邢滑裴陆荣翁荀羊甄家封芮储靳邴松井富乌焦巴弓牧隗山谷车侯伊宁仇祖武符刘景詹束龙叶幸司韶黎乔苍" +
            "双闻莘劳逄姬冉宰桂牛寿通边燕冀尚农温庄晏瞿茹习鱼容向古戈终居衡步都耿满弘国文东殴沃曾关红游盖益桓公晋楚闫";
    private static final String lastname = "伟刚勇毅俊秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环" +
            "雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦" +
            "岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽峰强军平保东文" +
            "辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先" +
            "敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家" +
            "致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

    /**
     * 随机产生姓氏
     *
     */
    public static String getFirstName() {
        int strLen = firstname.length();
        int index = new Random().nextInt(strLen - 1);
        return firstname.substring(index, index + 1);
    }

    /**
     * 随机产生名字
     *
     */
    public static String getLastName() {
        int strLen = lastname.length();
        int index = new Random().nextInt(strLen - 1);
        return lastname.substring(index, index + 2);
    }

    public static void count(final String mapName, final List<String> list, final Map<String, Integer> map) {
        long start = System.currentTimeMillis();
        // 这里为了比较效率，特意调大了人数
        for (int i = 0; i < 10_000_000; i++) {
            String name = getFirstName() + getLastName();
            list.add(name);
        }
        System.out.println(mapName + " 产生了 " + list.size() + " 个名字");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                list.stream().map(str -> str.substring(0, 1)).forEach(str -> {
                    Integer count = 1;
                    if (map.containsKey(str)) {
                        // 多线程情况下，普通的HashMap的get()方法得到的结果可能为null
                        // 但ConcurrentHashMap一定不会是null
                        count = map.get(str);
                        if (null == count) {
                            System.out.println("count == null");
                        }
                        ++count;
                    }
                    map.put(str, count);
                });
                System.out.println("总共 " + map.size() + " 个姓氏");
            }).start();
        }
        int count = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            count = count + entry.getValue();
        }
        long end = System.currentTimeMillis();
        // 最后统计名字总数
        System.out.println("执行时间：" + (end - start) + " 毫秒");
    }

    @Override
    protected void finalize() throws Throwable {

    }

    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        count("HashMap", list, map);

        System.out.println("========================");

        list = new ArrayList<>();
        map = new ConcurrentHashMap<>();
        count("ConcurrentHashMap", list, map);
    }
}
