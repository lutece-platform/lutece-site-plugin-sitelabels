-- liquibase formatted sql
-- changeset sitelabels:init_core_sitelabels_sample.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping sample data for table core_datastore
--

INSERT INTO core_datastore VALUES ('sitelabels.site_property.mylabel.label1', 'Valeur de label1' );
INSERT INTO core_datastore VALUES ('sitelabels.site_property.mylabel.label2', 'Valeur de label2');