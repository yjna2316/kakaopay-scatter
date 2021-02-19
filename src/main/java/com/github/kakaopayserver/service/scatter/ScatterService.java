package com.github.kakaopayserver.service.scatter;

import com.github.kakaopayserver.controller.ErrorCode;
import com.github.kakaopayserver.error.BadRequestException;
import com.github.kakaopayserver.error.NotFoundException;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import com.github.kakaopayserver.repository.scatter.PickUpRepository;
import com.github.kakaopayserver.repository.scatter.ScatterRepository;
import com.github.kakaopayserver.util.StringTokenUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.apache.commons.lang3.StringUtils.*;
import static org.h2.mvstore.DataUtils.checkArgument;

@Service
public class ScatterService {

  private final ScatterRepository scatterRepository;

  private final PickUpRepository pickUpRepository;

  public ScatterService(ScatterRepository scatterRepository, PickUpRepository pickUpRepository) {
    this.scatterRepository = scatterRepository;
    this.pickUpRepository = pickUpRepository;
  }


  @Transactional
  public Optional<Scatter> status(Long userId, String token) {
    checkNotNull(userId, "userId must be provided.");
    checkNotNull(token, "token must be provided.");
    checkArgument(
      length(token) == 3, "token length must be 3"
    );

    return scatterRepository.findByTokenAndCreateAt(token, LocalDateTime.now().minusDays(7))
      .map(
        scatter -> {
          if (!scatter.isMadeByMe(userId)) {
            throw new BadRequestException(ErrorCode.MONEY_SCATTER_INVALID_USER);
          }

          if (scatter.isExpired(10)) {
            throw new BadRequestException(ErrorCode.MONEY_SCATTER_EXPIRED_TOKEN);
          }
          return  scatter;
        });
  }

  @Transactional
  public Optional<PickUp> pickup(Long userId, String roomId, String token) {
    checkNotNull(userId, "userId must be provided.");
    checkNotNull(roomId, "roomId must be provided.");
    checkNotNull(token, "token must be provided.");
    checkArgument(
      length(token) == 3, "token length must be 3"
    );
    return scatterRepository.findByToken(token)
      .map(scatter -> {
        Scatter available = checkScatter(scatter, userId, roomId, token);

        PickUp pick = available.getPickUps()
                                    .stream()
                                    .filter(PickUp::notPicked)
                                    .findFirst().get();
        pick.updateUser(userId);
        updatePick(pick);
        return pick;
      });
  }

  private Scatter checkScatter(Scatter scatter, Long userId, String roomId, String token) {
    // 요청자 체크
    if (scatter.isMadeByMe(userId)) {
      throw new BadRequestException(ErrorCode.MONEY_SCATTER_SAME_USER);
    }

    // 채팅방 체크
    if (!scatter.getRoomId().equals(roomId)) {
      throw new BadRequestException(ErrorCode.MONEY_SCATTER_SAME_ROOM);
    }

    // 토큰 만료시간 체크 (10분)
    if (scatter.isExpired(10)) {
      throw new BadRequestException(ErrorCode.MONEY_SCATTER_EXPIRED_TOKEN);
    }

    // 완료된 뿌린기
    if (scatter.getRecipientNum() == scatter.getReceivers().size()) {
      throw new BadRequestException(ErrorCode.MONEY_CATTER_FINISHED);
    }

    // 받은 이력 체크
    if (scatter.getReceivers().stream()
                .anyMatch(receiver -> receiver.getReceiverId() == userId))
    {
      throw new BadRequestException(ErrorCode.MONEY_SCATTER_ALREADY_RECEIVED);
    }

    return scatter;
  }

  private void updatePick(PickUp pickUp) {
    pickUpRepository.update(pickUp);
  }



  @Transactional
  public Scatter scatter(Scatter scatter) {
    String token = StringTokenUtils.generate(3);
    scatter.updateToken(token);

    Scatter newScatter = insert(scatter);

    List<Long> cashes = divideCash(newScatter.getCash(), newScatter.getRecipientNum());

    batchInsert(cashes, newScatter.getSeq());
    return newScatter;
  }


  private List<Long> divideCash(long cash, int recipientNum) {
    List<Long> cashes = new ArrayList<>(recipientNum);

    if (recipientNum == 1) {
      cashes.add(cash);
      return cashes;
    }

    for (int i = 0; i < recipientNum - 1; i++) {
      long part = RandomUtils.nextLong(1, Math.round(cash / recipientNum) + 1);
      cashes.add(part);
      cash -= part;
    }

    cashes.add(cash);
    return cashes;
  }

  private void batchInsert(List<Long> cashes, Long scatterId) {
    pickUpRepository.batchInsert(cashes, scatterId);
  }

  private Scatter insert(Scatter scatter) {
    return scatterRepository.insert(scatter);
  }
}
