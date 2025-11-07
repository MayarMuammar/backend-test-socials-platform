package com.mayar.social_platform.modules.post.service;

import com.mayar.social_platform.exception.BadRequestException;
import com.mayar.social_platform.exception.NotFoundException;
import com.mayar.social_platform.modules.post.dto.FeedPostResponse;
import com.mayar.social_platform.modules.post.entity.PostCommentDocument;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.post.mapper.FeedPostMapper;
import com.mayar.social_platform.modules.post.repository.IPostCommentRepository;
import com.mayar.social_platform.modules.post.repository.IPostLikeRepository;
import com.mayar.social_platform.modules.post.repository.IPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostInteractionService {

    private final IPostRepository postRepository;
    private final IPostLikeRepository postLikeRepository;
    private final IPostCommentRepository postCommentRepository;

    public PostInteractionService(IPostRepository postRepository,
                                  IPostLikeRepository postLikeRepository,
                                  IPostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
    }


    @Transactional
    public FeedPostResponse likePost(String postId, String userId) {
        PostDocument post = postRepository.findById(postId)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));

        if(postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BadRequestException("Post already liked by user");
        }

        postLikeRepository.saveLike(postId, userId);
        return postRepository.incrementPostLike(postId)
                .map(FeedPostMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));
    }

    @Transactional
    public FeedPostResponse unlikePost(String postId, String userId) {
        PostDocument post = postRepository.findById(postId)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));

        if(!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BadRequestException("Post already unliked by user");
        }

        postLikeRepository.removeLike(postId, userId);
        return postRepository.decrementPostLike(postId)
                .map(FeedPostMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));
    }

    @Transactional
    public FeedPostResponse commentPost(String postId, String userId, String comment) {
        PostDocument post = postRepository.findById(postId)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));

        postCommentRepository.createComment(postId, userId, comment);
        return postRepository.incrementPostComment(postId)
                .map(FeedPostMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("Post", postId));
    }

    @Transactional
    public FeedPostResponse deleteComment(String commentId, String userId) {

        Optional<PostCommentDocument> comment = postCommentRepository.getByIdAndUserId(commentId, userId);

        if(comment.isEmpty()) {
            throw NotFoundException.forResource("Comment", commentId);
        }

        PostCommentDocument postComment = comment.get();

        postCommentRepository.deleteComment(commentId, userId);
        return postRepository.incrementPostComment(postComment.getPostId())
                .map(FeedPostMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("Post", postComment.getPostId()));
    }
}
