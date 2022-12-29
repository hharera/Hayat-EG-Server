create table company
(
    company_id   numeric,
    company_text text not null
        constraint "Company_pkey"
            primary key
);

alter table company
    owner to postgres;

create table user_location
(
    user_location_id numeric not null,
    user_id          numeric not null,
    user_street      text    not null,
    user_city        text    not null,
    user_state       text    not null,
    user_country     text    not null,
    location_lat     numeric,
    location_long    numeric
);

alter table user_location
    owner to postgres;

create table ingredient
(
    ingredient_id   integer      not null
        primary key,
    ingredient_name varchar(255) not null
);

alter table ingredient
    owner to postgres;

create table state
(
    id           integer              not null
        constraint state_pk
            primary key,
    arabic_name  text                 not null,
    english_name text                 not null,
    country_id   integer              not null,
    active       boolean default true not null
);

alter table state
    owner to postgres;

create table city
(
    arabic_name  text                 not null,
    id           integer              not null
        constraint city_pk
            primary key,
    state_id     integer              not null
        constraint city_state_null_fk
            references state,
    english_name text                 not null,
    active       boolean default true not null
);

alter table city
    owner to postgres;



alter table authority
    owner to postgres;

create table user_state
(
    state_id integer not null
        constraint user_state_pk
            primary key,
    state    text    not null
);

alter table user_state
    owner to postgres;

create table food_unit
(
    id           integer not null
        constraint food_unit_pk
            primary key,
    active       boolean not null,
    description  text,
    arabic_name  text,
    english_name text,
    name         varchar(255)
);

alter table food_unit
    owner to postgres;

create table medicine_unit
(
    id     integer not null
        constraint medicine_unit_pk
            primary key,
    name   text    not null,
    active boolean not null
);

alter table medicine_unit
    owner to postgres;

create table need
(
    donation_need                 integer not null
        constraint donation_need_pk
            primary key,
    donation_type_id              integer not null,
    donation_need_date            date    not null,
    donation_need_expiration_date date    not null
);

alter table need
    owner to postgres;

create table charity_location
(
    uid           integer           not null,
    location_lat  numeric,
    location_long numeric,
    address       text              not null,
    city_id       integer           not null,
    state_id      integer           not null,
    country_id    integer default 1 not null
);

alter table charity_location
    owner to postgres;

create table global_message
(
    id        bigint generated by default as identity
        primary key,
    active    boolean not null,
    code_     varchar(255),
    language_ varchar(255),
    message_  varchar(255)
);

alter table global_message
    owner to postgres;

create table announcement
(
    active      boolean      not null,
    id          integer      not null
        constraint announcement_pk
            primary key,
    title       varchar(100) not null,
    description varchar(250),
    start_date  date         not null,
    end_date    date         not null
);

alter table announcement
    owner to postgres;

create table donation_property
(
    id             integer              not null
        constraint donation_residence_pk
            primary key,
    active         boolean default true not null,
    donation_id    integer              not null,
    rooms          integer              not null,
    bathrooms      integer default 0    not null,
    kitchens       integer default 0    not null,
    available_from date                 not null,
    available_to   date                 not null
);

alter table donation_property
    owner to postgres;

create table "user"
(
    id            bigint generated by default as identity
        primary key,
    active        boolean not null,
    email         varchar(255),
    first_name    varchar(255),
    last_name     varchar(255),
    password      varchar(255),
    phone_number  varchar(255),
    user_state_id integer,
    username      varchar(255)
);

alter table "user"
    owner to postgres;

create table donation
(
    id                   bigint generated by default as identity
        primary key,
    active               boolean not null,
    date                 timestamp,
    description          varchar(255),
    expiration_date      timestamp,
    title                varchar(255),
    category             text    not null,
    city_id              bigint
        constraint fkdo9steo473ahrroirbeunkqu0
            references city,
    communication_method text    not null,
    state                text,
    uid                  bigint
        constraint fkclxn437gjbfuw7xpr4sryhpjg
            references "user"
);

alter table donation
    owner to postgres;

create table medicine_category
(
    id     integer not null
        constraint medicine_category_pk
            primary key,
    active boolean not null,
    name   varchar not null
);

alter table medicine_category
    owner to postgres;

