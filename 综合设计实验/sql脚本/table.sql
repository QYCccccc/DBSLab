drop database dbslab3;

create database dbslab3;
use dbslab3;
create table car (
    carid varchar(6) not null,
    brand varchar(10) not null ,
    status boolean not null ,
    cost  int not null ,
    pledge int not null,
    primary key (carid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table customer (
    custid varchar(6) not null ,
    cpw varchar(8) not null ,
    cname varchar(10) not null ,
    cage int ,
    balance double not null ,
    vip boolean not null default false,
    primary key (custid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table department (
    depid int not null primary key ,
    depname varchar(6) not null
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table employee (
    eid varchar(6) not null ,
    epw varchar(8) not null ,
    ename varchar(10) not null ,
    eage int ,
    depid int not null ,
    primary key (eid),
    foreign key (depid) references department(depid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



create table orderitem (
    orderid varchar(6) not null ,
    custid varchar(6) not null ,
    eid varchar(6) ,
    carid varchar(6) not null ,
    status boolean not null ,
    time TIMESTAMP not null ,
    money double not null ,
    primary key (orderid),
    foreign key (custid) references customer(custid),
    foreign key (carid) references car(carid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table punishment (
    infoid varchar(6) not null ,
    orderid varchar(6) not null ,
    time TIMESTAMP not null ,
    money double not null ,
    detail varchar(200),
    primary key (infoid),
    foreign key (orderid) references orderitem(orderid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
