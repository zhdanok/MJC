drop table if exists gifts_tags;
drop table if exists gift_certificate;
drop table if exists tag;
drop table if exists users_order;
drop table if exists user;
drop table if exists searchtags;
drop table if exists gift_certificate_aud;
drop table if exists tag_aud;
drop table if exists gifts_tags_aud;
drop table if exists user_aud;
drop table if exists users_order_aud;
drop table if exists revinfo;

create table revinfo
(
    rev      int auto_increment
        primary key,
    revtstmp bigint null
);

create table gift_certificate
(
    gift_id          int auto_increment
        primary key,
    create_date      datetime(6)  null,
    description      varchar(255) null,
    duration         int          null,
    last_update_date datetime(6)  null,
    gift_name        varchar(255) null,
    price            double       null,
    constraint UK_i7rd0l8oti9a1qlinna7dp8e3
        unique (gift_name)
);

create table gift_certificate_aud
(
    gift_id          int          not null,
    rev              int          not null,
    revtype          tinyint      null,
    create_date      datetime(6)  null,
    description      varchar(255) null,
    duration         int          null,
    last_update_date datetime(6)  null,
    gift_name        varchar(255) null,
    price            double       null,
    primary key (gift_id, rev),
    constraint FKhriym6x1m3uyap2l3lxfmitku
        foreign key (rev) references revinfo (rev)
);

create table tag
(
    tag_id   int auto_increment
        primary key,
    tag_name varchar(255) null,
    constraint UK_1r1tyf6uga9k6jwdqnoqwtk2a
        unique (tag_name)
);

create table tag_aud
(
    tag_id   int          not null,
    rev      int          not null,
    revtype  tinyint      null,
    tag_name varchar(255) null,
    primary key (tag_id, rev),
    constraint FKep272jdrgxgmq608l5y3792jn
        foreign key (rev) references revinfo (rev)
);

create table gifts_tags
(
    gift_id int not null,
    tag_id  int not null,
    primary key (gift_id, tag_id),
    constraint FK80fvfyo9lfiyhnnacyoobe4yt
        foreign key (tag_id) references tag (tag_id),
    constraint FKeaji0a7ri7lyq7gtdoxejv2eq
        foreign key (gift_id) references gift_certificate (gift_id)
);

create table gifts_tags_aud
(
    gift_id int     not null,
    rev     int     not null,
    revtype tinyint null,
    tag_id  int     not null,
    primary key (rev, gift_id, tag_id),
    constraint FK7sfopyr1tkf3y0thad8lh6ljb
        foreign key (rev) references revinfo (rev)
);

create table user
(
    user_id   int auto_increment
        primary key,
    user_name varchar(255) null
);

create table user_aud
(
    user_id   int          not null,
    rev       int          not null,
    revtype   tinyint      null,
    user_name varchar(255) null,
    primary key (user_id, rev),
    constraint FK89ntto9kobwahrwxbne2nqcnr
        foreign key (rev) references revinfo (rev)
);


create table users_order
(
    order_id    int auto_increment
        primary key,
    cost        double       null,
    date_of_buy datetime(6)  null,
    gift_id     int          null,
    gift_name   varchar(255) null,
    user_id     int          null
);

create table users_order_aud
(
    order_id    int          not null,
    rev         int          not null,
    revtype     tinyint      null,
    cost        double       null,
    date_of_buy datetime(6)  null,
    gift_id     int          null,
    gift_name   varchar(255) null,
    user_id     int          null,
    primary key (order_id, rev),
    constraint FK4kpxf5fbvv7hdswey49irur58
        foreign key (rev) references revinfo (rev)
);


create table searchtags
(
    stag_id   int auto_increment
        primary key,
    stag_name varchar(255) null
);
