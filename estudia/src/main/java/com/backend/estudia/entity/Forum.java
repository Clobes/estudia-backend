package com.backend.estudia.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.backend.estudia.util.ForumType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="forums")
@OnDelete(action = OnDeleteAction.CASCADE)
public class Forum extends Line implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Column(name="description", nullable = false, length = 500)
	private String description;
	@Column(name="forumType", nullable = false, length = 50)
	private ForumType forumType;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="forum_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<TopicDiscussion> topicDiscussions;
	
}