create table medicine
(
    id             integer              not null
        primary key,
    category_id    integer
        constraint medicine_medicine_category_id_fk
            references medicine_category,
    unit_id        integer              not null
        constraint medicine_medicine_unit_fk
            references medicine_unit,
    name           varchar(255),
    active         boolean default true not null,
    info           text                 not null,
    inserting_date integer
);

alter table medicine
    owner to postgres;

create table medicine_ingredient
(
    ingredient_id integer          not null,
    medicine_id   integer          not null,
    concentration double precision not null,
    primary key (ingredient_id, medicine_id)
);

alter table medicine_ingredient
    owner to postgres;

create table packaging
(
    packaging_id   integer not null
        primary key,
    packaging_type varchar(255)
);

alter table packaging
    owner to postgres;

create table medicine_donation_unit
(
    id     integer generated by default as identity
        primary key,
    active boolean,
    name   varchar(255)
);

alter table medicine_donation_unit
    owner to postgres;

create table donation_medicine
(
    id                       integer              not null
        constraint donation_medicine_pk
            primary key,
    medicine_id              integer              not null
        constraint fkim2d4l3qa0ux8dk7lx6oxa8ts
            references medicine,
    medicine_expiration_date date                 not null,
    donation_id              integer              not null
        constraint donation_medicine_donation_id_fk
            references donation,
    active                   boolean default true not null,
    unit_id                  integer              not null
        constraint donation_medicine_medicine_unit_null_fk
            references medicine_unit
        constraint fk7nip2dudok2gicut2d2jnutex
            references medicine_donation_unit,
    amount                   double precision     not null
);

alter table donation_medicine
    owner to postgres;

create table donation_food
(
    id              integer generated always as identity (minvalue 1001)
        constraint donation_food_pk
            primary key,
    expiration_date date                 not null,
    active          boolean default true not null,
    unit_id         integer              not null
        constraint fkorowunxtn73gty38fo9oov493
            references food_unit,
    amount          double precision     not null
);

alter table donation_food
    owner to postgres;

create table donation_clothes
(
    id             integer              not null
        constraint donation_clothes_pk
            primary key,
    active         boolean default true not null,
    clothes_status text                 not null,
    amount         integer              not null
);

alter table donation_clothes
    owner to postgres;

create table donation_book
(
    id          integer              not null
        constraint donation_book_pk
            primary key,
    active      boolean default true not null,
    donation_id integer              not null
        constraint donation_book_donation_id_fk
            references donation
);

alter table donation_book
    owner to postgres;

create table donation_book_item
(
    id               integer              not null
        constraint donation_book_item_pk
            primary key,
    active           boolean default true not null,
    donation_book_id integer              not null
        constraint donation_book_item_fk
            references donation_book,
    name             text                 not null,
    amount           integer              not null,
    status           text                 not null
);

alter table donation_book_item
    owner to postgres;

create table donation_baggage
(
    id          integer              not null
        constraint donation_baggage_pk
            primary key,
    active      boolean default true not null,
    donation_id integer              not null
        constraint donation_baggage_donation_null_fk
            references donation
);

alter table donation_baggage
    owner to postgres;

create table donation_baggage_item
(
    id                  integer              not null
        constraint baggage_item_pk
            primary key,
    active              boolean default true not null,
    donation_baggage_id integer              not null
        constraint baggage_item_fk
            references donation_baggage,
    name                text                 not null,
    amount              integer              not null,
    status              text                 not null
);

alter table donation_baggage_item
    owner to postgres;

create table donation_clothes_item
(
    id                 integer              not null
        constraint donation_clothes_item_pk
            primary key,
    active             boolean default true not null,
    donation_clothe_id integer              not null
        constraint donation_clothes_item_fk
            references donation_clothes,
    name               text                 not null,
    amount             integer              not null,
    status             text                 not null
);

alter table donation_clothes_item
    owner to postgres;

create table donation_post
(
    post_id                  integer generated by default as identity
        primary key,
    city_id                  integer,
    medicine_expiration_date timestamp,
    medicine_id              integer,
    post_date                timestamp,
    post_description         varchar(255),
    post_state_id            integer,
    uid                      integer
);

alter table donation_post
    owner to postgres;

create table donation_category
(
    id     bigint generated by default as identity
        primary key,
    active boolean not null,
    name   varchar(255)
);

alter table donation_category
    owner to postgres;

