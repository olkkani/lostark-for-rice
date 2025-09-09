create table auction_price_index
(
    id            bigint  not null primary key,
    recorded_date date    not null,
    item_code     integer not null,
    close_price   integer not null,
    open_price    integer not null,
    low_price     integer not null,
    high_price    integer not null
);
create table market_price_index
(
    id            bigint                       not null
        primary key,
    item_code     integer                      not null,
    recorded_date date                         not null,
    open_price    integer                      not null,
    close_price   integer                      not null,
    high_price    integer                      not null,
    low_price     integer                      not null,
    avg_price     double precision default 0.0 not null
);