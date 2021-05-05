package com.backend.estudia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.backend.estudia.entity.Comment;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {

	@Modifying
    @Query("SELECT c FROM Comment AS c WHERE topic_disc_id=:id ORDER BY creation_date")
	public List<Comment> findByTopicDiscIdOrderByCreationDate(@Param("id") Long id);
	
	@Modifying
    @Query("DELETE FROM Comment WHERE id=:id")
    void customDeleteCommentById(@Param("id") Long id);
	
}
