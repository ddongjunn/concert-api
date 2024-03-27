package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.error.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointUnitTest {

    @Test
    public void 포인트_충전(){
        // Given
        UserPoint userPoint = UserPoint.of(1L, 1000L);

        // When
        UserPoint chargeUserPoint = userPoint.charge(1000L);

        // Then
        assertThat(chargeUserPoint.point()).isEqualTo(2000L);
    }

    @Test
    public void 포인트_음수로_충전하는_경우_예외(){
        // Given
        UserPoint userPoint = UserPoint.of(1L, 1000L);

        // Then
        assertThatThrownBy(() -> userPoint.charge(-2000L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorCode.INCORRECT_AMOUNT.getMessage());
    }

    @Test
    public void 포인트_사용() throws Exception {
        // Given
        UserPoint userPoint = UserPoint.of(1L, 1000L);

        // When
        UserPoint useUserPoint = userPoint.use(500L);

        // Then
        assertThat(useUserPoint.point()).isEqualTo(500L);
    }

    @Test
    public void 보유하고_있는_포인트보다_초과_사용하는_경우_예외() throws Exception {
        // Given
        UserPoint userPoint = UserPoint.of(1L, 1000L);

        // Then
        assertThatThrownBy(() -> userPoint.use(2000L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorCode.INCORRECT_AMOUNT.getMessage(userPoint.point()));
    }

}