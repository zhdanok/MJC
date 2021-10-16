create table gift_certificate
(
    gift_id               int auto_increment,
    gift_name             varchar(255) null,
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
