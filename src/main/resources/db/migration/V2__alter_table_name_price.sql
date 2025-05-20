ALTER TABLE auction_price_index RENAME TO daily_auction_item_ohlc_prices;
ALTER TABLE market_price_index RENAME TO daily_market_item_ohlca_prices;

ALTER TABLE daily_market_item_ohlca_prices
ALTER COLUMN avg_price TYPE real USING (avg_price::real);
