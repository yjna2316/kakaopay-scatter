package com.github.kakaopayserver.controller.scatter;

import com.github.kakaopayserver.model.scatter.Scatter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScatteringRequest {

  private Long money;

  private int recipientNum;

  public ScatteringRequest(Long money, int recipientNum) {
    this.money = money;
    this.recipientNum = recipientNum;
  }

  public Scatter newScatter(Long userId, String roomId, Long money, int recipientNum) {
    return new Scatter(userId, roomId, money, recipientNum);
  }

  public Long getMoney() {
    return money;
  }

  public int getRecipientNum() {
    return recipientNum;
  }

  public void setMoney(Long money) {
    this.money = money;
  }

  public void setRecipientNum(int recipientNum) {
    this.recipientNum = recipientNum;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("money", money)
      .append("recipientNum", recipientNum)
      .toString();
  }
}
