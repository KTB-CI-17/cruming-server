package com.ci.Cruming.timeline.controller;

import com.ci.Cruming.auth.service.JwtTokenProvider;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.timeline.dto.TimelineRequest;
import com.ci.Cruming.timeline.dto.TimelineResponse;
import com.ci.Cruming.timeline.service.TimelineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        TimelineResponse mockResponse = new TimelineResponse(
            1L, null, null, "중급", "테스트 타임라인",
            Visibility.PUBLIC, LocalDateTime.now(), 0, 0,
            false, null, LocalDateTime.now()
        );

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
        int page = 0;
        int limit = 10;
        List<TimelineResponse> responses = Arrays.asList(/* mock timeline responses */);
        
        when(timelineService.getUserTimelines(any(com.ci.Cruming.user.entity.User.class), eq(userId), eq(page), eq(limit)))
            .thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/v1/timelines/users/{userId}", userId)
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit))
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserTimelinesByDate_ShouldReturnPaginatedTimelines() throws Exception {
        // Given
        Long userId = 1L;
        String date = "2024-03-20";
        int page = 0;
        int limit = 10;
        List<TimelineResponse> responses = Arrays.asList(/* mock timeline responses */);
        
        when(timelineService.getUserTimelinesByDate(
            any(com.ci.Cruming.user.entity.User.class), 
            eq(userId), 
            eq(LocalDate.parse(date)),
            eq(page), 
            eq(limit)
        )).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/v1/timelines/users/{userId}/date/{date}", userId, date)
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit))
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }
} 