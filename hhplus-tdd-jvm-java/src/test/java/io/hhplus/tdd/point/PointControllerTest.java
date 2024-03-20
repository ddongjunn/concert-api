package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Point 컨트롤러 테스트")
@WebMvcTest(PointController.class)
public class PointControllerTest {

    private final MockMvc mvc;

    @MockBean
    private PointService pointService;

    public PointControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[GET] - 포인트 조회")
    @Test
    public void givenUserId_whenRequestingUserPoint_thenReturnsUserPoint() throws Exception {
        // Given
        Long userId = 1L;
        Long amount = 0L;
        given(pointService.checkPoint(userId)).willReturn(new UserPoint(userId, amount, 0L));

        // When
        mvc.perform(get("/point/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        then(pointService).should().checkPoint(userId);
    }

    @DisplayName("[PATCH] - 포인트 충전")
    @Test
    public void givenUserId_whenRequestingChargingPoint_thenReturnsUserPoint() throws Exception {
        // Given
        Long userId = 1L;
        Long amount = 500L;
        given(pointService.chargePoint(userId, amount)).willReturn(new UserPoint(userId, amount, System.currentTimeMillis()));

        // When
        mvc.perform(patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\" : 500}")
                    ).andExpect(status().isOk());

        // Then
        then(pointService).should().chargePoint(userId, 500L);
    }

    @Test
    @DisplayName("[GET] - 포인트 충전/이용 내역 조회")
    void 포인트_충전_이용_내역_조회() throws Exception {
        // Given
        Long userId = 1L;
        given(pointService.checkPointHistory(userId)).willReturn(Collections.emptyList());

        // When
        mvc.perform(get("/point/" + userId + "/histories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        then(pointService).should().checkPointHistory(userId);
    }

    @Test
    @DisplayName("[PATCH] - 특정 사용자 포인트 사용")
    void 특정_사용자_포인트_사용() throws Exception {
        // Given
        Long userId = 1L;
        Long amount = 100L;
        given(pointService.usePoint(userId, amount)).willReturn(new UserPoint(1L, 1000L, System.currentTimeMillis()));

        // When
        mvc.perform(patch("/point/" + userId + "/use")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\" : 100}")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        // Then
        then(pointService).should().usePoint(userId, amount);
    }

}
