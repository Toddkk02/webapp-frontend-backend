package com.todd.cyclothymic.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import com.todd.cyclothymic.entity.Comment;
import com.todd.cyclothymic.entity.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    // GET tutti i post
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findByPublishedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(posts);
    }

    // GET ultimi 5 post per homepage
    @GetMapping("/latest")
    public ResponseEntity<List<Post>> getLatestPosts() {
        List<Post> posts = postRepository.findTop5ByPublishedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(posts);
    }

    // GET singolo post (incrementa view count)
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // Incrementa view count
            if (post.getViewCount() == null) {
                post.setViewCount(1);
            } else {
                post.setViewCount(post.getViewCount() + 1);
            }
            postRepository.save(post);
            return ResponseEntity.ok(post);
        }
        
        return ResponseEntity.notFound().build();
    }

    // POST nuovo post
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String summary = (String) request.get("summary");
            
            Map<String, Object> response = new HashMap<>();
            
            // Validazione
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Titolo richiesto");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Contenuto richiesto");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crea nuovo post
            Post newPost = new Post();
            newPost.setTitle(title.trim());
            newPost.setContent(content.trim());
            
            // Gestisci summary automatico se vuoto
            if (summary == null || summary.trim().isEmpty()) {
                String autoSummary = content.length() > 200 ? 
                    content.substring(0, 200) + "..." : content;
                newPost.setSummary(autoSummary);
            } else {
                newPost.setSummary(summary.trim());
            }
            
            newPost.setCreatedAt(LocalDateTime.now());
            newPost.setPublished(true);
            newPost.setViewCount(0);
            
            Post savedPost = postRepository.save(newPost);
            
            response.put("success", true);
            response.put("message", "Post creato con successo");
            response.put("post", savedPost);
            
            return ResponseEntity.ok(response);
                    
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Errore del server");
            return ResponseEntity.status(500).body(response);
        }
    }

    // DELETE post
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long id) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (!postRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            // Elimina prima i commenti associati
            commentRepository.deleteByPostId(id);
            
            // Poi elimina il post
            postRepository.deleteById(id);
            
            response.put("success", true);
            response.put("message", "Post eliminato con successo");
            
            return ResponseEntity.ok(response);
                    
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Errore nell'eliminazione");
            return ResponseEntity.status(500).body(response);
        }
    }

    // GET commenti di un post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        if (!postRepository.existsById(postId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return ResponseEntity.ok(comments);
    }
    
    // POST nuovo commento
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (!postRepository.existsById(postId)) {
                response.put("success", false);
                response.put("message", "Post non trovato");
                return ResponseEntity.status(404).body(response);
            }
            
            String author = request.get("author");
            String content = request.get("content");
            
            // Validazione
            if (author == null || author.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Nome autore richiesto");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Contenuto commento richiesto");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crea nuovo commento
            Comment newComment = new Comment();
            newComment.setAuthor(author.trim());
            newComment.setContent(content.trim());
            newComment.setPostId(postId);
            newComment.setCreatedAt(LocalDateTime.now());
            
            Comment savedComment = commentRepository.save(newComment);
            
            response.put("success", true);
            response.put("message", "Commento aggiunto con successo");
            response.put("comment", savedComment);
            
            return ResponseEntity.ok(response);
                    
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Errore nell'aggiunta del commento");
            return ResponseEntity.status(500).body(response);
        }
    }
}