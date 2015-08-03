drop table if exists 'operation_status';
create table 'operation_status' ('_id' integer not null primary key,'uuid' text not null unique, 'title' text not null);
