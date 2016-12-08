
    alter table entry 
        drop 
        foreign key FK_5hg3h6e58hj5bj7wepqf3mmd9;

    drop table if exists entry;

    drop table if exists type_of_goods;

    create table entry (
        id integer not null auto_increment,
        f longtext,
        g longtext,
        h longtext,
        i longtext,
        j longtext,
        k longtext,
        description_of_good longtext,
        price integer,
        quantity integer,
        types_id integer,
        primary key (id)
    ) type=InnoDB;

    create table type_of_goods (
        id integer not null auto_increment,
        description longtext,
        primary key (id)
    ) type=InnoDB;

    alter table entry 
        add constraint FK_5hg3h6e58hj5bj7wepqf3mmd9 
        foreign key (types_id) 
        references type_of_goods (id);
