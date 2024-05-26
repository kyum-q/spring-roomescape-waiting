package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import roomescape.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "detail_id"})
})
public class ReservationWaiting {
    private static final String status = "대기";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_id")
    private ReservationDetail detail;
    @Column(nullable = false, name = "create_at")
    private LocalDateTime createAt;


    public ReservationWaiting() {
    }

    public ReservationWaiting(Member member, ReservationDetail detail) {
        this(null, member, detail);
    }

    public ReservationWaiting(Long id, Member member, ReservationDetail detail) {
        validate(member, detail);
        this.id = id;
        this.member = member;
        this.detail = detail;
        this.createAt = LocalDateTime.now();
    }

    private void validate(Member member, ReservationDetail detail) {
        if (member == null || detail == null) {
            throw new BadRequestException("예약 정보가 부족합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getMemberId() {
        return member.getId();
    }

    public String getMemberName() {
        return member.getName();
    }

    public ReservationDetail getDetail() {
        return detail;
    }

    public LocalDate getDate() {
        return detail.getDate();
    }

    public Time getTime() {
        return detail.getTime();
    }

    public Long getTimeId() {
        return detail.getTimeId();
    }

    public Theme getTheme() {
        return detail.getTheme();
    }

    public Long getThemeId() {
        return detail.getThemeId();
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public String getStatus() {
        return status;
    }

    public boolean isReservedAtPeriod(LocalDate start, LocalDate end) {
        return detail.isReservedAtPeriod(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationWaiting that)) return false;

        if (id == null || that.id == null) {
            return Objects.equals(member, that.member) && Objects.equals(detail, that.detail);
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return Objects.hash(detail);
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReservationWaiting{" +
               "id=" + id +
               ", member=" + member +
               ", detail=" + detail +
               '}';
    }
}
