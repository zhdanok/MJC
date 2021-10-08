create table gift_certificate
(
    id               int auto_increment,
    name             varchar(255) null,
    description      text         null,
    price            double       null,
    duration         int          null,
    create_date      timestamp    null,
    last_update_date timestamp    null,
    constraint gift_certificate_id_uindex
        unique (id),
    constraint gift_certificate_name_uindex
        unique (name)
);

alter table gift_certificate
    add primary key (id);

create table tag
(
    id   int auto_increment,
    name varchar(255) null,
    constraint tag_id_uindex
        unique (id),
    constraint tag_name_uindex
        unique (name)
);

alter table tag
    add primary key (id);

create table gifts_tags
(
    gift_id int not null,
    tag_id  int not null,
    primary key (gift_id, tag_id),
    constraint gifts_id_fk
        foreign key (gift_id) references gift_certificate (id)
            on update cascade on delete cascade,
    constraint tags_id_fk
        foreign key (tag_id) references tag (id)
            on update cascade on delete cascade
);
