package com.ci.Cruming.timeline.service;

import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.follow.service.FollowService;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.dto.TimelineListResponse;
import com.ci.Cruming.timeline.dto.TimelineReplyRequest;
import com.ci.Cruming.timeline.dto.TimelineReplyResponse;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.entity.TimelineLike;
import com.ci.Cruming.timeline.entity.TimelineReply;
import com.ci.Cruming.timeline.mapper.TimelineMapper;
import com.ci.Cruming.timeline.repository.TimelineLikeRepository;
import com.ci.Cruming.timeline.repository.TimelineReplyRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.timeline.validator.TimelineValidator;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.location.repository.LocationRepository;
import com.ci.Cruming.user.repository.UserRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

    @InjectMocks
    private TimelineService timelineService;

    @Mock
    private TimelineRepository timelineRepository;
    @Mock
    private TimelineLikeRepository timelineLikeRepository;
    @Mock
    private TimelineReplyRepository timelineReplyRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TimelineMapper timelineMapper;
    @Mock
    private TimelineValidator timelineValidator;
    @Mock
    private FollowService followService;

    private User testUser;
    private Location testLocation;
    private Timeline testTimeline;
    private TimelineRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId("12345")
            .build();

        testLocation = Location.builder()
            .id(1L)
            .placeName("테스트 클라이밍장")
            .address("서울시 강남구")
            .latitude(37.123456)
            .longitude(127.123456)
            .build();

        testRequest = TimelineRequest.builder()
            .locationId(1L)
            .level("중급")
            .content("테스트 타임라인")
            .visibility(Visibility.PUBLIC)
            .activityAt(LocalDateTime.now())
            .build();

        testTimeline = Timeline.builder()
            .id(1L)
            .user(testUser)
            .location(testLocation)
            .level(testRequest.getLevel())
            .content(testRequest.getContent())
            .visibility(testRequest.getVisibility())
            .activityAt(testRequest.getActivityAt())
            .build();
    }

    @Nested
    @DisplayName("타임라인 생성")
    class CreateTimeline {
        @Test
        @DisplayName("타임라인 생성 성공")
        void createTimeline_Success() {
            // given
            when(locationRepository.findById(testRequest.getLocationId()))
                .thenReturn(Optional.of(testLocation));
            when(timelineMapper.toEntity(testRequest, testUser, testLocation))
                .thenReturn(testTimeline);
            when(timelineRepository.save(any(Timeline.class)))
                .thenReturn(testTimeline);

            // when
            TimelineResponse response = timelineService.createTimeline(testUser, testRequest);

            // then
            assertNotNull(response);
            assertNotNull(response.content());
            assertNotNull(response.level());
            assertNotNull(response.visibility());
        }

        @Test
        @DisplayName("위치 정보 없음 - 실패")
        void createTimeline_LocationNotFound() {
            // given
            when(locationRepository.findById(testRequest.getLocationId()))
                .thenReturn(Optional.empty());

            // when & then
            assertThrows(CrumingException.class,
                () -> timelineService.createTimeline(testUser, testRequest));
        }
    }

    @Nested
    @DisplayName("타임라인 삭제")
    class DeleteTimeline {
        @Test
        @DisplayName("타임라인 삭제 성공")
        void deleteTimeline_Success() {
            // given
            when(timelineRepository.findByIdAndDeletedAtIsNull(testTimeline.getId()))
                .thenReturn(Optional.of(testTimeline));

            // when
            timelineService.deleteTimeline(testUser, testTimeline.getId());

            // then
            assertNotNull(testTimeline.getDeletedAt());
        }

        @Test
        @DisplayName("타임라인 없음 - 실패")
        void deleteTimeline_TimelineNotFound() {
            // given
            when(timelineRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.empty());

            // when & then
            assertThrows(CrumingException.class, 
                () -> timelineService.deleteTimeline(testUser, 1L));
        }
    }

    @Nested
    @DisplayName("타임라인 좋아요 토글")
    class ToggleTimelineLike {
        @Test
        @DisplayName("좋아요 추가 성공")
        void toggleTimelineLike_AddLike_Success() {
            // given
            when(timelineRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.of(testTimeline));
            when(timelineLikeRepository.findByTimelineAndUser(any(), any()))
                .thenReturn(Optional.empty());
            when(timelineLikeRepository.save(any()))
                .thenReturn(TimelineLike.builder()
                    .timeline(testTimeline)
                    .user(testUser)
                    .build());

            // when
            boolean result = timelineService.toggleTimelineLike(testUser, 1L);

            // then
            assertNotNull(result);
            assertNotNull(result);
        }

        @Test
        @DisplayName("좋아요 취소 성공")
        void toggleTimelineLike_RemoveLike_Success() {
            // given
            TimelineLike existingLike = TimelineLike.builder()
                .timeline(testTimeline)
                .user(testUser)
                .build();

            when(timelineRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.of(testTimeline));
            when(timelineLikeRepository.findByTimelineAndUser(any(), any()))
                .thenReturn(Optional.of(existingLike));

            // when
            boolean result = timelineService.toggleTimelineLike(testUser, 1L);

            // then
            assertNotNull(result);
            assertNotNull(result);
        }

        @Test
        @DisplayName("타임라인 없음 - 실패")
        void toggleTimelineLike_TimelineNotFound() {
            // given
            when(timelineRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.empty());

            // when & then
            assertThrows(CrumingException.class, 
                () -> timelineService.toggleTimelineLike(testUser, 1L));
        }
    }

    @Nested
    @DisplayName("타임라인 댓글 작성")
    class CreateReply {
        @Test
        @DisplayName("댓글 작성 성공")
        void createReply_Success() {
            // given
            TimelineReplyRequest request = TimelineReplyRequest.builder()
                .content("테스트 댓글")
                .build();

            TimelineReply reply = TimelineReply.builder()
                .timeline(testTimeline)
                .user(testUser)
                .content(request.getContent())
                .build();

            when(timelineRepository.findByIdAndDeletedAtIsNull(anyLong()))
                .thenReturn(Optional.of(testTimeline));
            when(timelineMapper.toEntity(any(), any(), any(), any()))
                .thenReturn(reply);
            when(timelineReplyRepository.save(any()))
                .thenReturn(reply);

            // when
            TimelineReplyResponse response = timelineService.createReply(testUser, 1L, request);

            // then
            assertNotNull(response);
            assertNotNull(response.content());
            verify(timelineValidator).validateReplyRequest(request);
        }

        // ... 실패 케이스 테스트 추가
    }

    @Test
    void getUserTimelines_ShouldReturnPaginatedTimelines() {
        // Given
        User user = mock(User.class);
        User targetUser = mock(User.class);
        Pageable pageable = PageRequest.of(0, 10);
        List<Timeline> timelines = Arrays.asList(testTimeline);
        Page<Timeline> timelinePage = new PageImpl<>(timelines, pageable, 1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(targetUser));
        when(timelineRepository.findByUserOrderByCreatedAtDesc(eq(targetUser), eq(pageable)))
            .thenReturn(timelinePage);

        // When
        Page<TimelineListResponse> result = timelineService.getUserTimelines(user, 1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(1L, result.getTotalElements());
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUserTimelinesByDate_ShouldReturnPaginatedTimelines() {
        // Given
        User user = mock(User.class);
        User targetUser = mock(User.class);
        LocalDate date = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<Timeline> timelines = Arrays.asList(testTimeline);
        Page<Timeline> timelinePage = new PageImpl<>(timelines, pageable, 1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(targetUser));
        when(timelineRepository.findByUserAndActivityAtBetweenOrderByActivityAtDesc(
            eq(targetUser), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            eq(pageable)
        )).thenReturn(timelinePage);

        // When
        Page<TimelineListResponse> result = timelineService.getUserTimelinesByDate(user, 1L, date, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(1L, result.getTotalElements());
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getMonthlyTimelines_Success() {
        // Given
        User user = mock(User.class);
        int year = 2024;
        int month = 3;
        Page<Timeline> timelinePage = new PageImpl<>(Arrays.asList(testTimeline));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(timelineRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            eq(user), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            eq(pageable)))
            .thenReturn(timelinePage);

        // When
        Page<TimelineListResponse> result = timelineService.getMonthlyTimelines(user, year, month, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
} 