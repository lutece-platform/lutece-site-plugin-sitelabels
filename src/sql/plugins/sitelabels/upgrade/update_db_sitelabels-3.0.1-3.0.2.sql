-- liquibase formatted sql
-- changeset sitelabels:update_db_sitelabels-3.0.1-3.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment decodes, once, the HTML entities the removed input filter of lutece-core 8 (sanitizeFilterMode, LUT-34109) wrote into the plain text values: the values of the site labels (core_datastore, keys sitelabels.site_property.*), which the changeset rev20 of the core does not cover. As in the core, a key ending with .htmlblock is left as it is.
-- This changeset rewrites the data of the site. Back up the database before the upgrade. It must run ONCE only: run
-- again, it would decode a second time a text that really holds an entity (a typed &lt; stored as &amp;lt; becomes &lt;
-- once, then < the second time). Only the values matching LIKE '%&%;%' are concerned; to list them before the upgrade,
-- run the WHERE clauses below in a SELECT.
UPDATE core_datastore SET entity_value = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(entity_value, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE entity_value LIKE '%&%;%' AND entity_key LIKE 'sitelabels.site_property.%' AND entity_key NOT LIKE '%.htmlblock';
