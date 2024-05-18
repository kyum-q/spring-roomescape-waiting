package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import roomescape.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

@Entity
public class Reservation {
    private final String status = "예약";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    private LocalDate date;
    @ManyToOne
    private Time time;
    @ManyToOne
    private Theme theme;

    public Reservation() {
    }

    public Reservation(Member member, LocalDate date, Time time, Theme theme) {
        this(null, member, date, time, theme);
    }

    public Reservation(Long id, Member member, LocalDate date, Time time, Theme theme) {
        validate(member, date, time, theme);
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validate(Member member, LocalDate date, Time time, Theme theme) {
        if (member == null || date == null || time == null || theme == null) {
            throw new BadRequestException("예약 정보가 부족합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public String getMemberName() {
        return member.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Long getTimeId() {
        return time.getId();
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Long getThemeId() {
        return theme.getId();
    }

    public String getStatus() {
        return status;
    }

    public boolean isReservedAtPeriod(LocalDate start, LocalDate end) {
        return date.isAfter(start) && date.isBefore(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation that)) {
            return false;
        }
        if (id == null || that.id == null) {
            return Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(theme,
                    that.theme);
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(date, time, theme);
        }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" + "date=" + date + ", id=" + id + ", member=" + member + ", time=" + time + ", theme="
               + theme + '}';
    }
}
