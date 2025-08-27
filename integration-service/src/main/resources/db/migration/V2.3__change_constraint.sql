-- 1. 기존 제약조건 삭제
ALTER TABLE auction_item_price_snapshots
DROP
CONSTRAINT uk_auction_item_code_end_date;

-- 2. 새로운 제약조건 추가
ALTER TABLE auction_item_price_snapshots
    ADD CONSTRAINT uk_auction_item_code_end_date_price
        UNIQUE (item_code, end_date, price);
