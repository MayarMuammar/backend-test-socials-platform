package com.mayar.social_platform.modules.post.controller;

import com.mayar.social_platform.common.annotation.CurrentUser;
import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.common.pipe.QueryPageTransformPipe;
import com.mayar.social_platform.common.security.UserPrincipal;
import com.mayar.social_platform.modules.post.dto.CreatePostRequest;
import com.mayar.social_platform.modules.post.dto.FeedPostResponse;
import com.mayar.social_platform.modules.post.dto.PostResponse;
import com.mayar.social_platform.modules.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class UserPostController {

    private final PostService postService;
    private final QueryPageTransformPipe queryPageTransformPipe;

    public UserPostController(PostService postService,  QueryPageTransformPipe queryPageTransformPipe) {
        this.postService = postService;
        this.queryPageTransformPipe = queryPageTransformPipe;
    }

    @GetMapping("feed")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PageList<FeedPostResponse>> getFeed(@RequestParam Map<String, String> params) {
        PageQuery pageQuery = queryPageTransformPipe.transform(params);
        return ResponseEntity.ok(postService.getFeed(pageQuery));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FeedPostResponse> createPost(@RequestBody CreatePostRequest request, @CurrentUser UserPrincipal user) {
        return ResponseEntity.ok(postService.createPost(request, user.getId()));
    }

    @GetMapping("my-posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageList<PostResponse>> getMyPosts(@RequestParam Map<String, String> params, @CurrentUser UserPrincipal user) {
        PageQuery pageQuery = queryPageTransformPipe.transform(params);
        return ResponseEntity.ok(postService.getMyPosts(pageQuery, user.getId()));
    }
}
