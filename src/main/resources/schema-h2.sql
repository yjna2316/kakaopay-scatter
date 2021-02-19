DROP TABLE IF EXISTS scatters CASCADE; -- scatters에  FK로 연결된 table 같이 삭제

CREATE TABLE scatters (
  seq            bigint NOT NULL AUTO_INCREMENT,
  token          varchar(3) NOT NULL,
  room_seq       varchar(256) NOT NULL,
  user_seq       bigint NOT NULL,
  amount         bigint NOT NULL,
  receiver_count int NOT NULL,
  create_at      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (seq),
  CONSTRAINT unq_scatter_token UNIQUE (token)
);

create INDEX index_create_at on scatters(create_at);
create INDEX index_token on scatters(token);

CREATE TABLE pickups (
  seq             bigint NOT NULL AUTO_INCREMENT,
  scatter_seq     bigint NOT NULL,
  amount          bigint NOT NULL,
  user_seq        bigint DEFAULT NULL,
  picked_at       datetime DEFAULT NULL,
  create_at       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (seq),
  CONSTRAINT fk_pickups_to_scatter FOREIGN KEY (scatter_seq) REFERENCES scatters (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

create INDEX index_scatter_seq on pickups(scatter_seq);