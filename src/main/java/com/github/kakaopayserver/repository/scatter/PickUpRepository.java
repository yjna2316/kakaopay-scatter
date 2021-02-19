package com.github.kakaopayserver.repository.scatter;

import com.github.kakaopayserver.model.scatter.PickUp;

import java.util.List;
import java.util.Optional;

public interface PickUpRepository {
  PickUp insert(PickUp pickUp);

  void update(PickUp pickUp);

  Optional<List<PickUp>> findByScatterId(Long scatterId);

  void batchInsert(List<Long> cashes, long scatterId);
}
