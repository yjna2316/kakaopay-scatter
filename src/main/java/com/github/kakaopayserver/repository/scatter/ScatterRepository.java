package com.github.kakaopayserver.repository.scatter;

import com.github.kakaopayserver.model.scatter.Scatter;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScatterRepository {
  Scatter insert(Scatter scatter);

  Optional<Scatter> findByToken(String token);

  Optional<Scatter> findByTokenAndCreateAt(String token, LocalDateTime createAt);

}
