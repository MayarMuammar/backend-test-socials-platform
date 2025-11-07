package com.mayar.social_platform.modules.post.controller;

import com.mayar.social_platform.common.annotation.CurrentUser;
import com.mayar.social_platform.common.security.UserPrincipal;
import com.mayar.social_platform.modules.post.dto.CreatePostComment;
import com.mayar.social_platform.modules.post.dto.FeedPostResponse;
import com.mayar.social_platform.modules.post.service.PostInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
@PreAuthorize("hasRole('USER')")
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    public PostInteractionController(PostInteractionService postInteractionService) {
        this.postInteractionService = postInteractionService;
    }


    @PutMapping("/like/posts/{postId}")
    public ResponseEntity<FeedPostResponse> likePost(@PathVariable String postId, @CurrentUser UserPrincipal user) {
        return  ResponseEntity.ok(postInteractionService.likePost(postId, user.getId()));
    }

    @PutMapping("/unlike/posts/{postId}")
    public ResponseEntity<FeedPostResponse> unLikePost(@PathVariable String postId, @CurrentUser UserPrincipal user) {
        return  ResponseEntity.ok(postInteractionService.unlikePost(postId, user.getId()));
    }

    @PutMapping("/comments/posts/{postId}")
    public ResponseEntity<FeedPostResponse> commentPost(@PathVariable String postId, @CurrentUser UserPrincipal user, @RequestBody CreatePostComment request) {
        return ResponseEntity.ok(postInteractionService.commentPost(postId, user.getId(), request.getComment()));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<FeedPostResponse> deleteComment(@PathVariable String commentId, @CurrentUser UserPrincipal user) {
        return ResponseEntity.ok(postInteractionService.deleteComment(commentId, user.getId()));
    }


}
