-- liquibase formatted sql
-- changeset sitelabels:init_core_sitelabels.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_admin_right
--

INSERT INTO core_admin_right (id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) 
VALUES ('SITELABELS_MANAGEMENT','sitelabels.adminFeature.ManageSiteLabels.name',0,'jsp/admin/plugins/sitelabels/ManageLabels.jsp','sitelabels.adminFeature.ManageSiteLabels.description',0,'sitelabels',NULL,NULL,NULL,4);


INSERT INTO core_user_right (id_right,id_user) VALUES ('SITELABELS_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('SITELABELS_MANAGEMENT',2);
