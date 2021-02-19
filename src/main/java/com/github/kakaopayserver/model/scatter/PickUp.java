package com.github.kakaopayserver.model.scatter;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.h2.mvstore.DataUtils.checkArgument;

public class PickUp {

  private final Long seq;

  private final Long scatterId;

  private final Long cash;

  private Long userId;

  private LocalDateTime pickedAt;

  private final LocalDateTime createAt;

  public PickUp(Long scatterId, Long cash) {
    this(null, scatterId, cash, null, null, null);
  }

  public PickUp(Long seq, Long scatterId, Long cash, Long userId, LocalDateTime pickedAt, LocalDateTime createAt) {
    checkNotNull(scatterId, "scatterId must be provided.");
    checkNotNull(cash, "cash must be provided.");
    checkArgument(cash > 0L, "the cash must bigger than the zero.");

    this.seq = seq;
    this.scatterId = scatterId;
    this.cash = cash;
    this.userId = userId;
    this.pickedAt = pickedAt;
    this.createAt = defaultIfNull(createAt, now());
    ;
  }

  public boolean picked() {
    return Objects.nonNull(this.userId);
  }

  public boolean notPicked() {
    return Objects.isNull(this.userId);
  }

  public boolean isMyPick(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  public void updateUser(Long userId) {
    checkNotNull(userId, "userId must be provided.");

    this.userId = userId;
    this.pickedAt = now();
  }

  public Long getSeq() {
    return seq;
  }

  public Long getScatter_seq() {
    return scatterId;
  }

  public Long getCash() {
    return cash;
  }

  public Long getUser_seq() {
    return userId;
  }

  public LocalDateTime getPickedAt() {
    return pickedAt;
  }

  public LocalDateTime getCreateAt() {
    return createAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PickUp pickUp = (PickUp) o;
    return Objects.equals(seq, pickUp.seq);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seq);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("seq", seq)
      .append("scatterId", scatterId)
      .append("cash", cash)
      .append("userId", userId)
      .append("pickedAt", pickedAt)
      .append("createAt", createAt)
      .toString();
  }
  
  static public class Builder {
    private Long seq;
    private Long scatterId;
    private Long cash;
    private Long userId;
    private LocalDateTime pickedAt;
    private LocalDateTime createAt;

    public Builder(PickUp pickUp) {
      this.seq = pickUp.seq;
      this.scatterId = pickUp.scatterId;
      this.cash = pickUp.cash;
      this.userId = pickUp.userId;
      this.pickedAt = pickUp.pickedAt;
      this.createAt = pickUp.createAt;
    }

    public Builder() {
    }

    public Builder seq(Long seq) {
      this.seq = seq;
      return this;
    }

    public Builder scatterId(Long scatterId) {
      this.scatterId = scatterId;
      return this;
    }

    public Builder cash(long cash) {
      this.cash = cash;
      return this;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder pickedAt(LocalDateTime pickedAt) {
      this.pickedAt = pickedAt;
      return this;
    }

    public Builder createAt(LocalDateTime createAt) {
      this.createAt = createAt;
      return this;
    }

    public PickUp build() {
      return new PickUp(seq, scatterId, cash, userId, pickedAt, createAt);
    }
  }
}