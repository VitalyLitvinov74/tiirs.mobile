drop table if exists 'users';
create table 'users' ('uuid' text not null primary key, 'name' text not null, 'login' text not null, 'pass' text not null, 'type' integer, 'tag_id' text not null unique, 'active' integer not null default 0);
insert into 'users' values ('4462ed77-9bf0-4542-b127-f4ecefce49da', 'admin', 'admin', 'admin', 3, '01234567', 1);
drop table if exists 'measure_value';
create table 'measure_value' ('uuid' text not null primary key, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result' text not null, 'date' text not null, 'value' text not null);
drop table if exists 'task';
create table 'task' ('uuid' text not null primary key, 'users_uuid' text not null, 'create_date' text not null, 'modify_date' text not null, 'close_date' text not null default '0000-00-00 00:00:00', 'task_status_uuid' text not null, 'attempt_send_date' text not null default '0000-00-00 00:00:00', 'attempt_count' integer not null default 0, 'successefull_send' integer not null default 0);
drop table if exists 'equipment_operation_result';
create table 'equipment_operation_result' ('uuid' text not null primary key, 'equipment_operation_uuid' text not null, 'start_date' text not null, 'end_date' text not null, 'operation_result_uuid' text not null default '00000000-0000-0000-0000-000000000000');
