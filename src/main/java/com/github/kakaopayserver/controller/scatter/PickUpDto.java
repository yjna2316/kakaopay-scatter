package com.github.kakaopayserver.controller.scatter;

import com.github.kakaopayserver.model.scatter.PickUp;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static org.springframework.beans.BeanUtils.copyProperties;

public class PickUpDto {
  private Long amount;

  public PickUpDto(PickUp pickUp) {
    this.amount = pickUp.getCash();
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("amount", amount)
      .toString();
  }
}
