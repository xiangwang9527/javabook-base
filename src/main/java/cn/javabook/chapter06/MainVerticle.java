package cn.javabook.chapter06;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.*;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    // 创建路由
    Router router = Router.router(vertx);

    // 创建一个GET请求
    router.route(HttpMethod.GET, "/vertx/get")
            .handler(context -> {
              MultiMap queryParams = context.queryParams();
              String username = queryParams.contains("username") ? queryParams.get("username") : "unknown";
              String password = queryParams.contains("password") ? queryParams.get("password") : "unknown";
              context.json(
                      new JsonObject()
                              .put("username", username)
                              .put("password", password)
              );
            });

    // 创建一个POST请求（用Postman测试）
    router.route(HttpMethod.POST, "/vertx/api/:id/:username/")
            .handler(context -> {
              String id = context.pathParam("id");
              String username = context.pathParam("username");
              System.out.println(id + " - " + username);
              context.json(
                      new JsonObject()
                              .put("errcode", Constant.HTTP_STATUS_OK)
                              .put("message", "success")
              );
            });

    // 创建HTTP服务
    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });

    // 创建MySQL连接
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                                      .setPort(3306)
                                      .setHost("172.16.185.166")
                                      .setDatabase("javabook")
                                      .setUser("root")
                                      .setPassword("123456");
    // 连接池选项
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    // 创建连接池
    SqlClient client = MySQLBuilder
              .client()
              .with(poolOptions)
              // 官方源码中没有，但这里要记得加上vertx，否则抛异常
              .using(vertx)
              .connectingTo(connectOptions)
              .build();
    // 简单查询
    client.query("SELECT * FROM user_info")
          .execute()
          .onComplete(ar -> {
              if (ar.succeeded()) {
                  RowSet<Row> result = ar.result();
                  System.out.println("获取到了 " + result.size() + " 行数据");
                  for (Row row : result) {
                      System.out.println("id = " + row.getString("id"));
                  }
              } else {
                  System.out.println("Failure: " + ar.cause().getMessage());
              }
              // 关闭连接池
              client.close();
          });
  }
}
