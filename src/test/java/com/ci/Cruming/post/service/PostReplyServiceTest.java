package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.dto.PostReplyResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
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
            // given
            String longContent = "한글🎉EN".repeat(500);
            PostReplyRequest invalidRequest = new PostReplyRequest(longContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            doThrow(new CrumingException(ErrorCode.INVALID_REPLY_SIZE))
                    .when(postReplyValidator).validatePostReplyRequest(invalidRequest);

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, invalidRequest, post.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REPLY_SIZE);
        }

        @Test
        @DisplayName("댓글 작성 - 내용 길이 정상")
        void createPostReply_WithValidContent_Success() {
            // given
            String validContent = "한글🎉EN".repeat(300);
            PostReplyRequest validContentRequest = new PostReplyRequest(validContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());
            doNothing().when(postReplyValidator).validatePostReplyRequest(any());

            // when
            postReplyService.createPostReply(user, validContentRequest, post.getId(), null);

            // then
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
            // given
            PostReply reply = PostReply.builder()
                    .id(1L)
                    .user(user)
                    .build();
            given(postReplyRepository.findById(reply.getId())).willReturn(Optional.of(reply));

            String longContent = "한글🎉EN".repeat(500);
            PostReplyRequest invalidRequest = new PostReplyRequest(longContent);

            doThrow(new CrumingException(ErrorCode.INVALID_REPLY_SIZE))
                    .when(postReplyValidator).validatePostReplyRequest(invalidRequest);

            // when & then
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

        @Test
        @DisplayName("댓글 삭제 - 대댓글도 삭제 성공")
        void deletePostReply_WithChildren_Success() {
            // given
            PostReply childReply1 = PostReply.builder()
                    .id(2L)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(childReply1, "deletedAt", null);

            PostReply childReply2 = PostReply.builder()
                    .id(3L)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(childReply2, "deletedAt", null);

            PostReply parentReply = PostReply.builder()
                    .id(1L)
                    .user(user)
                    .children(List.of(childReply1, childReply2))
                    .build();
            ReflectionTestUtils.setField(parentReply, "deletedAt", null);

            given(postReplyRepository.findById(parentReply.getId())).willReturn(Optional.of(parentReply));
            doAnswer(invocation -> {
                PostReply reply = invocation.getArgument(0);
                ReflectionTestUtils.setField(reply, "deletedAt", LocalDateTime.now());
                reply.getChildren().forEach(child ->
                        ReflectionTestUtils.setField(child, "deletedAt", LocalDateTime.now())
                );
                return null;
            }).when(postReplyRepository).delete(parentReply);

            // when
            postReplyService.deletePostReply(user, parentReply.getId());

            // then
            verify(postReplyRepository).delete(parentReply);
            assertThat(parentReply.getDeletedAt()).isNotNull();
            assertThat(parentReply.getChildren())
                    .allMatch(child -> child.getDeletedAt() != null);
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회")
    class FindPostReplyList {
        @Test
        @DisplayName("댓글 및 대댓글 목록 조회 - 성공")
        void findPostReplyList_Success() {
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<PostReply> parentReplies = List.of(
                    createPostReply(1L, "parent1"),
                    createPostReply(2L, "parent2")
            );
            List<PostReply> childReplies = List.of(
                    createPostReply(3L, "child1"),
                    createPostReply(4L, "child2")
            );
            Page<PostReply> parentPage = new PageImpl<>(parentReplies, pageRequest, 2);
            Page<PostReply> childPage = new PageImpl<>(childReplies, PageRequest.of(0, 5), 2);

            given(postReplyRepository.findByPostIdAndParentIsNull(1L, pageRequest)).willReturn(parentPage);
            given(postReplyRepository.findByParentId(anyLong(), any())).willReturn(childPage);
            given(postReplyMapper.toChildPostReplyResponse(any())).willAnswer(i -> {
                PostReply reply = i.getArgument(0);
                return new PostReplyResponse(
                        reply.getId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        null,
                        "nickname",
                        List.of()
                );
            });
            given(postReplyMapper.toParentPostReplyResponse(any(), any())).willAnswer(i -> {
                PostReply reply = i.getArgument(0);
                List<PostReplyResponse> children = i.getArgument(1);
                return new PostReplyResponse(
                        reply.getId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        null,
                        "nickname",
                        children
                );
            });

            Page<PostReplyResponse> result = postReplyService.findPostReplyList(pageRequest, 1L);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).children()).hasSize(2);
            verify(postReplyRepository).findByPostIdAndParentIsNull(1L, pageRequest);
            verify(postReplyRepository, times(2)).findByParentId(anyLong(), any());
            verify(postReplyMapper, times(4)).toChildPostReplyResponse(any());
            verify(postReplyMapper, times(2)).toParentPostReplyResponse(any(), any());
        }

        @Test
        @DisplayName("대댓글만 목록 조회 - 성공")
        void findChildReplyList_Success() {
            PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<PostReply> childReplies = List.of(
                    createPostReply(1L, "child1"),
                    createPostReply(2L, "child2")
            );
            Page<PostReply> childPage = new PageImpl<>(childReplies, pageRequest, 2);

            given(postReplyRepository.findByParentId(1L, pageRequest)).willReturn(childPage);
            given(postReplyMapper.toChildPostReplyResponse(any())).willAnswer(i -> {
                PostReply reply = i.getArgument(0);
                return new PostReplyResponse(
                        reply.getId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        null,
                        "nickname",
                        List.of()
                );
            });

            Page<PostReplyResponse> result = postReplyService.findPostChildReplyList(1L, pageRequest);

            assertThat(result.getContent()).hasSize(2);
            verify(postReplyRepository).findByParentId(1L, pageRequest);
            verify(postReplyMapper, times(2)).toChildPostReplyResponse(any());
        }

        private PostReply createPostReply(Long id, String content) {
            PostReply reply = PostReply.builder()
                    .content(content)
                    .user(User.builder().nickname("nickname").build())
                    .build();
            ReflectionTestUtils.setField(reply, "id", id);
            ReflectionTestUtils.setField(reply, "createdAt", LocalDateTime.now());
            return reply;
        }
    }
}