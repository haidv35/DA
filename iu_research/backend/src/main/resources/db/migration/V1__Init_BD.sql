-- create sequence perfume_id_seq start 109 increment 1;
-- create sequence users_id_seq start 4 increment 1;
-- create sequence order_item_seq start 12 increment 1;
-- create sequence orders_seq start 6 increment 1;
--
-- CREATE TABLE order_item
-- (
--     id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
--     amount     INT,
--     quantity   INT,
--     perfume_id INT,
--     primary key (id)
-- );
--
-- CREATE TABLE orders
-- (
--     id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
--     address      VARCHAR(255),
--     city         VARCHAR(255),
--     DATE         DATE,
--     email        VARCHAR(255),
--     first_name   VARCHAR(255),
--     last_name    VARCHAR(255),
--     phone_number VARCHAR(255),
--     post_index   INT,
--     total_price  FLOAT,
--     primary key (id)
-- );
--
-- CREATE TABLE orders_order_items
-- (
--     order_id       INT NOT NULL,
--     order_items_id INT NOT NULL
-- );
--
-- CREATE TABLE perfume
-- (
--     id                     INT NOT NULL,
--     country                VARCHAR(255),
--     description            VARCHAR(255),
--     filename               VARCHAR(255),
--     fragrance_base_notes   VARCHAR(255),
--     fragrance_middle_notes VARCHAR(255),
--     fragrance_top_notes    VARCHAR(255),
--     perfume_gender         VARCHAR(255),
--     perfume_title          VARCHAR(255),
--     perfumer               VARCHAR(255),
--     price                  INT,
--     type                   VARCHAR(255),
--     volume                 VARCHAR(255),
--     year                   INT,
--     perfume_rating         FLOAT,
--     primary key (id)
-- );
--
-- CREATE TABLE perfume_reviews
-- (
--     perfume_id INT NOT NULL,
--     reviews_id INT NOT NULL
-- );
--
-- CREATE TABLE review
-- (
--     id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
--     author  VARCHAR(255),
--     DATE    DATE,
--     message VARCHAR(255),
--     rating  INT,
--     primary key (id)
-- );
--
-- CREATE TABLE user_role
-- (
--     user_id INT NOT NULL,
--     roles   VARCHAR(255)
-- );
--
-- CREATE TABLE users
-- (
--     id                  INT    NOT NULL,
--     activation_code     VARCHAR(255),
--     active              BOOLEAN NOT NULL,
--     address             VARCHAR(255),
--     city                VARCHAR(255),
--     email               VARCHAR(255),
--     first_name          VARCHAR(255),
--     last_name           VARCHAR(255),
--     password            VARCHAR(255),
--     password_reset_code VARCHAR(255),
--     phone_number        VARCHAR(255),
--     post_index          VARCHAR(255),
--     provider            VARCHAR(255),
--     primary key (id)
-- );
--
-- alter table if exists orders_order_items add constraint UK_9d47gapmi35omtannusv6btu3 unique (order_items_id);
-- alter table if exists perfume_reviews add constraint UK_gp5u9cs9leiwnbh2rhn27e2w7 unique (reviews_id);
-- alter table if exists order_item add constraint FKst073lwr6yongjsmgaravadre foreign key (perfume_id) references perfume;
-- alter table if exists orders_order_items add constraint FK7nw03p9mxq154wvbsonaq0qrw foreign key (order_items_id) references order_item;
-- alter table if exists orders_order_items add constraint FK3l8rktw0f4w5t6tift31e2d7c foreign key (order_id) references orders;
-- alter table if exists perfume_reviews add constraint FKq51iuslnvq3nw8teocq9y7ag8 foreign key (reviews_id) references review;
-- alter table if exists perfume_reviews add constraint FK7k3k0ru1omu7xdtdamtrl276 foreign key (perfume_id) references perfume;
-- alter table if exists user_role add constraint FKj345gk1bovqvfame88rcx7yyx foreign key (user_id) references users;

-- create table sequence perfume_id_seq start 109 increment 1;
-- create table sequence users_id_seq start 4 increment 1;
-- create table sequence order_item_seq start 12 increment 1;
-- create table sequence orders_seq start 6 increment 1;

CREATE TABLE order_item
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    amount     BIGINT,
    quantity   BIGINT,
    perfume_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    address      VARCHAR(255),
    city         VARCHAR(255),
    DATE         DATE,
    email        VARCHAR(255),
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    phone_number VARCHAR(255),
    post_index   INT,
    total_price  DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE orders_order_items
(
    id             BIGINT NOT NULL AUTO_INCREMENT,
    order_id       BIGINT NOT NULL,
    order_items_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE perfume
(
    id                     BIGINT NOT NULL AUTO_INCREMENT,
    country                VARCHAR(255),
    description            VARCHAR(255),
    filename               VARCHAR(255),
    fragrance_base_notes   VARCHAR(255),
    fragrance_middle_notes VARCHAR(255),
    fragrance_top_notes    VARCHAR(255),
    perfume_gender         VARCHAR(255),
    perfume_title          VARCHAR(255),
    perfumer               VARCHAR(255),
    price                  INT,
    type                   VARCHAR(255),
    volume                 VARCHAR(255),
    year                   INT,
    perfume_rating         DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE perfume_reviews
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    perfume_id BIGINT NOT NULL,
    reviews_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE review
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    author  VARCHAR(255),
    DATE    DATE,
    message VARCHAR(255),
    rating  INT,
    PRIMARY KEY (id)
);

CREATE TABLE user_role
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    roles   VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    activation_code     VARCHAR(255),
    active              BOOLEAN NOT NULL,
    address             VARCHAR(255),
    city                VARCHAR(255),
    email               VARCHAR(255),
    first_name          VARCHAR(255),
    last_name           VARCHAR(255),
    password            VARCHAR(255),
    password_reset_code VARCHAR(255),
    phone_number        VARCHAR(255),
    post_index          VARCHAR(255),
    provider            VARCHAR(255),
    PRIMARY KEY (id)
);

