drop table if exists 'equipment_operation_result';
create table 'equipment_operation_result' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'start_date' integer not null, 'end_date' integer not null default 0, 'operation_result_uuid' text not null default '00000000-0000-0000-0000-000000000000', 'type' integer not null default 0);
