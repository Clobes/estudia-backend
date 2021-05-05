package com.backend.estudia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.backend.estudia.entity.TopicDiscussion;

@Repository
public interface ITopicDiscussionRepository extends JpaRepository<TopicDiscussion, Long>{

	@Modifying
    @Query("DELETE FROM TopicDiscussion WHERE id=:id")
    void customDeleteTopicDiscussionById(@Param("id") Long id);
	
}
