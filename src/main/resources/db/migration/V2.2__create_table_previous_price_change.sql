CREATE TABLE item_previous_price_changes
(
    id                        BIGINT           NOT NULL PRIMARY KEY ,
    item_code                 INTEGER          NOT NULL ,
    recorded_date             DATE             NOT NULL,
    price                     INTEGER          NOT NULL,
    price_diff_prev_day       INTEGER          NOT NULL,
    price_diff_rate_prev_day  DOUBLE PRECISION NOT NULL,
    price_diff_pair_item      INTEGER,
    price_diff_rate_pair_item DOUBLE PRECISION
);