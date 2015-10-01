drop table if exists 'measure_value_new';
create table 'measure_value_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result' text not null, 'value' text not null, 'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'measure_value_new' ('uuid', 'equipment_operation_uuid', 'operation_pattern_step_result', 'value') select "uuid", "equipment_operation_uuid", "operation_pattern_step_result", "value" from 'measure_value';
drop table if exists 'measure_value';
alter table 'measure_value_new' rename to 'measure_value';

