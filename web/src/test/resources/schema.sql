drop table if exists gifts_tags;
drop table if exists gift_certificate;
drop table if exists tag;
drop table if exists users_order;
drop table if exists user;
drop table if exists searchtags;

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
create table user
(
    user_id   int auto_increment,
    user_name varchar(50) not null,
    constraint user_user_id_uindex
        unique (user_id)
);

alter table user
    add primary key (user_id);
create table `users_order`
(
    order_id    int auto_increment,
    user_id     int          not null,
    gift_id     int          not null,
    cost        double       null,
    date_of_buy timestamp    null,
    gift_name   varchar(256) not null,
    constraint order_order_id_uindex
        unique (order_id)
);

alter table `users_order`
    add primary key (order_id);

create table searchtags
(
    stag_id   int auto_increment
        primary key,
    stag_name varchar(60) null,
    constraint searchtags_stag_name_uindex
        unique (stag_name)
);

