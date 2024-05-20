package cn.javabook.chapter06;

import java.util.ArrayList;
import java.util.List;

/**
 * 剧院
 *
 */
public class Theatre {
    // 订阅接口
    @FunctionalInterface
    interface Viewer {
        public void watch();
    }

    // 观众（具体订阅者）
    static class ConcreteViewer implements Viewer {
        @Override
        public void watch() {
            System.out.println("正在看表演");
        }
    }

    // 演员（发布者）
    static class Actor {
        // 观众（订阅者）列表
        private final List<Viewer> viewers = new ArrayList<>();

        // 售票（订阅）
        public void sell(Viewer viewer) {
            viewers.add(viewer);
        }

        // 开始表演
        public void play() {
            for (Viewer viewer : viewers) {
                viewer.watch();
            }
        }
    }

    public static void main(String[] args) {
        ConcreteViewer concreteViewer1 = new ConcreteViewer();
        ConcreteViewer concreteViewer2 = new ConcreteViewer();
        Actor actor = new Actor();
        actor.sell(concreteViewer1);
        actor.sell(concreteViewer2);
        actor.sell(() -> System.out.println("正在包厢看表演"));
        actor.play();
    }
}
