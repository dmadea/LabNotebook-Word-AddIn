-- --------------------------------------------------------------------------
-- TABLESPACES
-- --------------------------------------------------------------------------

create tablespace indigo_owner_data
    datafile 'indigo_owner_data.ora'
        size 100m autoextend on next 128k
            extent management local uniform size 128k ;

create tablespace indigo_owner_lob
    datafile 'indigo_owner_lob.ora'
        size 100m autoextend on next 128k
            extent management local uniform size 128k ;

-- --------------------------------------------------------------------------
-- SCHEMAS (Change passwords here)
-- --------------------------------------------------------------------------

create user indigo_owner
    identified by indigo_owner                        -- Change password here
        default tablespace indigo_owner_data
            temporary tablespace temp
                quota unlimited on indigo_owner_data
                quota unlimited on indigo_owner_lob ;

create user indigo_user
    identified by indigo_user                         -- Change password here
        default tablespace indigo_owner_data
        temporary tablespace temp
            quota unlimited on indigo_owner_data
            quota unlimited on indigo_owner_lob ;

-- --------------------------------------------------------------------------
-- ROLES
-- --------------------------------------------------------------------------

create role indigo_owner_select_any ;
create role indigo_owner_update_any ;

-- --------------------------------------------------------------------------
-- PRIVILEGES
-- --------------------------------------------------------------------------

grant connect to indigo_owner ;
grant resource to indigo_owner ;
grant ctxapp to indigo_owner ;
grant indigo_owner_SELECT_ANY to indigo_owner with admin option ;
grant indigo_owner_UPDATE_ANY to indigo_owner with admin option ;

grant unlimited tablespace to indigo_owner ;
grant create any context to indigo_owner ;
grant execute on dbms_session to indigo_owner ;
grant create any view to indigo_owner ;
grant create any synonym to indigo_owner ;
grant create session to indigo_owner ;

grant connect to indigo_user ;

grant resource to indigo_user ;
grant indigo_owner_SELECT_ANY to indigo_user ;
grant indigo_owner_UPDATE_ANY to indigo_user ;
grant create any synonym to indigo_user ;
grant create session to indigo_user ;

-- --------------------------------------------------------------------------
-- TABLES
-- --------------------------------------------------------------------------

-- users

