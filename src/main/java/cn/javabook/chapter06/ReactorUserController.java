package cn.javabook.chapter06;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

/**
 * 用户Controller
 *
 */
@RestController
public class ReactorUserController {
	private final UserService userService;

	public ReactorUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/mysql/users")
	public Flux<User> users() {
		return userService.getUsers();
	}

	@PutMapping("/mysql/user/{id}")
	public Mono<JSONObject> update(@PathVariable("id") String id, @NonNull String nickname) {
		return userService.getUserById(id)
						  .zipWhen(user -> userService.updateUserInfo(user, nickname), (user, userSaved) -> userSaved)
						  .map(user -> ResultUtil.success());
	}

	@GetMapping("/mongo/users")
	public Flux<User> mongoUsers() {
		return userService.getMongoUsers();
	}

	@PostMapping("/mongo/add")
	public Mono<JSONObject> add(@NonNull Integer id, @NonNull String username, @NonNull String password,
								@NonNull String nickname, @NonNull int gender, @NonNull String avatar) {
		User user = new User(id, username, password, nickname, gender, avatar, LocalDateTime.now(), LocalDateTime.now());
		Mono<Object> mono = userService.addMongoUser(user, Constant.MONGO_COLLECTION_NAME);
		return mono.flatMap(result -> Mono.just(ResultUtil.success()));
	}

	@GetMapping("/redis/getString")
	public Mono<String> redisGet(@NonNull String key) {
		return userService.redisGet(key).map(result -> ResultUtil.extra(result).toJSONString());
	}

	@PostMapping("/redis/setString")
	public Mono<Long> redisSet(@NonNull String key, @NonNull String value, @NonNull Long expireTime) {
		return userService.redisSet(key, value, expireTime);
	}
}
