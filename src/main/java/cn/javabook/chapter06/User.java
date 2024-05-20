package cn.javabook.chapter06;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 */
@Document(collection = Constant.MONGO_COLLECTION_NAME)
@Table(value = Constant.MONGO_COLLECTION_NAME)
public class User {
	@Id
	private Integer id;
	private String username;
	private String password;
	private String nickname;
	private Integer gender;
	private String avatar;
	private LocalDateTime createtime;
	private LocalDateTime updatetime;

	public User(Integer id, String username, String password, String nickname, Integer gender, String avatar, LocalDateTime createtime, LocalDateTime updatetime) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.gender = gender;
		this.avatar = avatar;
		this.createtime = createtime;
		this.updatetime = updatetime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public LocalDateTime getCreatetime() {
		return createtime;
	}

	public void setCreatetime(LocalDateTime createtime) {
		this.createtime = createtime;
	}

	public LocalDateTime getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(LocalDateTime updatetime) {
		this.updatetime = updatetime;
	}
}