create table indigo_owner.cen_users
(
    username varchar2(64),
    password varchar2(256),
    xml_metadata clob ,
    my_reagents clob ,
    fullname varchar2(256),
    email varchar2(256),
    site_code varchar2(16),
    status varchar2(16),
    privilege_list varchar2(1024),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    audit_log clob ,
    constraint cen_users_pk primary key (username)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- notebooks

create table indigo_owner.cen_notebooks
(
    site_code varchar2(16),
    username varchar2(50),
    notebook varchar2(8),
    status varchar2(15),
    xml_metadata clob ,
    id number(19,0),
    version number(19,0),
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_notebooks_pk primary key (site_code, username, notebook)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- pages

create table indigo_owner.cen_pages
(
    page_key varchar2(256),
    site_code varchar2(16),
    notebook varchar2(8),
    experiment varchar2(4),
    username varchar2(50),
    owner_username varchar2(50),
    look_n_feel varchar2(10),
    page_status varchar2(15),
    creation_date timestamp (6) with time zone,
    modified_date timestamp (6) with time zone,
    xml_metadata clob ,
    procedure clob default empty_clob(),
    page_version number(2,0),
    latest_version varchar2(1),
    pdf_document blob default empty_blob(),
    spid varchar2(32),
    ta_code varchar2(10),
    project_code varchar2(54),
    literature_ref varchar2(1800),
    subject varchar2(1000),
    series_id varchar2(15),
    protocol_id varchar2(25),
    migrated_to_pcen varchar2(2) default 'N',
    batch_owner varchar2(50),
    batch_creator varchar2(50),
    design_submitter varchar2(50),
    nbk_ref_version varchar2(16),
    version number(*,0) default 0,
    constraint cen_pages_pk primary key (page_key),
    constraint cen_pages_uk_2 unique (nbk_ref_version),
    constraint cen_pages_fk1 foreign key (site_code, username, notebook)
        references indigo_owner.cen_notebooks (site_code, username, notebook) enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (procedure) store as basicfile cen_pages_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (pdf_document) store as basicfile (
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- lists

create table indigo_owner.cen_lists
(
    list_key varchar2(256),
    list_name varchar2(50),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_lists_pk primary key (list_key)
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- batches

create table indigo_owner.cen_batches
(
    batch_key varchar2(256),
    page_key varchar2(256),
    batch_number varchar2(30),
    struct_key varchar2(256),
    xml_metadata clob ,
    step_key varchar2(256),
    batch_type varchar2(32),
    molecular_formula varchar2(100),
    theoritical_yield_percent number,
    salt_code varchar2(5),
    salt_equivs number,
    list_key varchar2(256),
    batch_mw_value number,
    batch_mw_unit_code varchar2(5),
    batch_mw_is_calc char(1) default 'Y',
    batch_mw_sig_digits number(*,0),
    batch_mw_sig_digits_set char(1) default 'Y',
    batch_mw_user_pref_figs number(*,0),
    is_limiting varchar2(1) default 'N',
    auto_calc varchar2(1),
    synthszd_by varchar2(50),
    added_solv_batch_key varchar2(256),
    no_of_times_used number(*,0),
    intd_addition_order number(*,0),
    chloracnegen_type varchar2(20),
    is_chloracnegen varchar2(1),
    tested_for_chloracnegen varchar2(1) default 'N',
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_batches_pk primary key (batch_key),
    constraint cen_batches_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable,
    constraint cen_batches_fk2 foreign key (list_key)
        references indigo_owner.cen_lists (list_key) enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- analysis

create table indigo_owner.cen_analysis
(
    analysis_key varchar2(256),
    page_key varchar2(256),
    xml_metadata clob ,
    blob_data blob default empty_blob(),
    cen_sample_ref varchar2(256),
    analytical_service_sample_ref varchar2(256),
    annotation varchar2(140),
    comments varchar2(200),
    site_code varchar2(16),
    cyber_lab_domain_id varchar2(38),
    cyber_lab_file_id varchar2(38),
    cyber_lab_folder_id varchar2(38),
    cyber_lab_lcdf_id varchar2(38),
    cyber_lab_user_id varchar2(30),
    domain varchar2(10),
    server varchar2(100),
    url varchar2(100),
    user_id varchar2(30),
    analytical_version number(*,0),
    instrument varchar2(34),
    instrument_type varchar2(20),
    file_name varchar2(500),
    file_size number,
    file_type varchar2(10),
    experiment_time varchar2(30),
    experiment varchar2(900),
    group_id varchar2(50),
    ip_related varchar2(1),
    is_linked varchar2(1),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_analysis_pk primary key (analysis_key),
    constraint cen_analysis_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (blob_data) store as basicfile cen_analysis_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- attahments

create table indigo_owner.cen_attachements
(
    attachement_key varchar2(256),
    page_key varchar2(256),
    xml_metadata clob ,
    blob_data blob default empty_blob(),
    date_modified varchar2(30),
    document_description varchar2(400),
    document_name varchar2(256),
    ip_related varchar2(1),
    original_file_name varchar2(512),
    document_size number(*,0),
    document_type varchar2(30),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_attachements_pk primary key (attachement_key),
    constraint cen_attachements_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (blob_data) store as basicfile cen_attachements_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- audit_trail

create table indigo_owner.cen_audit_trail
(
    table_name varchar2(50),
    record_primary_key varchar2(256),
    action varchar2(10),
    modified_by varchar2(50),
    modified_timestamp timestamp (6) with time zone default systimestamp
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- batch_amounts

create table indigo_owner.cen_batch_amounts
(
    batch_key varchar2(256),
    page_key varchar2(256),
    weight_value number,
    weight_unit_code varchar2(5),
    weight_is_calc char(1) default 'Y',
    weight_sig_digits number(*,0),
    weight_sig_digits_set char(1) default 'Y',
    weight_user_pref_figs number(*,0),
    volume_value number,
    volume_unit_code varchar2(5),
    volume_is_calc char(1) default 'Y',
    volume_sig_digits number(*,0),
    volume_sig_digits_set char(1) default 'Y',
    volume_user_pref_figs number(*,0),
    molarity_value number,
    molarity_unit_code varchar2(5),
    molarity_is_calc char(1) default 'Y',
    molarity_sig_digits number(*,0),
    molarity_sig_digits_set char(1) default 'Y',
    molarity_user_pref_figs number(*,0),
    mole_value number,
    mole_unit_code varchar2(5),
    mole_is_calc char(1) default 'Y',
    mole_sig_digits number(*,0),
    mole_sig_digits_set char(1) default 'Y',
    mole_user_pref_figs number(*,0),
    density_value number,
    density_unit_code varchar2(5),
    density_is_calc char(1) default 'Y',
    density_sig_digits number(*,0),
    density_sig_digits_set char(1) default 'Y',
    density_user_pref_figs number(*,0),
    purity_value number,
    purity_unit_code varchar2(5),
    purity_is_calc char(1) default 'Y',
    purity_sig_digits number(*,0),
    purity_sig_digits_set char(1) default 'Y',
    purity_user_pref_figs number(*,0),
    loading_value number,
    loading_unit_code varchar2(5),
    loading_is_calc char(1) default 'Y',
    loading_sig_digits number(*,0),
    loading_sig_digits_set char(1) default 'Y',
    loading_user_pref_figs number(*,0),
    rxnequivs_value number,
    rxnequivs_unit_code varchar2(5),
    rxnequivs_is_calc char(1) default 'Y',
    rxnequivs_sig_digits number(*,0),
    rxnequivs_sig_digits_set char(1) default 'Y',
    rxnequivs_user_pref_figs number(*,0),
    theo_wt_value number,
    theo_wt_unit_code varchar2(5),
    theo_wt_is_calc char(1) default 'Y',
    theo_wt_sig_digits number(*,0),
    theo_wt_sig_digits_set char(1) default 'Y',
    theo_wt_user_pref_figs number(*,0),
    theo_mole_value number,
    theo_mole_unit_code varchar2(5),
    theo_mole_is_calc char(1) default 'Y',
    theo_mole_sig_digits number(*,0),
    theo_mole_sig_digits_set char(1) default 'Y',
    theo_mole_user_pref_figs number(*,0),
    total_wt_value number,
    total_wt_unit_code varchar2(5),
    total_wt_is_calc char(1) default 'Y',
    total_wt_sig_digits number(*,0),
    total_wt_sig_digits_set char(1) default 'Y',
    total_wt_user_pref_figs number(*,0),
    total_vol_value number,
    total_vol_unit_code varchar2(5),
    total_vol_is_calc char(1) default 'Y',
    total_vol_sig_digits number(*,0),
    total_vol_sig_digits_set char(1) default 'Y',
    total_vol_user_pref_figs number(*,0),
    deliv_wt_value number,
    deliv_wt_unit_code varchar2(5),
    deliv_wt_is_calc char(1) default 'Y',
    deliv_wt_sig_digits number(*,0),
    deliv_wt_sig_digits_set char(1) default 'Y',
    deliv_wt_user_pref_figs number(*,0),
    deliv_vol_value number,
    deliv_vol_unit_code varchar2(5),
    deliv_vol_is_calc char(1) default 'Y',
    deliv_vol_sig_digits number(*,0),
    deliv_vol_sig_digits_set char(1) default 'Y',
    deliv_vol_user_pref_figs number(*,0),
    needed_mole_value number,
    needed_mole_unit_code varchar2(5),
    needed_mole_is_calc char(1) default 'Y',
    needed_mole_sig_digits number(*,0),
    needed_mole_sig_digits_set char(1) default 'Y',
    needed_mole_user_pref_figs number(*,0),
    ex_neded_mole_value number,
    ex_neded_mole_unit_code varchar2(5),
    ex_neded_mole_is_calc char(1) default 'Y',
    ex_neded_mole_sig_digits number(*,0),
    ex_neded_mole_sig_digits_set char(1) default 'Y',
    ex_neded_mole_user_pref_figs number(*,0),
    solute_wt_value number,
    solute_wt_unit_code varchar2(5),
    solute_wt_is_calc char(1) default 'Y',
    solute_wt_sig_digits number(*,0),
    solute_wt_sig_digits_set char(1) default 'Y',
    solute_wt_user_pref_figs number(*,0),
    prev_molar_value number,
    prev_molar_unit_code varchar2(5),
    prev_molar_is_calc char(1) default 'Y',
    prev_molar_sig_digits number(*,0),
    prev_molar_sig_digits_set char(1) default 'Y',
    prev_molar_user_pref_figs number(*,0),
    theo_yld_pcnt_value number,
    theo_yld_pcnt_unit_code varchar2(5),
    theo_yld_pcnt_is_calc char(1) default 'Y',
    theo_yld_pcnt_sig_digits number(*,0),
    theo_yld_pcnt_sig_digits_set char(1) default 'Y',
    theo_yld_pcnt_user_pref_figs number(*,0),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_batch_amounts_fk1 foreign key (batch_key)
        references indigo_owner.cen_batches (batch_key) on delete cascade enable,
    constraint cen_batch_amounts_fk2 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- container

create table indigo_owner.cen_container
(
    container_key varchar2(256),
    container_code varchar2(15) not null enable,
    creator_id varchar2(25) not null enable,
    container_name varchar2(60),
    is_user_defined varchar2(1) not null enable,
    x_positions number(5,0),
    y_positions number(5,0),
    major_axis varchar2(1),
    container_type varchar2(10),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    skip_well_positions varchar2(500),
    constraint cen_container_pk primary key (container_key),
    constraint cen_container_uk1 unique (creator_id, container_code),
    constraint cen_container_fk1 foreign key (creator_id)
        references indigo_owner.cen_users (username) enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- cro_pageinfo

create table indigo_owner.cen_cro_pageinfo
(
    page_key varchar2(256) not null enable,
    vendor_id varchar2(50) not null enable,
    vendor_display_name varchar2(256),
    vendor_chemist_id varchar2(50),
    vendor_chemist_display_name varchar2(256),
    vendor_application_source varchar2(50),
    request_id varchar2(25) not null enable,
    xml_metadata clob ,
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_cropageinfo_pk primary key (page_key),
    constraint cen_cropageinfo_fk foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- jobs

create table indigo_owner.cen_jobs
(
    job_id varchar2(256) not null enable,
    plate_key varchar2(256),
    compound_registration_status varchar2(256),
    modified_date timestamp (6) with time zone,
    compound_reg_submission_date timestamp (6) with time zone,
    compound_reg_status_message varchar2(256),
    compound_management_status varchar2(256),
    compound_mgmt_status_message varchar2(256),
    purification_service_status varchar2(256),
    pur_service_status_msg varchar2(256),
    compound_aggregation_status varchar2(256),
    cmpd_aggregation_status_msg varchar2(256),
    callback_url varchar2(256),
    page_key varchar2(256),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_jobs_pk primary key (job_id)
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- log

create table indigo_owner.cen_log
(
    id number,
    timestamp date,
    message clob,
    app_version number(19,0),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_log_pk primary key (id)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (message) store as (
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- metrics

create table indigo_owner.cen_metrics
(
    log_seq_number number,
    application_name varchar2(50),
    sub_function_name varchar2(50),
    when_used date,
    account_name varchar2(50),
    site_code varchar2(16),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_metrics_pk primary key (log_seq_number)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- reaction_steps

create table indigo_owner.cen_reaction_steps
(
    step_key varchar2(256),
    page_key varchar2(256),
    seq_num number(2,0) default 0,
    rxn_scheme_key varchar2(256),
    xml_metadata clob ,
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_reaction_steps_pk primary key (step_key),
    constraint cen_reaction_steps_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- plate

create table indigo_owner.cen_plate
(
    plate_key varchar2(256),
    plate_bar_code varchar2(12),
    cen_plate_type varchar2(15),
    page_key varchar2(256),
    step_key varchar2(256),
    plate_number varchar2(25),
    registered_date timestamp (6),
    parent_plate_key varchar2(256),
    container_key varchar2(256),
    comments varchar2(100),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_plate_pk primary key (plate_key),
    constraint cen_plate_fk2 foreign key (step_key)
        references indigo_owner.cen_reaction_steps (step_key) enable,
    constraint cen_plate_fk4 foreign key (parent_plate_key)
        references indigo_owner.cen_plate (plate_key) enable,
    constraint cen_plate_fk3 foreign key (container_key)
        references indigo_owner.cen_container (container_key) on delete cascade enable,
    constraint cen_plate_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- plate_well

create table indigo_owner.cen_plate_well
(
    well_key varchar2(256),
    plate_key varchar2(256),
    well_position number,
    well_type varchar2(10),
    batch_key varchar2(256),
    solvent_code varchar2(12),
    barcode varchar2(12),
    weight_value number,
    weight_unit_code varchar2(5),
    weight_is_calc char(1) default 'Y',
    weight_sig_digits number(*,0),
    weight_sig_digits_set char(1) default 'Y',
    weight_user_pref_figs number(*,0),
    volume_value number,
    volume_unit_code varchar2(5),
    volume_is_calc char(1) default 'Y',
    volume_sig_digits number(*,0),
    volume_sig_digits_set char(1) default 'Y',
    volume_user_pref_figs number(*,0),
    molarity_value number,
    molarity_unit_code varchar2(5),
    molarity_is_calc char(1) default 'Y',
    molarity_sig_digits number(*,0),
    molarity_sig_digits_set char(1) default 'Y',
    molarity_user_pref_figs number(*,0),
    version number(*,0) default 0 not null enable,
    well_number varchar2(5),
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_well_pk primary key (well_key),
    constraint cen_plate_well_fk1 foreign key (plate_key)
        references indigo_owner.cen_plate (plate_key) on delete cascade enable,
    constraint cen_plate_well_fk2 foreign key (batch_key)
        references indigo_owner.cen_batches (batch_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- procedure_images

create table indigo_owner.cen_procedure_images
(
    image_key varchar2(255) not null enable,
    page_key varchar2(256),
    image_type varchar2(255),
    image_data blob default empty_blob(),
    primary key (image_key),
    constraint cen_procedure_images_fk2 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (image_data) store as (
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    nocache
    storage(initial 131072 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- properties

create table indigo_owner.cen_properties
(
    site_code varchar2(16),
    enabled_flag varchar2(5),
    xml_metadata clob ,
    reagent_db_properties clob ,
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_properties_pk primary key (site_code)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- purification_service

create table indigo_owner.cen_purification_service
(
    purification_service_key varchar2(255) not null enable,
    modifiers varchar2(255),
    ph_value float(126),
    archive_plate varchar2(255),
    archive_volume float(126),
    sample_workup varchar2(255),
    inorganic_byproduct_salt char(1),
    seperate_isomers char(1),
    destination_lab varchar2(255),
    well_key varchar2(255),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_ap3_pk primary key (purification_service_key),
    constraint cen_ap3_fk foreign key (well_key)
        references indigo_owner.cen_plate_well (well_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- queries

create table indigo_owner.cen_queries
(
    id number,
    sub_id number,
    query_blob blob default empty_blob(),
    query_string varchar2(1000),
    load_date date,
    version number(19,0),
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_queries_pk primary key (id, sub_id)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (query_blob) store as cen_queries_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- reaction_schemes

create table indigo_owner.cen_reaction_schemes
(
    rxn_scheme_key varchar2(256),
    page_key varchar2(256),
    reaction_type varchar2(10),
    rxn_sketch blob default empty_blob(),
    native_rxn_sketch blob default empty_blob(),
    sketch_image blob default empty_blob(),
    xml_metadata clob ,
    vrxn_id varchar2(512),
    protocol_id varchar2(32),
    rxn_skth_frmt varchar2(15),
    native_rxn_skth_frmt varchar2(15),
    rxn_image_frmt varchar2(15),
    synth_route_ref varchar2(20),
    reaction_id varchar2(20),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    constraint cen_reaction_schemes_pk primary key (rxn_scheme_key),
    constraint cen_reaction_schemes_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (rxn_sketch) store as basicfile cen_reaction_schemes_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (native_rxn_sketch) store as basicfile cen_reaction_schemes_lob2(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (sketch_image) store as basicfile cen_reaction_schemes_lob3(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- reg_jobs

create table indigo_owner.cen_reg_jobs
(
    job_id number(*,0) not null enable,
    plate_key varchar2(64),
    compound_registration_status varchar2(25),
    modified_date timestamp (6) with time zone,
    compound_reg_submission_date timestamp (6) with time zone,
    compound_reg_status_message varchar2(2000),
    compound_management_status varchar2(25),
    compound_mgmt_status_message varchar2(2000),
    purification_service_status varchar2(25),
    pur_service_status_msg varchar2(2000),
    compound_aggregation_status varchar2(25),
    cmpd_aggregation_status_msg varchar2(2000),
    callback_url varchar2(256),
    page_key varchar2(256),
    compound_registration_job_id varchar2(20),
    batch_keys clob,
    plate_keys clob,
    workflow varchar2(100),
    status varchar2(16),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_reg_jobs_pk primary key (job_id),
    constraint cen_reg_jobs_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (batch_keys) store as (
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    nocache
    storage(initial 131072 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (plate_keys) store as (
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    nocache
    storage(initial 131072 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- registered_batches

create table indigo_owner.cen_registered_batches
(
    reg_batch_key varchar2(256),
    page_key varchar2(256),
    batch_key varchar2(256),
    conversational_batch_number varchar2(32),
    parent_batch_number varchar2(32),
    batch_tracking_id number,
    job_id number,
    registration_status varchar2(32),
    submission_status varchar2(32),
    status varchar2(32),
    xml_metadata clob ,
    compound_aggregation_status varchar2(256),
    cmpd_aggregation_status_msg varchar2(256),
    purification_service_status varchar2(256),
    pur_service_status_msg varchar2(256),
    compound_registration_offset number,
    compound_management_status varchar2(256),
    compound_mgmt_status_message varchar2(256),
    compound_reg_status_message varchar2(256),
    melt_point_val_lower number,
    melt_point_val_upper number,
    melt_point_comments varchar2(400),
    supplier_code varchar2(12),
    supplier_registry_number varchar2(70),
    source_code varchar2(10),
    source_detail_code varchar2(10),
    batch_comments varchar2(1400),
    compound_state varchar2(12),
    batch_hazard_comment varchar2(240),
    batch_handling_comment varchar2(200),
    batch_storage_comment varchar2(500),
    protection_code varchar2(5),
    continue_status varchar2(15),
    selectivity_status varchar2(15),
    hit_id varchar2(30),
    registration_date timestamp (6) with time zone,
    vnv_status varchar2(15),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    batch_owner varchar2(50),
    product_flag varchar2(1),
    intermediate_or_test varchar2(20) default 'U',
    constraint check_intermediate_or_test check (intermediate_or_test in ('Intermediate', 'Test Compound', 'U', 'N')) enable,
    constraint cen_registered_batches_pk primary key (reg_batch_key),
    constraint cen_registered_batches_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable,
    constraint cen_registered_batches_fk2 foreign key (batch_key)
        references indigo_owner.cen_batches (batch_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- step_batch_lists

create table indigo_owner.cen_step_batch_lists
(
    step_key varchar2(256),
    list_key varchar2(256),
    position varchar2(3),
    page_key varchar2(256),
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_step_batch_lists_fk1 foreign key (step_key)
        references indigo_owner.cen_reaction_steps (step_key) enable,
    constraint cen_step_batch_lists_fk2 foreign key (list_key)
        references indigo_owner.cen_lists (list_key) enable,
    constraint cen_step_batch_lists_fk3 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- structures

create table indigo_owner.cen_structures
(
    struct_key varchar2(256),
    page_key varchar2(256),
    struct_sketch blob default empty_blob(),
    native_struct_sketch blob default empty_blob(),
    struct_image blob default empty_blob(),
    xml_metadata clob ,
    chemical_name varchar2(400),
    molecular_formula varchar2(100),
    molecular_weight number,
    virtual_compound_id varchar2(50),
    registration_number varchar2(50),
    cas_number varchar2(20),
    struct_skth_frmt varchar2(15),
    native_struct_skth_frmt varchar2(15),
    struct_image_frmt varchar2(15),
    user_hazard_comments varchar2(4000),
    struct_comments varchar2(200),
    stereoisomer_code varchar2(10),
    compound_name varchar2(400),
    boiling_pt_value number,
    boiling_pt_unit_code varchar2(5),
    melting_pt_value number(*,0),
    melting_pt_unit_code varchar2(5),
    created_by_notebook varchar2(5),
    exact_mass number,
    compound_parent_id varchar2(25),
    version number(*,0) default 0,
    last_modified timestamp (6) with time zone default systimestamp,
    stoich_comments varchar2(100),
    constraint cen_structures_pk primary key (struct_key),
    constraint cen_structures_fk1 foreign key (page_key)
        references indigo_owner.cen_pages (page_key) on delete cascade enable
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
lob (struct_sketch) store as basicfile cen_structures_lob1(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (native_struct_sketch) store as basicfile cen_structures_lob2(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
lob (struct_image) store as basicfile cen_structures_lob3(
    tablespace indigo_owner_lob enable storage in row chunk 8192 pctversion 10
    cache
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default))
;

-- temp_next_experiment

create table indigo_owner.cen_temp_next_experiment
(
    notebook varchar2(8) not null enable,
    next_experiment varchar2(4) not null enable,
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_temp_next_experiment_u01 unique (notebook, next_experiment)
)
    pctfree 10 pctused 40 initrans 1 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- cen_user_sessions

create table indigo_owner.cen_user_sessions
(
    session_token varchar2(100),
    username varchar2(50),
    lease_start_time date,
    last_modified timestamp (6) with time zone default systimestamp,
    version number(*,0) default 0,
    constraint cen_user_sessions_pk primary key (session_token)
)
    pctfree 10 pctused 85 initrans 10 maxtrans 255 nocompress logging
    storage(initial 65536 next 131072 minextents 1 maxextents 2147483645
    pctincrease 0 freelists 1 freelist groups 1 buffer_pool default)
    tablespace indigo_owner_data
;

-- Bingo Indexes

--create index indigo_owner.bingo_eln_mol_idx on indigo_owner.cen_structures(native_struct_sketch) indextype is bingo.MoleculeIndex ;
--create index indigo_owner.bingo_eln_rxn_idx on indigo_owner.cen_reaction_schemes(native_rxn_sketch) indextype is bingo.ReactionIndex ;

-- --------------------------------------------------------------------------
-- SEQUENCES
-- --------------------------------------------------------------------------

create sequence indigo_owner.cen_log_seq ;
create sequence indigo_owner.cen_metrics_seq ;
create sequence indigo_owner.cen_queries_seq ;
create sequence indigo_owner.cen_reg_job_id ;
create sequence indigo_owner.hibernate_sequence ;
create sequence indigo_owner.reg_batch_key_seq ;

-- --------------------------------------------------------------------------
-- TRIGGERS
-- --------------------------------------------------------------------------

create or replace trigger indigo_owner.cen_log_seq_nextval_trig
  before insert on indigo_owner.cen_log
  for each row
declare
  temp_id integer;
begin
  select indigo_owner.cen_log_seq.nextval into temp_id from dual;
  :new.id:=temp_id;
end cen_log_seq_nextval_trig;
/

create or replace trigger indigo_owner.cen_metrics_seq_nextval_trig
  before insert on indigo_owner.cen_metrics
  for each row
declare
  temp_id integer;
begin
  select indigo_owner.cen_metrics_seq.nextval into temp_id from dual;
  :new.log_seq_number:=temp_id;
end cen_metrics_seq_nextval_trig;
/

create or replace trigger indigo_owner.cen_new_reg_job
  before insert on indigo_owner.cen_reg_jobs
  for each row
declare
  v_job_id integer;
begin
  select indigo_owner.cen_reg_job_id.nextval into v_job_id from dual;
  :new.job_id:=v_job_id;
end cen_new_reg_job;
/

create or replace trigger indigo_owner.nbk_ref_version_populate_trig
before insert
on indigo_owner.cen_pages referencing new as new old as old
for each row
declare
begin
   :new.nbk_ref_version := :new.notebook||'-'||:new.experiment||'-'||:new.page_version;
end nbk_ref_version_populate_trig;
/

-- --------------------------------------------------------------------------
-- GRANTS
-- --------------------------------------------------------------------------

define strSchemaOwner=indigo_owner
define strUser=indigo_user
define strSelectRole=indigo_owner_select_any
define strUpdateRole=indigo_owner_update_any

set head off
set verify off
set line 1000
set newp none
set feedback off
spool temp\tmp_dynamic_grant.sql
select 'GRANT &strSelectRole. TO &strUser;' S from dual
union
select 'GRANT &strUpdateRole. TO &strUser;' from dual
union
select 'GRANT SELECT ON &strSchemaOwner..'||table_name||' TO &strSelectRole.;'
from all_tables
where upper(owner)=upper('&strSchemaOwner')
union
select 'GRANT SELECT ON &strSchemaOwner..'||object_name||' TO &strSelectRole.;'
from all_objects
where object_type IN ('SEQUENCE','VIEW') and upper(owner)=upper('&strSchemaOwner')
union
select 'GRANT EXECUTE ON &strSchemaOwner..'||object_name||' TO &strUser.;'
from all_objects
where object_type = 'PACKAGE' and upper(owner)=upper('&strSchemaOwner')
/
spool off
spool temp\tmp_dynamic_grant_DML.sql
select 'GRANT DELETE ON &strSchemaOwner..'||table_name||' TO &strUpdateRole.;' S from all_tables where upper(owner)=upper('&strSchemaOwner')
union
select 'GRANT INSERT ON &strSchemaOwner..'||table_name||' TO &strUpdateRole.;' from all_tables where upper(owner)=upper('&strSchemaOwner')
union
select 'GRANT UPDATE ON &strSchemaOwner..'||table_name||' TO &strUpdateRole.;' from all_tables where upper(owner)=upper('&strSchemaOwner')
/
spool off
spool temp\tmp_dynamic_synonym.sql
select 'CREATE OR REPLACE SYNONYM &strUser..'||object_name||' FOR &strSchemaOwner..' || object_name ||';' S
from all_objects
where object_type in ('PACKAGE','TABLE','SEQUENCE','VIEW','TYPE') and upper(owner)=upper('&strSchemaOwner')
and object_name not like 'BIN$%'
/
spool off
set feedback on
set head on
set line 80
set newp 1

@temp\tmp_dynamic_grant.sql
@temp\tmp_dynamic_grant_DML.sql
@temp\tmp_dynamic_synonym.sql


-- --------------------------------------------------------------------------
-- COMMIT
-- --------------------------------------------------------------------------

COMMIT;
