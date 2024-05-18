package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.ConflictException;
import roomescape.exception.BadRequestException;
import roomescape.member.dao.MemberRepository;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberProfileInfo;
import roomescape.reservation.dao.ReservationRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.dao.ThemeRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.dao.TimeRepository;
import roomescape.time.domain.Time;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private final Time time = new Time(1L, LocalTime.of(12, 0));
    private final Theme theme = new Theme(1L, "그켬미", "켬미 방탈출", "thumbnail");
    private final Member member = new Member("켬미", "kyummi@email.com", "pass");
    private final Reservation reservation = new Reservation(1L, member, LocalDate.MAX, time, theme);

    private final MemberProfileInfo memberProfileInfo = new MemberProfileInfo(1L, "Dobby", "kimdobby@wotaeco.com");
    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TimeRepository timeRepository;

    @Test
    @DisplayName("예약을 추가한다.")
    void addReservation() {
        Mockito.when(reservationRepository.save(any()))
                .thenReturn(reservation);

        Mockito.when(timeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTime()));

        Mockito.when(themeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTheme()));

        ReservationRequest reservationRequest = new ReservationRequest(reservation.getDate(),
                reservation.getMember(), reservation.getTime()
                .getId(), reservation.getTheme()
                .getId());
        ReservationResponse reservationResponse = reservationService.addReservation(reservationRequest);

        assertThat(reservationResponse.id()).isEqualTo(1);
    }

    @Test
    @DisplayName("과거의 날짜를 예약하려고 시도하는 경우 에외를 던진다.")
    void validation_ShouldThrowException_WhenReservationDateIsPast() {

        Mockito.when(timeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTime()));

        Mockito.when(themeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTheme()));

        ReservationRequest reservationRequest = new ReservationRequest(LocalDate.MIN, reservation.getMember(),
                reservation.getTimeId(), reservation.getThemeId());

        assertThatThrownBy(() -> reservationService.addReservation(reservationRequest)).isInstanceOf(
                BadRequestException.class);

    }

    @Test
    @DisplayName("예약을 찾는다.")
    void findReservations() {
        Mockito.when(reservationRepository.findAllByOrderByDateAsc())
                .thenReturn(List.of(reservation));

        List<ReservationResponse> reservationResponses = reservationService.findReservations();

        assertThat(reservationResponses).hasSize(1);
    }

    @Test
    @DisplayName("예약을 지운다.")
    void removeReservations() {
        Mockito.doNothing()
                .when(reservationRepository)
                .deleteById(reservation.getId());

        assertThatCode(() -> reservationService.removeReservations(reservation.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 테마의 예약이 존재하는 시간에 예약을 요청할 때 예외를 던진다.")
    void addReservation_ShouldThrowException_WhenDuplicatedReservationRequestOccurs() {
        Mockito.when(reservationRepository.findAllByTheme_IdAndDate(any(Long.class), any(LocalDate.class)))
                .thenReturn(List.of(reservation));
        Mockito.when(timeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTime()));
        Mockito.when(themeRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(reservation.getTheme()));

        ReservationRequest reservationRequest = new ReservationRequest(reservation.getDate(),
                reservation.getMember(), reservation.getTimeId(), reservation.getThemeId());

        assertThatThrownBy(() -> reservationService.addReservation(reservationRequest)).isInstanceOf(
                ConflictException.class);
    }

}
