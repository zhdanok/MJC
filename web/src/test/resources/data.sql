create schema MJC_School;

use MJC_School;

create table gift_certificate
(
    gift_id          int auto_increment,
    gift_name        varchar(255) null,
    description      text         null,
    price            double       null,
    duration         int          null,
    create_date      timestamp    null,
    last_update_date timestamp    null,
    constraint gift_certificate_id_uindex
        unique (gift_id),
    constraint gift_certificate_name_uindex
        unique (gift_name)
);

alter table gift_certificate
    add primary key (gift_id);

create table tag
(
    tag_id   int auto_increment,
    tag_name varchar(255) null,
    constraint tag_id_uindex
        unique (tag_id),
    constraint tag_name_uindex
        unique (tag_name)
);

alter table tag
    add primary key (tag_id);

create table gifts_tags
(
    gift_id int not null,
    tag_id  int not null,
    primary key (gift_id, tag_id),
    constraint gifts_id_fk
        foreign key (gift_id) references gift_certificate (gift_id)
            on update cascade on delete cascade,
    constraint tags_id_fk
        foreign key (tag_id) references tag (tag_id)
            on update cascade on delete cascade
);

INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (3, 'gift', 'disc gift', 123.17, 123, '2021-10-05 20:07:59', '2021-10-05 20:08:31');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (5, 'gift-update-000', 'disc gift 100', 639.4, 180, '2021-06-05 20:08:55', '2021-10-07 12:28:19');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (9, 'check', 'description', 34.5, 70, '2021-10-07 01:01:21', '2021-10-07 01:01:21');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (13, 'mobile-55', 'mts-55', 999, 200, '2021-10-07 01:28:01', '2021-10-07 13:02:46');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (14, 'mobile-66', 'mts-66', 36.7, 200, '2021-10-07 01:33:47', '2021-10-07 01:33:47');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (16, 'gift-upd777ate-000', 'disc gift 100', 639.4, 180, '2021-10-07 12:46:30', '2021-10-07 12:46:30');
INSERT INTO gift_certificate (gift_id, gift_name, description, price, duration, create_date, last_update_date)
VALUES (17, 'test', 'for test', 555.5, 45, '2021-10-08 12:30:44', '2021-10-08 12:30:44');

INSERT INTO tag (tag_id, tag_name)
VALUES (14, '?');
INSERT INTO tag (tag_id, tag_name)
VALUES (1, 'cofe');
INSERT INTO tag (tag_id, tag_name)
VALUES (13, 'dom');
INSERT INTO tag (tag_id, tag_name)
VALUES (10, 'flower');
INSERT INTO tag (tag_id, tag_name)
VALUES (6, 'health');
INSERT INTO tag (tag_id, tag_name)
VALUES (7, 'house');
INSERT INTO tag (tag_id, tag_name)
VALUES (5, 'makeup');
INSERT INTO tag (tag_id, tag_name)
VALUES (4, 'ocean');
INSERT INTO tag (tag_id, tag_name)
VALUES (23, 'ocean-77');
INSERT INTO tag (tag_id, tag_name)
VALUES (26, 'ocean758');
INSERT INTO tag (tag_id, tag_name)
VALUES (8, 'postman');
INSERT INTO tag (tag_id, tag_name)
VALUES (9, 'repost');
INSERT INTO tag (tag_id, tag_name)
VALUES (12, 'take');
INSERT INTO tag (tag_id, tag_name)
VALUES (2, 'tea');
INSERT INTO tag (tag_id, tag_name)
VALUES (11, 'tictok');
INSERT INTO tag (tag_id, tag_name)
VALUES (18, 'wakeup');
INSERT INTO tag (tag_id, tag_name)
VALUES (21, 'wakeup-66');

INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (3, 1);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (14, 2);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (5, 4);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (16, 4);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (5, 5);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (13, 5);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (5, 6);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (3, 7);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (13, 7);
INSERT INTO gifts_tags (gift_id, tag_id)
VALUES (9, 12);
