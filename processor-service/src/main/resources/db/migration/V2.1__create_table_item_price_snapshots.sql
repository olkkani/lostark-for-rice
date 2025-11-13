CREATE TABLE auction_item_price_snapshots
(
    id        BIGINT  NOT NULL PRIMARY KEY,
    item_code INTEGER NOT NULL,
    end_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    price     INTEGER,
    CONSTRAINT uk_auction_item_code_end_date UNIQUE (item_code, end_date)
);

CREATE TABLE market_item_price_snapshots
(
    id        BIGINT  NOT NULL PRIMARY KEY,
    item_code INTEGER NOT NULL,
    price     INTEGER,
    CONSTRAINT uk_market_item_code_price UNIQUE (item_code, price)
);

CREATE INDEX idx_auction_item_price_snapshots_price ON auction_item_price_snapshots (price);
CREATE INDEX idx_market_item_price_snapshots_price ON market_item_price_snapshots (price);