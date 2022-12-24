CREATE TABLE system
(
    system_id SERIAL PRIMARY KEY,
    name      TEXT NOT NULL
);
ALTER SEQUENCE system_system_id_seq RESTART 1000;

CREATE TABLE mapping
(
    mapping_id SERIAL PRIMARY KEY,
    source_system_id INT REFERENCES system (system_id) NOT NULL,
    source_field_name TEXT NOT NULL,
    source_value TEXT NOT NULL,
    target_system_id INT REFERENCES system (system_id) NOT NULL,
    target_value TEXT NOT NULL,
    target_value_type TEXT NOT NULL
);
ALTER SEQUENCE mapping_mapping_id_seq RESTART 1000;
CREATE UNIQUE INDEX mapping_unique_idx on mapping (source_system_id, source_field_name, source_value, target_system_id, target_value);