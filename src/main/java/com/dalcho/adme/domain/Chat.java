package com.dalcho.adme.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_id")
	private Long idx;

	@Column(nullable = false)
	private String roomId;

	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@ToString.Exclude
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Chat(String roomId, User user) {
		this.roomId = roomId;
		this.user = user;
	}

	public void addUser(User user){
		user.addChat(this);
		this.user = user;
	}
}

