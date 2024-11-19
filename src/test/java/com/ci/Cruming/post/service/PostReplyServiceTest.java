package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.dto.mapper.PostReplyMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.post.repository.PostReplyRepository;
import com.ci.Cruming.post.repository.PostRepository;
import com.ci.Cruming.post.service.validator.PostReplyValidator;
import com.ci.Cruming.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostReplyServiceTest {

    @InjectMocks
    private PostReplyService postReplyService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostReplyRepository postReplyRepository;
    @Mock
    private PostReplyValidator postReplyValidator;
    @Mock
    private PostReplyMapper postReplyMapper;

    private User user;
    private Post post;
    private Post anotherPost;
    private PostReply parentReply;
    private PostReplyRequest validRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .build();

        anotherPost = Post.builder()
                .id(2L)
                .build();

        parentReply = PostReply.builder()
                .id(1L)
                .post(post)
                .build();

        validRequest = new PostReplyRequest("Valid comment content");
    }

    @Nested
    @DisplayName("댓글 생성")
    class CreatePostReply {
        @Test
        @DisplayName("댓글 작성 - 성공")
        void createPostReply_Success() {
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());

            postReplyService.createPostReply(user, validRequest, post.getId(), null);

            verify(postReplyRepository).save(any(PostReply.class));
        }

        @Test
        @DisplayName("대댓글 작성 - 성공")
        void createChildPostReply_Success() {
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(parentReply.getId())).willReturn(Optional.of(parentReply));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());

            postReplyService.createPostReply(user, validRequest, post.getId(), parentReply.getId());

            verify(postReplyRepository).save(any(PostReply.class));
        }

        @Test
        @DisplayName("댓글 작성 - 게시글 없음 실패")
        void createPostReply_PostNotFound() {
            given(postRepository.findById(post.getId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("댓글 작성 - 게시글 삭제됨 실패")
        void createPostReply_PostDeleted() {
            Post deletedPost = Post.builder()
                    .id(1L)
                    .deletedAt(LocalDateTime.now())
                    .build();
            given(postRepository.findById(deletedPost.getId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, deletedPost.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("대댓글 작성 - 부모 댓글 없음 실패")
        void createPostReply_ParentReplyNotFound() {
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(parentReply.getId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), parentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }

        @Test
        @DisplayName("대댓글 작성 - 부모 댓글 삭제됨 실패")
        void createPostReply_ParentReplyDeleted() {
            PostReply deletedParentReply = PostReply.builder()
                    .id(1L)
                    .post(post)
                    .deletedAt(LocalDateTime.now())
                    .build();
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(deletedParentReply.getId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), deletedParentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }

        @Test
        @DisplayName("대댓글 작성 - 부모 댓글이 다른 게시글의 댓글인 경우 실패")
        void createPostReply_InvalidParentReplyPost() {
            PostReply invalidParentReply = PostReply.builder()
                    .id(1L)
                    .post(anotherPost)
                    .build();

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(invalidParentReply.getId())).willReturn(Optional.of(invalidParentReply));

            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), invalidParentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REPLY_AND_POST);

            verify(postRepository).findById(post.getId());
            verify(postReplyRepository).findById(invalidParentReply.getId());
            verifyNoMoreInteractions(postRepository, postReplyRepository);
        }

        @Test
        @DisplayName("댓글 작성 - 내용 길이 초과 실패")
        void createPostReply_ContentTooLong() {
            String longContent = "안녕Hello🎉!".repeat(100);
            PostReplyRequest invalidRequest = new PostReplyRequest(longContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            doThrow(new CrumingException(ErrorCode.INVALID_REPLY_SIZE))
                    .when(postReplyValidator).validatePostReplyRequest(invalidRequest);

            assertThatThrownBy(() -> postReplyService.createPostReply(user, invalidRequest, post.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REPLY_SIZE);
        }

        @Test
        @DisplayName("댓글 작성 - 내용 길이 정상")
        void createPostReply_WithValidContent_Success() {
            String validContent = "안녕Hello🎉!".repeat(90);
            PostReplyRequest validContentRequest = new PostReplyRequest(validContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());
            doNothing().when(postReplyValidator).validatePostReplyRequest(any());

            postReplyService.createPostReply(user, validContentRequest, post.getId(), null);

            verify(postReplyRepository).save(any(PostReply.class));
            verify(postReplyValidator).validatePostReplyRequest(validContentRequest);
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdatePostReply {
        @Test
        @DisplayName("댓글 수정 - 성공")
        void updatePostReply_Success() {
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(user)
                    .content("Original content")
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));

            postReplyService.updatePostReply(user, validRequest, reply.getId());

            verify(postReplyValidator).validatePostReplyRequest(validRequest);
            assertThat(reply.getContent()).isEqualTo(validRequest.content());
        }

        @Test
        @DisplayName("댓글 수정 - 댓글 없음 실패")
        void updatePostReply_ReplyNotFound() {
            given(postReplyRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postReplyService.updatePostReply(user, validRequest, 1L))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }

        @Test
        @DisplayName("댓글 수정 - 권한 없음 실패")
        void updatePostReply_Unauthorized() {
            // given
            User anotherUser = User.builder().id(2L).build();
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(anotherUser)
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));
            doThrow(new CrumingException(ErrorCode.POST_REPLY_NOT_AUTHORIZED))
                    .when(postReplyValidator).validatePostReplyAuthor(reply, user);

            // when & then
            assertThatThrownBy(() -> postReplyService.updatePostReply(user, validRequest, reply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_REPLY_NOT_AUTHORIZED);
        }

        @Test
        @DisplayName("댓글 수정 - 내용 길이 초과 실패")
        void updatePostReply_ContentTooLong() {
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(user)
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));

            String longContent = "안녕Hello🎉!".repeat(100);
            PostReplyRequest invalidRequest = new PostReplyRequest(longContent);

            doThrow(new CrumingException(ErrorCode.INVALID_REPLY_SIZE))
                    .when(postReplyValidator).validatePostReplyRequest(invalidRequest);

            assertThatThrownBy(() -> postReplyService.updatePostReply(user, invalidRequest, reply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REPLY_SIZE);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeletePostReply {
        @Test
        @DisplayName("댓글 삭제 - 성공")
        void deletePostReply_Success() {
            // given
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(user)
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));
            doNothing().when(postReplyRepository).delete(reply);

            // when
            postReplyService.deletePostReply(user, reply.getId());

            // then
            verify(postReplyRepository).delete(reply);
        }

        @Test
        @DisplayName("댓글 삭제 - 댓글 없음 실패")
        void deletePostReply_ReplyNotFound() {
            // given
            given(postReplyRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postReplyService.deletePostReply(user, 1L))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }

        @Test
        @DisplayName("댓글 삭제 - 권한 없음 실패")
        void deletePostReply_Unauthorized() {
            // given
            User anotherUser = User.builder().id(2L).build();
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(anotherUser)
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));
            doThrow(new CrumingException(ErrorCode.POST_REPLY_NOT_AUTHORIZED))
                    .when(postReplyValidator).validatePostReplyAuthor(reply, user);

            // when & then
            assertThatThrownBy(() -> postReplyService.deletePostReply(user, reply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_REPLY_NOT_AUTHORIZED);
        }
    }
}