package com.mayar.social_platform.modules.post.controller;

import com.mayar.social_platform.common.annotation.CurrentUser;
import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.common.pipe.QueryPageTransformPipe;
import com.mayar.social_platform.common.security.UserPrincipal;
import com.mayar.social_platform.modules.post.dto.PostResponse;
import com.mayar.social_platform.modules.post.dto.RejectPostRequest;
import com.mayar.social_platform.modules.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final PostService postService;
    private final QueryPageTransformPipe queryPageTransformPipe;
    public AdminPostController(PostService postService,  QueryPageTransformPipe queryPageTransformPipe) {
        this.postService = postService;
        this.queryPageTransformPipe = queryPageTransformPipe;
    }

    @GetMapping
    public ResponseEntity<PageList<PostResponse>> getPosts(@RequestParam Map<String, String> params) {
        PageQuery pageQuery = queryPageTransformPipe.transform(params);
        return ResponseEntity.ok(postService.getPosts(pageQuery));
    }

    @GetMapping("{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("{id}/approve")
    public ResponseEntity<PostResponse> approvePost(@PathVariable String id, @CurrentUser UserPrincipal user) {
        return ResponseEntity.ok(postService.approvePost(id, user.getId()));
    }

    @PutMapping("{id}/reject")
    public ResponseEntity<PostResponse> rejectPost(@PathVariable String id, @RequestBody RejectPostRequest request, @CurrentUser UserPrincipal user) {
        return ResponseEntity.ok(postService.rejectPost(id, request.getRejectionReason(), user.getId()));
    }


}
