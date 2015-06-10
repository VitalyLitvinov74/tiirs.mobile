drop table if exists 'gps_position';
create table 'gps_position' ('uuid' text not null primary key, 'user_uuid' text not null, 'cur_date' text not null, 'latitude' text not null, 'longitude' text not null);
