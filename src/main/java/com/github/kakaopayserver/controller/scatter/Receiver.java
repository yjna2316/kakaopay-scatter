package com.github.kakaopayserver.controller.scatter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Receiver {

  private long receivedAmount;
  private long receiverId;

  public Receiver(long receivedAmount, long receiverId) {
    this.receivedAmount = receivedAmount;
    this.receiverId = receiverId;
  }

  public long getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(long receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(long receiverId) {
    this.receiverId = receiverId;
  }


  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("amount", receivedAmount)
      .toString();
  }
}
