package cn.javabook.chapter06;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 直接继承ReactiveCrudRepository
 *
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {
	@Query(value = "select * from user_info where id = :id")
	Mono<User> findById(String id);
}
