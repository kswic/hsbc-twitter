drop table if exists user CASCADE;
drop table if exists post CASCADE;
drop table if exists subscription CASCADE;

create table user
(
    id varchar(10) primary key
);

create table post
(
    id          bigint auto_increment primary key,
    author_id   varchar(10),
    message     varchar(140),
    create_time timestamp
);

create table subscription
(
    id               bigint auto_increment primary key,
    owner_id         varchar(10),
    followed_user_id varchar(10)
);

alter table subscription
    add constraint fk_subscription_owner_id foreign key (owner_id) references user (id);
alter table subscription
    add constraint fk_subscription_followed_user_id foreign key (followed_user_id) references user (id);