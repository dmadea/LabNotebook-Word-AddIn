-- roles
-- change passwords here

create role indigo_owner login password 'indigo_owner' ;
create role indigo_user login password 'indigo_user' ;

-- schema

create schema indigo_owner authorization indigo_owner ;

set search_path to indigo_owner ;

alter role indigo_owner set search_path to indigo_owner,public ;
alter role indigo_user set search_path to indigo_owner,public ;

-- sequences

create sequence cen_log_seq ;
alter sequence cen_log_seq owner to indigo_owner ;

create sequence cen_metrics_seq ;
alter sequence cen_metrics_seq owner to indigo_owner ;

create sequence cen_queries_seq ;
alter sequence cen_queries_seq owner to indigo_owner ;

create sequence cen_reg_job_id ;
alter sequence cen_reg_job_id owner to indigo_owner ;

create sequence hibernate_sequence ;
alter sequence hibernate_sequence owner to indigo_owner ;

create sequence reg_batch_key_seq ;
alter sequence reg_batch_key_seq owner to indigo_owner ;

-- tables

-- table: cen_users

create table cen_users
(
  username varchar(64) not null,
  password varchar(256),
  xml_metadata text,
  my_reagents text,
  fullname varchar(256),
  email varchar(256),
  site_code varchar(16),
  status varchar(16),
  privilege_list varchar(1024),
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  audit_log text,
  constraint cen_users_pk primary key (username)
) ;
alter table cen_users owner to indigo_owner ;

-- table: cen_notebooks

create table cen_notebooks
(
  site_code varchar(16) not null,
  username varchar(64) not null,
  notebook varchar(8) not null,
  status varchar(16),
  xml_metadata text,
  id numeric,
  version numeric,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_notebooks_pk primary key (site_code, username, notebook),
  constraint cen_notebooks_fk foreign key (username) references cen_users (username) on delete cascade
) ;
alter table cen_notebooks owner to indigo_owner ;

-- table: cen_pages

create table cen_pages
(
  page_key varchar(256) not null,
  site_code varchar(16),
  notebook varchar(8),
  experiment varchar(4),
  username varchar(64),
  owner_username varchar(64),
  look_n_feel varchar(16),
  page_status varchar(16),
  creation_date timestamp with time zone,
  modified_date timestamp with time zone,
  xml_metadata text,
  procedure text,
  page_version integer,
  latest_version varchar(1),
  pdf_document bytea,
  spid varchar(32),
  ta_code varchar(12),
  project_code varchar(64),
  literature_ref text,
  subject text,
  series_id varchar(16),
  protocol_id varchar(32),
  batch_owner varchar(64),
  batch_creator varchar(64),
  design_submitter varchar(64),
  nbk_ref_version varchar(24),
  version numeric default 0,
  constraint cen_pages_pk primary key (page_key),
  constraint cen_pages_fk foreign key (site_code, username, notebook) references cen_notebooks (site_code, username, notebook) on delete cascade,
  constraint cen_pages_nbk_ref_version_key unique (nbk_ref_version)
) ;
alter table cen_pages owner to indigo_owner ;

-- table: cen_lists

create table cen_lists
(
  list_key varchar(256) not null,
  list_name varchar(64),
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_lists_pk primary key (list_key)
) ;
alter table cen_lists owner to indigo_owner ;

-- table: cen_batches

create table cen_batches
(
  batch_key varchar(256) not null,
  page_key varchar(256),
  batch_number varchar(32),
  struct_key varchar(256),
  xml_metadata text,
  step_key varchar(256),
  batch_type varchar(32),
  molecular_formula varchar(128),
  theoritical_yield_percent numeric,
  salt_code varchar(5),
  salt_equivs numeric,
  list_key varchar(256),
  batch_mw_value numeric,
  batch_mw_unit_code varchar(5),
  batch_mw_is_calc varchar(1) default 'Y'::varchar,
  batch_mw_sig_digits numeric,
  batch_mw_sig_digits_set varchar(1) default 'Y'::varchar,
  batch_mw_user_pref_figs numeric,
  is_limiting varchar(1) default 'N'::varchar,
  auto_calc varchar(1),
  synthszd_by varchar(64),
  added_solv_batch_key varchar(256),
  no_of_times_used numeric,
  intd_addition_order numeric,
  chloracnegen_type varchar(32),
  is_chloracnegen varchar(1),
  tested_for_chloracnegen varchar(1) default 'N'::varchar,
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_batches_pk primary key (batch_key),
  constraint cen_batches_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade,
  constraint cen_batches_fk2 foreign key (list_key) references cen_lists (list_key) on delete cascade
) ;
alter table cen_batches owner to indigo_owner ;

