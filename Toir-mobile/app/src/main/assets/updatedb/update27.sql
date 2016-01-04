drop table if exists 'equipment_operation_new';
create table 'equipment_operation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null, 'operation_time' integer not null default 0, 'attempt_send_date' integer not null default 0, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'equipment_operation_new' ('uuid', 'task_uuid', 'equipment_uuid', 'operation_type_uuid', 'operation_pattern_uuid', 'operation_status_uuid', 'operation_time', 'CreatedAt', 'ChangedAt') select "uuid", "task_uuid", "equipment_uuid", "operation_type_uuid", "operation_pattern_uuid", "operation_status_uuid", "operation_time", "CreatedAT", "ChangedAt" from 'equipment_operation';
drop table if exists 'equipment_operation';
alter table 'equipment_operation_new' rename to 'equipment_operation';

