package com.ci.Cruming.timeline.service;

import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.timeline.entity.Timeline;
import com.ci.Cruming.timeline.repository.TimelineLikeRepository;
import com.ci.Cruming.timeline.repository.TimelineReplyRepository;
import com.ci.Cruming.timeline.repository.TimelineRepository;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.location.repository.LocationRepository;
import com.ci.Cruming.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

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

    @InjectMocks
    private TimelineService timelineService;

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

    @Test
    void createTimeline_Success() {
        // given
        when(locationRepository.findById(any())).thenReturn(Optional.of(testLocation));
        when(timelineRepository.save(any())).thenAnswer(invocation -> {
            Timeline timeline = invocation.getArgument(0);
            return Timeline.builder()
                .id(1L)
                .user(timeline.getUser())
                .location(testLocation)
                .level(timeline.getLevel())
                .content(timeline.getContent())
                .visibility(timeline.getVisibility())
                .activityAt(timeline.getActivityAt())
                .build();
        });
        when(timelineLikeRepository.existsByTimelineAndUser(any(), any())).thenReturn(false);

        // when
        TimelineResponse response = timelineService.createTimeline(testUser, testRequest);

        // then
        assertNotNull(response);
        assertNotNull(response.content());
        assertNotNull(response.level());
        assertNotNull(response.visibility());
    }

    @Test
    void createTimeline_LocationNotFound() {
        // given
        when(locationRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(jakarta.persistence.EntityNotFoundException.class, 
            () -> timelineService.createTimeline(testUser, testRequest));
    }

    @Test
    void deleteTimeline_Success() {
        // given
        when(timelineRepository.findByIdAndDeletedAtIsNull(any()))
            .thenReturn(Optional.of(testTimeline));

        // when & then
        timelineService.deleteTimeline(testUser, 1L);
        assertNotNull(testTimeline.isDeleted());
    }

    @Test
    void likeTimeline_Success() {
        // given
        when(timelineRepository.findByIdAndDeletedAtIsNull(any()))
            .thenReturn(Optional.of(testTimeline));
        when(timelineLikeRepository.existsByTimelineAndUser(any(), any()))
            .thenReturn(false);

        // when & then
        timelineService.likeTimeline(testUser, 1L);
    }

    @Test
    void getUserTimelines_ShouldReturnPaginatedTimelines() {
        // Given
        User user = mock(User.class);
        User targetUser = mock(User.class);
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        List<Timeline> timelines = Arrays.asList(/* mock timelines */);
        Page<Timeline> timelinePage = new PageImpl<>(timelines);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(targetUser));
        when(timelineRepository.findByUserOrderByCreatedAtDesc(eq(targetUser), eq(pageable)))
            .thenReturn(timelinePage);

        // When
        List<TimelineResponse> result = timelineService.getUserTimelines(user, 1L, page, limit);

        // Then
        assertNotNull(result);
        verify(timelineRepository).findByUserOrderByCreatedAtDesc(eq(targetUser), eq(pageable));
    }

    @Test
    void getUserTimelinesByDate_ShouldReturnPaginatedTimelines() {
        // Given
        User user = mock(User.class);
        User targetUser = mock(User.class);
        LocalDate date = LocalDate.now();
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        List<Timeline> timelines = Arrays.asList(/* mock timelines */);
        Page<Timeline> timelinePage = new PageImpl<>(timelines);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(targetUser));
        when(timelineRepository.findByUserAndActivityAtBetweenOrderByActivityAtDesc(
            eq(targetUser), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            eq(pageable)
        )).thenReturn(timelinePage);

        // When
        List<TimelineResponse> result = timelineService.getUserTimelinesByDate(user, 1L, date, page, limit);

        // Then
        assertNotNull(result);
        verify(timelineRepository).findByUserAndActivityAtBetweenOrderByActivityAtDesc(
            eq(targetUser), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            eq(pageable)
        );
    }
} 