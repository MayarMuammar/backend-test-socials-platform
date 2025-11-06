package com.mayar.social_platform.modules.post.service;


import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.exception.NotFoundException;
import com.mayar.social_platform.modules.post.dto.CreatePostRequest;
import com.mayar.social_platform.modules.post.dto.FeedPostResponse;
import com.mayar.social_platform.modules.post.dto.PostResponse;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.post.mapper.FeedPostMapper;
import com.mayar.social_platform.modules.post.mapper.PostMapper;
import com.mayar.social_platform.modules.post.repository.IPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final IPostRepository postRepository;

    public PostService(IPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public FeedPostResponse createPost(CreatePostRequest request, String userId) {
        PostDocument postDocument =  PostDocument.builder()
                .content(request.getContent())
                .mediaUrls(request.getMediaUrls())
                .build();
        PostDocument created = postRepository.create(postDocument, userId);
        return FeedPostMapper.toResponse(created);
    }

    public Optional<FeedPostResponse> getPostForFeed(String postId) {
        return postRepository.findById(postId)
                .map(FeedPostMapper::toResponse)
                .or(() -> {throw new NotFoundException("Post Not Found");});
    }

    public PostResponse getPostById(String postId) {
        return postRepository.findById(postId)
                .map(PostMapper::toResponse)
                .orElseThrow(() ->  NotFoundException.forResource("Post", postId));
    }

    public PostResponse approvePost(String postId, String adminUsername) {
        return postRepository.approve(postId, adminUsername)
                .map(PostMapper::toResponse)
                .orElseThrow(() ->   NotFoundException.forResource("Post", postId));

    }

    public PostResponse rejectPost(String postId, String rejectionReason, String adminUsername) {
        return postRepository.reject(postId,rejectionReason, adminUsername)
                .map(PostMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));

    }

    public PageList<FeedPostResponse> getFeed(PageQuery pageQuery) {
        PageList<PostDocument> postsPage = postRepository.getPosts(pageQuery);

        List<FeedPostResponse> responses = FeedPostMapper.toResponse(postsPage.getItems());

        return PageList.of(responses, postsPage.getTotalItems(), postsPage.getCurrentPage(), postsPage.getLimit());
    }

    public PageList<PostResponse> getPosts(PageQuery pageQuery) {
        PageList<PostDocument> postsPage = postRepository.getPosts(pageQuery);

        List<PostResponse> responses = postsPage.getItems().stream()
                .map(PostMapper::toResponse)
                .toList();

        return PageList.of(responses, postsPage.getTotalItems(), postsPage.getCurrentPage(), postsPage.getLimit());
    }
}
