package com.ci.Cruming.timeline.controller;

import com.ci.Cruming.auth.service.JwtTokenProvider;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.timeline.dto.TimelineListResponse;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.timeline.dto.TimelineUserDTO;
import com.ci.Cruming.timeline.service.TimelineService;
import com.ci.Cruming.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TimelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TimelineService timelineService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private com.ci.Cruming.user.entity.User testUser;
    private TimelineRequest testRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = com.ci.Cruming.user.entity.User.builder()
            .id(1L)
            .nickname("테스트유저")
            .platform(Platform.NAVER)
            .platformId("12345")
            .build();

        testRequest = TimelineRequest.builder()
            .locationId(1L)
            .level("중급")
            .content("테스트 타임라인")
            .visibility(Visibility.PUBLIC)
            .activityAt(LocalDateTime.now())
            .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUser.getNickname())
            .password("")
            .authorities(Collections.emptyList())
            .build();

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getUserId(any())).thenReturn(1L);
    }

    @Test
    @WithMockUser
    void createTimeline_Success() throws Exception {
        // given
        TimelineResponse mockResponse = TimelineResponse.builder()
            .id(1L)
            .user(TimelineUserDTO.builder()
                .id(1L)
                .nickname("테스트유저")
                .profileImageUrl(null)
                .build())
            .location(null)
            .level("중급")
            .content("테스트 타임라인")
            .visibility(Visibility.PUBLIC)
            .activityAt(LocalDateTime.now())
            .likeCount(0)
            .replyCount(0)
            .isLiked(false)
            .createdAt(LocalDateTime.now())
            .build();

        when(timelineService.createTimeline(any(), any())).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/timelines")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTimeline_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/timelines/{timelineId}", 1L)
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void likeTimeline_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/timelines/{timelineId}/likes", 1L)
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    void getUserTimelines_ShouldReturnPaginatedTimelines() throws Exception {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<TimelineListResponse> timelineResponses = Arrays.asList(/* mock timeline responses */);
        Page<TimelineListResponse> pageResponse = new PageImpl<>(timelineResponses, pageable, 1);
        
        when(timelineService.getUserTimelines(any(User.class), eq(userId), any(Pageable.class)))
            .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/timelines/users/{userId}", userId)
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserTimelinesByDate_ShouldReturnPaginatedTimelines() throws Exception {
        // Given
        Long userId = 1L;
        String date = "2024-03-20";
        Pageable pageable = PageRequest.of(0, 10);
        List<TimelineListResponse> timelineResponses = Arrays.asList(/* mock timeline responses */);
        Page<TimelineListResponse> pageResponse = new PageImpl<>(timelineResponses, pageable, 1);
        
        when(timelineService.getUserTimelinesByDate(
            any(User.class), 
            eq(userId), 
            eq(LocalDate.parse(date)),
            any(Pageable.class)
        )).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/timelines/users/{userId}/date/{date}", userId, date)
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
} 