ALTER TABLE auction_price_index RENAME TO daily_auction_item_ohlc;
ALTER TABLE market_price_index RENAME TO daily_market_item_ohlc;

ALTER TABLE daily_market_item_ohlc
ALTER COLUMN avg_price TYPE real USING (avg_price::real);
