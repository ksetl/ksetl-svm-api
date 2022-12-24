insert into system (system_id, name) values (1, 'System1');
insert into system (system_id, name) values (2, 'System2');
insert into system (system_id, name) values (3, 'System3');

insert into mapping (mapping_id, source_system_id, source_field_name, source_value, target_system_id, target_value, target_value_type) values (1, 1, 'Field1', 'Value1', 2, 'Value2', 'STRING');