-- table: cen_analysis

create table cen_analysis
(
  analysis_key varchar(256) not null,
  page_key varchar(256),
  xml_metadata text,
  blob_data bytea,
  cen_sample_ref varchar(256),
  analytical_service_sample_ref varchar(256),
  annotation text,
  comments text,
  site_code varchar(16),
  cyber_lab_domain_id varchar(64),
  cyber_lab_file_id varchar(64),
  cyber_lab_folder_id varchar(64),
  cyber_lab_lcdf_id varchar(64),
  cyber_lab_user_id varchar(64),
  domain varchar(16),
  server varchar(256),
  url varchar(256),
  user_id varchar(64),
  analytical_version numeric,
  instrument varchar(64),
  instrument_type varchar(64),
  file_name varchar(512),
  file_size numeric,
  file_type varchar(16),
  experiment_time varchar(64),
  experiment text,
  group_id varchar(64),
  ip_related varchar(1),
  is_linked varchar(1),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_analysis_pk primary key (analysis_key),
  constraint cen_analysis_fk foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_analysis owner to indigo_owner ;

-- table: cen_attachements

create table cen_attachements
(
  attachement_key varchar(256) not null,
  page_key varchar(256),
  xml_metadata text,
  blob_data bytea,
  date_modified varchar(64),
  document_description text,
  document_name varchar(256),
  ip_related varchar(1),
  original_file_name varchar(512),
  document_size numeric,
  document_type varchar(64),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_attachements_pk primary key (attachement_key),
  constraint cen_attachements_fk foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_attachements owner to indigo_owner ;

-- table: cen_audit_trail

create table cen_audit_trail
(
  table_name varchar(64),
  record_primary_key varchar(256),
  action varchar(16),
  modified_by varchar(64),
  modified_timestamp timestamp with time zone default current_timestamp
) ;
alter table cen_audit_trail owner to indigo_owner ;

-- table: cen_batch_amounts

create table cen_batch_amounts
(
  batch_key varchar(256),
  page_key varchar(256),
  weight_value numeric,
  weight_unit_code varchar(5),
  weight_is_calc varchar(1) default 'Y'::varchar,
  weight_sig_digits numeric,
  weight_sig_digits_set varchar(1) default 'Y'::varchar,
  weight_user_pref_figs numeric,
  volume_value numeric,
  volume_unit_code varchar(5),
  volume_is_calc varchar(1) default 'Y'::varchar,
  volume_sig_digits numeric,
  volume_sig_digits_set varchar(1) default 'Y'::varchar,
  volume_user_pref_figs numeric,
  molarity_value numeric,
  molarity_unit_code varchar(5),
  molarity_is_calc varchar(1) default 'Y'::varchar,
  molarity_sig_digits numeric,
  molarity_sig_digits_set varchar(1) default 'Y'::varchar,
  molarity_user_pref_figs numeric,
  mole_value numeric,
  mole_unit_code varchar(5),
  mole_is_calc varchar(1) default 'Y'::varchar,
  mole_sig_digits numeric,
  mole_sig_digits_set varchar(1) default 'Y'::varchar,
  mole_user_pref_figs numeric,
  density_value numeric,
  density_unit_code varchar(5),
  density_is_calc varchar(1) default 'Y'::varchar,
  density_sig_digits numeric,
  density_sig_digits_set varchar(1) default 'Y'::varchar,
  density_user_pref_figs numeric,
  purity_value numeric,
  purity_unit_code varchar(5),
  purity_is_calc varchar(1) default 'Y'::varchar,
  purity_sig_digits numeric,
  purity_sig_digits_set varchar(1) default 'Y'::varchar,
  purity_user_pref_figs numeric,
  loading_value numeric,
  loading_unit_code varchar(5),
  loading_is_calc varchar(1) default 'Y'::varchar,
  loading_sig_digits numeric,
  loading_sig_digits_set varchar(1) default 'Y'::varchar,
  loading_user_pref_figs numeric,
  rxnequivs_value numeric,
  rxnequivs_unit_code varchar(5),
  rxnequivs_is_calc varchar(1) default 'Y'::varchar,
  rxnequivs_sig_digits numeric,
  rxnequivs_sig_digits_set varchar(1) default 'Y'::varchar,
  rxnequivs_user_pref_figs numeric,
  theo_wt_value numeric,
  theo_wt_unit_code varchar(5),
  theo_wt_is_calc varchar(1) default 'Y'::varchar,
  theo_wt_sig_digits numeric,
  theo_wt_sig_digits_set varchar(1) default 'Y'::varchar,
  theo_wt_user_pref_figs numeric,
  theo_mole_value numeric,
  theo_mole_unit_code varchar(5),
  theo_mole_is_calc varchar(1) default 'Y'::varchar,
  theo_mole_sig_digits numeric,
  theo_mole_sig_digits_set varchar(1) default 'Y'::varchar,
  theo_mole_user_pref_figs numeric,
  total_wt_value numeric,
  total_wt_unit_code varchar(5),
  total_wt_is_calc varchar(1) default 'Y'::varchar,
  total_wt_sig_digits numeric,
  total_wt_sig_digits_set varchar(1) default 'Y'::varchar,
  total_wt_user_pref_figs numeric,
  total_vol_value numeric,
  total_vol_unit_code varchar(5),
  total_vol_is_calc varchar(1) default 'Y'::varchar,
  total_vol_sig_digits numeric,
  total_vol_sig_digits_set varchar(1) default 'Y'::varchar,
  total_vol_user_pref_figs numeric,
  deliv_wt_value numeric,
  deliv_wt_unit_code varchar(5),
  deliv_wt_is_calc varchar(1) default 'Y'::varchar,
  deliv_wt_sig_digits numeric,
  deliv_wt_sig_digits_set varchar(1) default 'Y'::varchar,
  deliv_wt_user_pref_figs numeric,
  deliv_vol_value numeric,
  deliv_vol_unit_code varchar(5),
  deliv_vol_is_calc varchar(1) default 'Y'::varchar,
  deliv_vol_sig_digits numeric,
  deliv_vol_sig_digits_set varchar(1) default 'Y'::varchar,
  deliv_vol_user_pref_figs numeric,
  needed_mole_value numeric,
  needed_mole_unit_code varchar(5),
  needed_mole_is_calc varchar(1) default 'Y'::varchar,
  needed_mole_sig_digits numeric,
  needed_mole_sig_digits_set varchar(1) default 'Y'::varchar,
  needed_mole_user_pref_figs numeric,
  ex_neded_mole_value numeric,
  ex_neded_mole_unit_code varchar(5),
  ex_neded_mole_is_calc varchar(1) default 'Y'::varchar,
  ex_neded_mole_sig_digits numeric,
  ex_neded_mole_sig_digits_set varchar(1) default 'Y'::varchar,
  ex_neded_mole_user_pref_figs numeric,
  solute_wt_value numeric,
  solute_wt_unit_code varchar(5),
  solute_wt_is_calc varchar(1) default 'Y'::varchar,
  solute_wt_sig_digits numeric,
  solute_wt_sig_digits_set varchar(1) default 'Y'::varchar,
  solute_wt_user_pref_figs numeric,
  prev_molar_value numeric,
  prev_molar_unit_code varchar(5),
  prev_molar_is_calc varchar(1) default 'Y'::varchar,
  prev_molar_sig_digits numeric,
  prev_molar_sig_digits_set varchar(1) default 'Y'::varchar,
  prev_molar_user_pref_figs numeric,
  theo_yld_pcnt_value numeric,
  theo_yld_pcnt_unit_code varchar(5),
  theo_yld_pcnt_is_calc varchar(1) default 'Y'::varchar,
  theo_yld_pcnt_sig_digits numeric,
  theo_yld_pcnt_sig_digits_set varchar(1) default 'Y'::varchar,
  theo_yld_pcnt_user_pref_figs numeric,
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_batch_amounts_fk1 foreign key (batch_key) references cen_batches (batch_key) on delete cascade,
  constraint cen_batch_amounts_fk2 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_batch_amounts owner to indigo_owner ;

-- table: cen_container

create table cen_container
(
  container_key varchar(256) not null,
  container_code varchar(16) not null,
  creator_id varchar(64) not null,
  container_name varchar(64),
  is_user_defined varchar(1) not null,
  x_positions numeric,
  y_positions numeric,
  major_axis varchar(1),
  container_type varchar(16),
  last_modified timestamp with time zone default current_timestamp,
  skip_well_positions varchar(512),
  constraint cen_container_pk primary key (container_key),
  constraint cen_container_fk foreign key (creator_id) references cen_users (username) on delete cascade,
  constraint cen_container_uq unique (container_code, creator_id)
) ;
alter table cen_container owner to indigo_owner ;

-- table: cen_cro_pageinfo

create table cen_cro_pageinfo
(
  page_key varchar(256) not null,
  vendor_id varchar(256),
  vendor_display_name varchar(256),
  vendor_chemist_id varchar(50),
  vendor_chemist_display_name varchar(256),
  vendor_application_source varchar(256),
  request_id varchar(256) not null,
  xml_metadata text,
  last_modified timestamp with time zone default current_timestamp,
  version numeric,
  constraint cen_cro_pageinfo_pk primary key (page_key),
  constraint cen_cro_pageinfo_fk foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_cro_pageinfo owner to indigo_owner ;

-- table: cen_jobs

create table cen_jobs
(
  job_id varchar(256) not null,
  plate_key varchar(256),
  compound_registration_status varchar(256),
  modified_date timestamp with time zone,
  compound_reg_submission_date timestamp with time zone,
  compound_reg_status_message varchar(256),
  compound_management_status varchar(256),
  compound_mgmt_status_message varchar(256),
  purification_service_status varchar(256),
  pur_service_status_msg varchar(256),
  compound_aggregation_status varchar(256),
  cmpd_aggregation_status_msg varchar(256),
  callback_url varchar(256),
  page_key varchar(256),
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_jobs_pk primary key (job_id)
) ;
alter table cen_jobs owner to indigo_owner ;

-- table: cen_log

create table cen_log
(
  id integer not null,
  timestamp date,
  message text,
  app_version numeric,
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_log_pk primary key (id)
) ;
alter table cen_log owner to indigo_owner ;

-- table: cen_metrics

create table cen_metrics
(
  log_seq_number numeric not null,
  application_name varchar(64),
  sub_function_name varchar(64),
  when_used date,
  account_name varchar(64),
  site_code varchar(16),
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_metrics_pk primary key (log_seq_number)
) ;
alter table cen_metrics owner to indigo_owner ;

-- table: cen_reaction_steps

create table cen_reaction_steps
(
  step_key varchar(256) not null,
  page_key varchar(256),
  seq_num numeric default 0,
  rxn_scheme_key varchar(256),
  xml_metadata text,
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_reaction_steps_pk primary key (step_key),
  constraint cen_reaction_steps_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_reaction_steps owner to indigo_owner ;

-- table: cen_plate

create table cen_plate
(
  plate_key varchar(256) not null,
  plate_bar_code varchar(16),
  cen_plate_type varchar(16),
  page_key varchar(256),
  step_key varchar(256),
  plate_number varchar(32),
  registered_date timestamp without time zone,
  parent_plate_key varchar(256),
  container_key varchar(256),
  comments varchar(128),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_plate_pk primary key (plate_key),
  constraint cen_plate_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade,
  constraint cen_plate_fk2 foreign key (step_key) references cen_reaction_steps (step_key) on delete cascade,
  constraint cen_plate_fk3 foreign key (container_key) references cen_container (container_key) on delete cascade,
  constraint cen_plate_fk4 foreign key (parent_plate_key) references cen_plate (plate_key) on delete cascade
) ;
alter table cen_plate owner to indigo_owner ;

-- table: cen_plate_well

create table cen_plate_well
(
  well_key varchar(256) not null,
  plate_key varchar(256),
  well_position numeric,
  well_type varchar(16),
  batch_key varchar(256),
  solvent_code varchar(16),
  barcode varchar(16),
  weight_value numeric,
  weight_unit_code varchar(5),
  weight_is_calc varchar(1) default 'Y'::varchar,
  weight_sig_digits numeric,
  weight_sig_digits_set varchar(1) default 'Y'::varchar,
  weight_user_pref_figs numeric,
  volume_value numeric,
  volume_unit_code varchar(5),
  volume_is_calc varchar(1) default 'Y'::varchar,
  volume_sig_digits numeric,
  volume_sig_digits_set varchar(1) default 'Y'::varchar,
  volume_user_pref_figs numeric,
  molarity_value numeric,
  molarity_unit_code varchar(5),
  molarity_is_calc varchar(1) default 'Y'::varchar,
  molarity_sig_digits numeric,
  molarity_sig_digits_set varchar(1) default 'Y'::varchar,
  molarity_user_pref_figs numeric,
  version numeric not null default 0,
  well_number varchar(5),
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_well_pk primary key (well_key),
  constraint cen_plate_well_fk1 foreign key (plate_key) references cen_plate (plate_key) on delete cascade,
  constraint cen_plate_well_fk2 foreign key (batch_key) references cen_batches (batch_key) on delete cascade
) ;
alter table cen_plate_well owner to indigo_owner ;

-- table: cen_procedure_images

create table cen_procedure_images
(
  image_key varchar(256) not null,
  page_key varchar(256),
  image_type varchar(256),
  image_data bytea,
  constraint cen_procedure_images_pk primary key (image_key),
  constraint cen_procedure_images_fk foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_procedure_images owner to indigo_owner ;

-- table: cen_properties

create table cen_properties
(
  site_code varchar(16) not null,
  enabled_flag varchar(5),
  xml_metadata text,
  reagent_db_properties text,
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_properties_pk primary key (site_code)
) ;
alter table cen_properties owner to indigo_owner ;

-- table: cen_purification_service

create table cen_purification_service
(
  purification_service_key varchar(256) not null,
  modifiers varchar(256),
  ph_value numeric,
  archive_plate varchar(256),
  archive_volume numeric,
  sample_workup varchar(256),
  inorganic_byproduct_salt varchar(1),
  seperate_isomers varchar(1),
  destination_lab varchar(256),
  well_key varchar(256),
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_ap3_pk primary key (purification_service_key),
  constraint cen_ap3_fk foreign key (well_key) references cen_plate_well (well_key) on delete cascade
) ;
alter table cen_purification_service owner to indigo_owner ;

-- table: cen_queries

create table cen_queries
(
  id numeric not null,
  sub_id numeric not null,
  query_blob bytea,
  query_string varchar(1024),
  load_date date,
  version numeric,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_queries_pk primary key (id, sub_id)
) ;
alter table cen_queries owner to indigo_owner ;

-- table: cen_reaction_schemes

create table cen_reaction_schemes
(
  rxn_scheme_key varchar(256) not null,
  page_key varchar(256),
  reaction_type varchar(16),
  rxn_sketch bytea,
  native_rxn_sketch bytea,
  sketch_image bytea,
  xml_metadata text,
  vrxn_id varchar(512),
  protocol_id varchar(32),
  rxn_skth_frmt varchar(16),
  native_rxn_skth_frmt varchar(16),
  rxn_image_frmt varchar(16),
  synth_route_ref varchar(20),
  reaction_id varchar(20),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  constraint cen_reaction_schemes_pk primary key (rxn_scheme_key),
  constraint cen_reaction_schemes_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_reaction_schemes owner to indigo_owner ;

-- table: cen_reg_jobs

create table cen_reg_jobs
(
  job_id numeric not null,
  plate_key varchar(64),
  compound_registration_status varchar(32),
  modified_date timestamp with time zone,
  compound_reg_submission_date timestamp with time zone,
  compound_reg_status_message varchar(2048),
  compound_management_status varchar(32),
  compound_mgmt_status_message varchar(2048),
  purification_service_status varchar(32),
  pur_service_status_msg varchar(2048),
  compound_aggregation_status varchar(32),
  cmpd_aggregation_status_msg varchar(2048),
  callback_url varchar(256),
  page_key varchar(256),
  compound_registration_job_id varchar(32),
  batch_keys text,
  plate_keys text,
  workflow varchar(128),
  status varchar(16),
  last_modified timestamp with time zone default current_timestamp,
  version numeric,
  constraint cen_reg_jobs_pk primary key (job_id),
  constraint cen_reg_jobs_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_reg_jobs owner to indigo_owner ;

-- table: cen_registered_batches

create table cen_registered_batches
(
  reg_batch_key varchar(256) not null,
  page_key varchar(256),
  batch_key varchar(256),
  conversational_batch_number varchar(32),
  parent_batch_number varchar(32),
  batch_tracking_id numeric,
  job_id numeric,
  registration_status varchar(32),
  submission_status varchar(32),
  status varchar(32),
  xml_metadata text,
  compound_aggregation_status varchar(256),
  cmpd_aggregation_status_msg varchar(256),
  purification_service_status varchar(256),
  pur_service_status_msg varchar(256),
  compound_registration_offset numeric,
  compound_management_status varchar(256),
  compound_mgmt_status_message varchar(256),
  compound_reg_status_message varchar(256),
  melt_point_val_lower numeric,
  melt_point_val_upper numeric,
  melt_point_comments varchar(400),
  supplier_code varchar(12),
  supplier_registry_number varchar(70),
  source_code varchar(10),
  source_detail_code varchar(10),
  batch_comments varchar(1400),
  compound_state varchar(12),
  batch_hazard_comment varchar(240),
  batch_handling_comment varchar(200),
  batch_storage_comment varchar(500),
  protection_code varchar(5),
  continue_status varchar(15),
  selectivity_status varchar(15),
  hit_id varchar(30),
  registration_date timestamp with time zone,
  vnv_status varchar(15),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  batch_owner varchar(50),
  product_flag varchar(1),
  intermediate_or_test varchar(20) default 'U'::varchar,
  constraint cen_registered_batches_pk primary key (reg_batch_key),
  constraint cen_registered_batches_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade,
  constraint cen_registered_batches_fk2 foreign key (batch_key) references cen_batches (batch_key) on delete cascade,
  constraint check_intermediate_or_test check (intermediate_or_test in ('Intermediate', 'Test Compound', 'U', 'N'))
) ;
alter table cen_registered_batches owner to indigo_owner ;

-- table: cen_report_templates

create table cen_report_templates
(
  template_id numeric not null,
  template_name varchar(256),
  description varchar(256),
  hb_version numeric,
  major_version numeric,
  minor_version numeric,
  report_template text,
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_report_templates_pk primary key (template_id),
  constraint cen_report_templates_uq unique (template_name, major_version, minor_version)
) ;
alter table cen_report_templates owner to indigo_owner ;

-- table: cen_step_batch_lists

create table cen_step_batch_lists
(
  step_key varchar(256),
  list_key varchar(256),
  position varchar(3),
  page_key varchar(256),
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_step_batch_lists_fk1 foreign key (step_key) references cen_reaction_steps (step_key),
  constraint cen_step_batch_lists_fk2 foreign key (list_key) references cen_lists (list_key),
  constraint cen_step_batch_lists_fk3 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_step_batch_lists owner to indigo_owner ;

-- table: cen_structures

create table cen_structures
(
  struct_key varchar(256) not null,
  page_key varchar(256),
  struct_sketch bytea,
  native_struct_sketch bytea,
  struct_image bytea,
  xml_metadata text,
  chemical_name varchar(400),
  molecular_formula varchar(100),
  molecular_weight numeric,
  virtual_compound_id varchar(50),
  registration_number varchar(50),
  cas_number varchar(20),
  struct_skth_frmt varchar(15),
  native_struct_skth_frmt varchar(15),
  struct_image_frmt varchar(15),
  user_hazard_comments varchar(4000),
  struct_comments varchar(200),
  stereoisomer_code varchar(10),
  compound_name varchar(400),
  boiling_pt_value numeric,
  boiling_pt_unit_code varchar(5),
  melting_pt_value numeric,
  melting_pt_unit_code varchar(5),
  created_by_notebook varchar(5),
  exact_mass numeric,
  compound_parent_id varchar(25),
  version numeric default 0,
  last_modified timestamp with time zone default current_timestamp,
  stoich_comments varchar(128),
  constraint cen_structures_pk primary key (struct_key),
  constraint cen_structures_fk1 foreign key (page_key) references cen_pages (page_key) on delete cascade
) ;
alter table cen_structures owner to indigo_owner ;

-- table: cen_temp_next_experiment

create table cen_temp_next_experiment
(
  notebook varchar(8) not null,
  next_experiment varchar(4) not null,
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_temp_next_experiment_uq unique (notebook, next_experiment)
) ;
alter table cen_temp_next_experiment owner to indigo_owner ;

-- table: cen_user_sessions

create table cen_user_sessions
(
  session_token varchar(128) not null,
  username varchar(64),
  lease_start_time date,
  last_modified timestamp with time zone default current_timestamp,
  version numeric default 0,
  constraint cen_user_sessions_pk primary key (session_token)
) ;
alter table cen_user_sessions owner to indigo_owner ;

-- triggers

-- trigger: cen_new_reg_job

create function cen_new_reg_job_proc() returns trigger language plpgsql as
$$
  begin
    new.job_id:=nextval('cen_reg_job_id');
    return new;
  end;
$$;
alter function cen_new_reg_job_proc() owner to indigo_owner ;

create trigger cen_new_reg_job
  before insert on cen_reg_jobs
  for each row
  execute procedure cen_new_reg_job_proc() ;

-- trigger: nbk_ref_version_populate_trig

create function nbk_ref_version_populate_trig_proc() returns trigger language plpgsql as
$$
  begin
    new.nbk_ref_version := new.notebook||'-'||new.experiment||'-'||new.page_version;
    return new;
  end;
$$;
alter function nbk_ref_version_populate_trig_proc() owner to indigo_owner ;

create trigger nbk_ref_version_populate_trig
  before insert on cen_pages
  for each row
  execute procedure nbk_ref_version_populate_trig_proc() ;

-- trigger: cen_log_seq_nextval_trig

create function cen_log_seq_nextval_trig_proc() returns trigger language plpgsql as
$$
  begin
    new.id := nextval('cen_log_seq');
    return new;
  end;
$$;
alter function cen_log_seq_nextval_trig_proc() owner to indigo_owner ;

create trigger cen_log_seq_nextval_trig
  before insert on cen_log
  for each row
  execute procedure cen_log_seq_nextval_trig_proc() ;

-- trigger: cen_metrics_seq_nextval_trig

create function cen_metrics_seq_nextval_trig_proc() returns trigger language plpgsql as
$$
  begin
    new.log_seq_number := nextval('cen_metrics_seq');
    return new;
  end;
$$;
alter function cen_metrics_seq_nextval_trig_proc() owner to indigo_owner ;

create trigger cen_metrics_seq_nextval_trig
  before insert on cen_metrics
  for each row
  execute procedure cen_metrics_seq_nextval_trig_proc() ;

-- fill xml_metadata

create or replace function indigo_eln_xml_import(p_oid integer, p_result out text) language plpgsql as
$$
declare
  l_oid oid;
  r record;
begin
  p_result := '';
  select cast(p_oid as oid) into l_oid;
  for r in ( select data from pg_largeobject where loid = l_oid order by pageno )
  loop
    p_result = p_result || convert_from(r.data, 'UTF8');
  end loop;
  perform lo_unlink(l_oid);
end;
$$;

-- fill cen_properties

insert into cen_properties (site_code, enabled_flag, xml_metadata, reagent_db_properties) values ('GBL', 'YES', '', '') ;
insert into cen_properties (site_code, enabled_flag, xml_metadata, reagent_db_properties) values ('SITE1', 'YES', '', '') ;

\lo_import 'xml/xml_metadata.xml';
update cen_properties set xml_metadata = indigo_eln_xml_import(:LASTOID) where site_code='GBL';
\lo_import 'xml/reagent_db_properties.xml';
update cen_properties set reagent_db_properties = indigo_eln_xml_import(:LASTOID) where site_code='GBL';

\lo_import 'xml/xml_metadata_site.xml';
update cen_properties set xml_metadata = indigo_eln_xml_import(:LASTOID) where site_code='SITE1';
\lo_import 'xml/reagent_db_properties_site.xml';
update cen_properties set reagent_db_properties = indigo_eln_xml_import(:LASTOID) where site_code='SITE1';

-- grants

-- grant to allow use bingo

grant usage on schema bingo to indigo_owner ;
grant select on bingo.bingo_config to indigo_owner ;
grant select on bingo.bingo_tau_config to indigo_owner ;

grant usage on schema bingo to indigo_user ;
grant select on bingo.bingo_config to indigo_user ;
grant select on bingo.bingo_tau_config to indigo_user ;

-- create bingo indexes

--create index bingo_eln_mol_idx on cen_structures using bingo_idx (native_struct_sketch bingo.bmolecule) ;
--create index bingo_eln_rxn_idx on cen_reaction_schemes using bingo_idx (native_rxn_sketch bingo.breaction) ;

-- grant to allow use indigo_owner

grant usage on schema indigo_owner to indigo_user ;
grant usage on all sequences in schema indigo_owner to indigo_user ;
grant execute on all functions in schema indigo_owner to indigo_user ;
grant select,insert,update,delete on all tables in schema indigo_owner to indigo_user ;

commit ;
