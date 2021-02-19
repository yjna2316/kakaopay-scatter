package com.github.kakaopayserver.model.scatter;

import com.github.kakaopayserver.controller.scatter.Receiver;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.length;
import static com.google.common.base.Preconditions.checkArgument;


public class Scatter {

  private final Long seq;

  private String token;
  
  private final Long userId;
  
  private final String roomId;

  private final Long cash;

  private final int recipientNum;

  private Long currentDistributed;

  private List<PickUp> pickUps;

  private List<Receiver> receivers;

  private final LocalDateTime createAt;

  public Scatter(Long userId, String roomId, Long cash, int recipientNum) {
    this(null, null, userId, roomId, cash, recipientNum, null, null, null, null);
  }

  public Scatter(Long seq, String token, Long userId, String roomId, Long cash, int recipientNum, Long currentDistributed, List<PickUp> pickUps, List<Receiver> receivers, LocalDateTime createAt) {
    // 주어진 객체의 레퍼런스가 null일 경우 익셉션 메시지를 담은  NullPointerException을 발생
    checkNotNull(userId, "userId must be provided.");
    checkNotNull(roomId, "roomId must be provided.");
    checkNotNull(cash, "cash must be provided.");
    checkNotNull(recipientNum, "recipientNum must be provided.");

    // 주어진 조건이 false일 경우 익셉션 메시지를 담은 IllegalArgumentException을 발생
    checkArgument(cash > 0L && recipientNum > 0, "the cash and the number of recipients must larger than the zero.");
    checkArgument(cash >= recipientNum, "the cash must larger than the number of recipients.");
    
    this.seq = seq;
    this.token = token;
    this.userId = userId;
    this.roomId = roomId;
    this.cash = cash;
    this.currentDistributed = defaultIfNull(currentDistributed, 0L);
    this.recipientNum = recipientNum;
    this.receivers = receivers;
    this.pickUps = pickUps;
    this.createAt = defaultIfNull(createAt, now());
  }

  public boolean isMadeByMe(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  public void updateToken(String token) {
    checkArgument(isNotEmpty(token), "token must be provided.");
    checkArgument(
      length(token) == 3, "token length must be 3."
    );

    this.token = token;
  }

  public Long getSeq() {
    return seq;
  }

  public String getToken() {
    return token;
  }

  public Long getUserId() {
    return userId;
  }

  public String getRoomId() {
    return roomId;
  }

  public Long getCash() {
    return cash;
  }

  public Long getCurrentDistributed() {
    return currentDistributed;
  }

  public int getRecipientNum() {
    return recipientNum;
  }

  public List<PickUp> getPickUps() {
    if (isEmpty(pickUps)) {
      pickUps = new ArrayList<>();
    }
    return pickUps;
  }

  public List<Receiver> getReceivers() { // Optional?
    if (isEmpty(receivers)) {
      receivers = new ArrayList<>();
    }
    return receivers;
  }

  public void increaseDistribute(Long pickedCash) {
    this.currentDistributed += pickedCash;
  }

  public boolean isExpired(int minutes) {
    return this.createAt.isBefore(LocalDateTime.now().minusMinutes(minutes));
  }

  public LocalDateTime getCreateAt() {
    return createAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Scatter scatter = (Scatter) o;
    return Objects.equals(seq, scatter.seq);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seq);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("seq", seq)
      .append("token", token)
      .append("userId", userId)
      .append("roomId", "roomId")
      .append("cash", cash)
      .append("recipientNum", recipientNum)
      .append("currentDistributed", currentDistributed)
      .append("pickUps", pickUps)
      .append("receivers", receivers)
      .append("createAt", createAt)
      .toString();
  }

  static public class Builder {
    private Long seq;
    private String token;
    private Long userId;
    private String roomId;
    private Long cash;
    private int recipientNum;
    private Long currentDistributed;
    private List<PickUp> pickUps;
    private List<Receiver> receivers;
    private LocalDateTime createAt;

    public Builder(Scatter scatter) {
      this.seq = scatter.seq;
      this.token = scatter.token;
      this.userId = scatter.userId;
      this.roomId = scatter.roomId;
      this.cash = scatter.cash;
      this.currentDistributed = scatter.currentDistributed;
      this.recipientNum = scatter.recipientNum;
      this.pickUps = scatter.pickUps;
      this.receivers = scatter.receivers;
      this.createAt = scatter.createAt;
    }

    public Builder() {
    }

    public Builder seq(Long seq) {
      this.seq = seq;
      return this;
    }

    public Builder token(String token) {
      this.token = token;
      return this;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder roomId(String roomId) {
      this.roomId = roomId;
      return this;
    }

    public Builder cash(long cash) {
      this.cash = cash;
      return this;
    }

    public Builder recipientNum(int recipientNum) {
      this.recipientNum = recipientNum;
      return this;
    }

    public Builder currentDistributed(Long currentDistributed) {
      this.currentDistributed = currentDistributed;
      return this;
    }

    public Builder pickUps(List<PickUp> pickUps) {
      this.pickUps = pickUps;
      return this;
    }

    public Builder receivers(List<Receiver> receivers) {
      this.receivers = receivers;
      return this;
    }

    public Builder createAt(LocalDateTime createAt) {
      this.createAt = createAt;
      return this;
    }

    public Scatter build() {
      return new Scatter(seq, token, userId, roomId, cash, recipientNum, currentDistributed, pickUps, receivers, createAt);
    }
  }
}
