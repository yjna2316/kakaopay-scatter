package com.github.kakaopayserver.controller.scatter;

import com.github.kakaopayserver.model.scatter.Scatter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

public class ScatterStatusDto {

  private LocalDateTime createAt;
  private Long totalCash;
  private Long pickedCash;
  private List<Receiver> receivers;

  public ScatterStatusDto(LocalDateTime createAt, Long totalCash, Long pickedCash, List<Receiver> receivers) {
    this.createAt = createAt;
    this.totalCash = totalCash;
    this.pickedCash = pickedCash;
    this.receivers = receivers;
  }


  public LocalDateTime getCreateAt() {
    return createAt;
  }

  public void setCreateAt(LocalDateTime createAt) {
    this.createAt = createAt;
  }

  public long getTotalCash() {
    return totalCash;
  }

  public void setTotalCash(long totalCash) {
    this.totalCash = totalCash;
  }

  public long getPickedCash() {
    return pickedCash;
  }

  public void setPickedCash(long pickedCash) {
    this.pickedCash = pickedCash;
  }

  public List<Receiver> getReceiverList() {
    return receivers;
  }

  public void setReceiverList(List<Receiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("createAt", createAt)
      .append("totalCash", totalCash)
      .append("pickedCash", pickedCash)
      .append("receivers", receivers)
      .toString();
  }
}