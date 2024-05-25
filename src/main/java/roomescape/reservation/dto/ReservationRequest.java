package roomescape.reservation.dto;

import jakarta.validation.constraints.NotNull;

import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationBuilder;
import roomescape.reservation.domain.ReservationDetail;
import roomescape.reservation.domain.ReservationWaiting;
import roomescape.reservation.domain.ReservationWaitingBuilder;

public record ReservationRequest(
        @NotNull(message = "예약자 정보가 없습니다.")
        Long memberId,
        @NotNull(message = "테마 정보가 입력되지 않았습니다.")
        Long detailId
) {

    public Reservation toReservation(Member member, ReservationDetail detail) {
        return new ReservationBuilder()
                .member(member)
                .reservationDetail(detail)
                .build();
    }

    public ReservationWaiting toReservationWaiting(Member member, ReservationDetail detail) {
        return new ReservationWaitingBuilder()
                .member(member)
                .reservationDetail(detail)
                .build();
    }
}
