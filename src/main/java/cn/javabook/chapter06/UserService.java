package cn.javabook.chapter06;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;

/**
 * 用户service
 *
 */
@Service
public class UserService {
	private final UserRepository userRepository;

	@Resource
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Resource
	private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Flux<User> getUsers() {
		return userRepository.findAll();
	}

	public Mono<User> getUserById(String id) {
		return userRepository.findById(id)
				             .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid id：" + id)));
	}

	public Mono<User> updateUserInfo(User user, String nickname) {
		if (!StringUtils.isEmpty(nickname)) {
			user.setNickname(nickname);
			return userRepository.save(user);
		} else {
			return Mono.error(new IllegalArgumentException("invalid parameters"));
		}
	}

	public Flux<User> getMongoUsers() {
		return reactiveMongoTemplate.findAll(User.class);
	}

	public Mono<Object> addMongoUser(Object object, String collection) {
		return reactiveMongoTemplate.insert(object, collection);
	}

	public Mono<String> redisGet(String key) {
		return reactiveStringRedisTemplate.opsForValue().get(key);
	}

	public Mono<Long> redisSet(String key, String value, Long expireTime) {
		return reactiveStringRedisTemplate.opsForValue().set(key, value, expireTime);
	}
}
