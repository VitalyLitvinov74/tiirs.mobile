drop table if exists 'task_new';
create table 'task_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'users_uuid' text not null, 'CreatedAt' integer not null, 'ChangedAt' integer not null, 'close_date' integer default null, 'task_status_uuid' text not null, 'attempt_send_date' integer not null default 0, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, 'task_name' text);
insert into 'task_new' ('uuid', 'users_uuid', 'CreatedAt', 'ChangedAt', 'close_date', 'task_status_uuid', 'attempt_send_date', 'attempt_count', 'updated', 'task_name') select "uuid", "users_uuid", "create_date", "modify_date", "close_date", "task_status_uuid", "attempt_send_date", "attempt_count", "updated", "task_name" from 'task';
drop table if exists 'task';
alter table 'task_new' rename to 'task';

drop table if exists 'equipment_operation_result_new';
create table 'equipment_operation_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'start_date' integer not null, 'end_date' integer default null, 'operation_result_uuid' text not null default '00000000-0000-0000-0000-000000000000', 'type' integer not null default 0,  'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0);
insert into 'equipment_operation_result_new' ('uuid', 'equipment_operation_uuid', 'start_date', 'end_date', 'operation_result_uuid') select "uuid", "equipment_operation_uuid", "start_date", "end_date", "operation_result_uuid" from 'equipment_operation_result';
drop table if exists 'equipment_operation_result';
alter table 'equipment_operation_result_new' rename to 'equipment_operation_result';

drop table if exists 'measure_value_new';
create table 'measure_value_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result' text not null, 'date' integer not null, 'value' text not null, 'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0);
insert into 'measure_value_new' ('uuid', 'equipment_operation_uuid', 'operation_pattern_step_result', 'date', 'value') select "uuid", "equipment_operation_uuid", "operation_pattern_step_result", "date", "value" from 'measure_value';
drop table if exists 'measure_value';
alter table 'measure_value_new' rename to 'measure_value';

