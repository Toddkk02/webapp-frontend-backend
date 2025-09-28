package com.todd.cyclothymic.entity;

import com.todd.cyclothymic.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Trova tutti i commenti per un post specifico, ordinati per data di creazione
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    
    // Conta i commenti per un post specifico
    Long countByPostId(Long postId);
    
    // Elimina tutti i commenti di un post (utile quando si elimina un post)
    void deleteByPostId(Long postId);
}