# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table ix_core_acl (
  id                        bigint not null,
  perm                      integer,
  constraint ck_ix_core_acl_perm check (perm in (0,1,2,3,4,5,6)),
  constraint pk_ix_core_acl primary key (id))
;

create table ix_ncats_clinical_arm (
  id                        bigint not null,
  label                     varchar(255),
  description               clob,
  type                      varchar(255),
  constraint pk_ix_ncats_clinical_arm primary key (id))
;

create table ix_core_attribute (
  id                        bigint not null,
  name                      varchar(255),
  value                     varchar(1024),
  namespace_id              bigint,
  constraint pk_ix_core_attribute primary key (id))
;

create table ix_ncats_clinical_trial (
  id                        bigint not null,
  nct_id                    varchar(15),
  url                       varchar(1024),
  title                     clob,
  official_title            clob,
  summary                   clob,
  description               clob,
  sponsor                   varchar(1024),
  study_type                varchar(255),
  study_design              varchar(255),
  start_date                timestamp,
  completion_date           timestamp,
  first_received_date       timestamp,
  last_changed_date         timestamp,
  verification_date         timestamp,
  first_received_results_date timestamp,
  has_results               boolean,
  status                    varchar(255),
  phase                     varchar(255),
  eligibility_id            bigint,
  constraint uq_ix_ncats_clinical_trial_nct_i unique (nct_id),
  constraint pk_ix_ncats_clinical_trial primary key (id))
;

create table ix_ncats_clinical_cohort (
  id                        bigint not null,
  label                     varchar(255),
  description               clob,
  constraint pk_ix_ncats_clinical_cohort primary key (id))
;

create table ix_ncats_clinical_condition (
  id                        bigint not null,
  name                      varchar(1024),
  is_rare_disease           boolean,
  constraint pk_ix_ncats_clinical_condition primary key (id))
;

create table ix_core_curation (
  id                        bigint not null,
  curator_id                bigint,
  status                    integer,
  timestamp                 timestamp,
  constraint ck_ix_core_curation_status check (status in (0,1,2,3)),
  constraint pk_ix_core_curation primary key (id))
;

create table ix_core_etag (
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  etag                      varchar(16),
  uri                       varchar(4000),
  path                      varchar(255),
  method                    varchar(10),
  sha1                      varchar(40),
  total                     integer,
  count                     integer,
  skip                      integer,
  top                       integer,
  status                    integer,
  query                     varchar(2048),
  filter                    varchar(4000),
  version                   bigint not null,
  constraint uq_ix_core_etag_etag unique (etag),
  constraint pk_ix_core_etag primary key (id))
;

create table ix_core_etagref (
  id                        bigint not null,
  etag_id                   bigint,
  ref_id                    bigint,
  constraint pk_ix_core_etagref primary key (id))
;

create table ix_core_edit (
  id                        varchar(40) not null,
  created                   timestamp,
  refid                     varchar(255),
  kind                      varchar(255),
  editor_id                 bigint,
  path                      varchar(1024),
  comments                  clob,
  old_value                 clob,
  new_value                 clob,
  constraint pk_ix_core_edit primary key (id))
;

create table ix_ncats_clinical_eligibility (
  id                        bigint not null,
  gender                    varchar(32),
  min_age                   varchar(255),
  max_age                   varchar(255),
  healthy_volunteers        boolean,
  criteria                  clob,
  constraint pk_ix_ncats_clinical_eligibility primary key (id))
;

create table ix_core_event (
  id                        bigint not null,
  title                     varchar(1024),
  description               clob,
  url                       varchar(1024),
  event_start               timestamp,
  event_end                 timestamp,
  is_duration               boolean,
  constraint pk_ix_core_event primary key (id))
;

create table ix_core_figure (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  caption                   varchar(255),
  mime_type                 varchar(255),
  url                       varchar(1024),
  data                      blob,
  data_size                 integer,
  sha1                      varchar(140),
  parent_id                 bigint,
  constraint pk_ix_core_figure primary key (id))
;

create table ix_ncats_funding (
  id                        bigint not null,
  grant_id                  bigint not null,
  ic                        varchar(255),
  amount                    integer,
  constraint pk_ix_ncats_funding primary key (id))
;

create table ix_ncats_grant (
  id                        bigint not null,
  application_id            bigint,
  activity                  varchar(255),
  administering_ic          varchar(255),
  application_type          integer,
  is_arra_funded            boolean,
  award_notice_date         timestamp,
  budget_start              timestamp,
  budget_end                timestamp,
  cfda_code                 integer,
  foa_number                varchar(255),
  full_project_num          varchar(255),
  subproject_id             bigint,
  fiscal_year               integer,
  ic_name                   varchar(255),
  ed_inst_type              varchar(255),
  nih_spending_cats         varchar(255),
  program_officer_name      varchar(255),
  project_start             timestamp,
  project_end               timestamp,
  core_project_num          varchar(255),
  project_title             varchar(255),
  public_health_relevance   clob,
  serial_number             bigint,
  study_section             varchar(255),
  study_section_name        varchar(255),
  suffix                    varchar(255),
  funding_mechanism         varchar(255),
  total_cost                integer,
  total_cost_subproject     integer,
  project_abstract          clob,
  constraint uq_ix_ncats_grant_application_id unique (application_id),
  constraint pk_ix_ncats_grant primary key (id))
;

create table ix_core_group (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_ix_core_group_name unique (name),
  constraint pk_ix_core_group primary key (id))
;

create table ix_ncats_clinical_intervention (
  id                        bigint not null,
  name                      varchar(255),
  description               clob,
  type                      varchar(255),
  constraint pk_ix_ncats_clinical_interventio primary key (id))
;

create table ix_core_investigator (
  id                        bigint not null,
  name                      varchar(255),
  pi_id                     bigint,
  organization_id           bigint,
  role                      integer,
  constraint ck_ix_core_investigator_role check (role in (0,1)),
  constraint pk_ix_core_investigator primary key (id))
;

create table ix_core_journal (
  id                        bigint not null,
  issn                      varchar(10),
  volume                    varchar(255),
  issue                     varchar(255),
  year                      integer,
  month                     varchar(10),
  title                     varchar(1024),
  iso_abbr                  varchar(255),
  factor                    double,
  constraint pk_ix_core_journal primary key (id))
;

create table ix_core_namespace (
  id                        bigint not null,
  name                      varchar(255),
  owner_id                  bigint,
  location                  varchar(1024),
  modifier                  integer,
  constraint ck_ix_core_namespace_modifier check (modifier in (0,1,2)),
  constraint uq_ix_core_namespace_name unique (name),
  constraint pk_ix_core_namespace primary key (id))
;

create table ix_core_organization (
  id                        bigint not null,
  duns                      varchar(10),
  name                      varchar(255),
  department                varchar(255),
  city                      varchar(255),
  state                     varchar(128),
  zipcode                   varchar(64),
  district                  varchar(255),
  country                   varchar(255),
  fips                      varchar(3),
  longitude                 double,
  latitude                  double,
  constraint pk_ix_core_organization primary key (id))
;

create table ix_ncats_clinical_outcome (
  id                        bigint not null,
  type                      integer,
  measure                   varchar(255),
  timeframe                 varchar(255),
  description               clob,
  safety_issue              boolean,
  constraint ck_ix_ncats_clinical_outcome_type check (type in (0,1,2,3)),
  constraint pk_ix_ncats_clinical_outcome primary key (id))
;

create table ix_core_payload (
  id                        varchar(40) not null,
  namespace_id              bigint,
  created                   timestamp,
  name                      varchar(1024),
  sha1                      varchar(40),
  mime_type                 varchar(128),
  capacity                  bigint,
  constraint pk_ix_core_payload primary key (id))
;

create table ix_core_predicate (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  subject_id                bigint,
  predicate                 varchar(255) not null,
  version                   bigint not null,
  constraint pk_ix_core_predicate primary key (id))
;

create table ix_core_principal (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  provider                  varchar(255),
  username                  varchar(255),
  email                     varchar(255),
  admin                     boolean,
  uri                       varchar(1024),
  selfie_id                 bigint,
  version                   bigint not null,
  lastname                  varchar(255),
  forename                  varchar(255),
  initials                  varchar(255),
  prefname                  varchar(255),
  suffix                    varchar(20),
  affiliation               clob,
  orcid                     varchar(255),
  institution_id            bigint,
  ncats_employee            boolean,
  dn                        varchar(1024),
  uid                       bigint,
  phone                     varchar(32),
  biography                 clob,
  title                     varchar(255),
  research                  clob,
  is_lead                   boolean,
  dept                      integer,
  role                      integer,
  constraint ck_ix_core_principal_dept check (dept in (0,1,2,3)),
  constraint ck_ix_core_principal_role check (role in (0,1,2,3,4)),
  constraint pk_ix_core_principal primary key (id))
;

create table ix_core_procjob (
  id                        bigint not null,
  jobkey                    varchar(255) not null,
  invoker                   varchar(255),
  status                    integer,
  job_start                 bigint,
  job_stop                  bigint,
  message                   clob,
  owner_id                  bigint,
  payload_id                varchar(40),
  constraint ck_ix_core_procjob_status check (status in (0,1,2,3,4,5,6)),
  constraint uq_ix_core_procjob_jobkey unique (jobkey),
  constraint pk_ix_core_procjob primary key (id))
;

create table ix_core_procrecord (
  id                        bigint not null,
  rec_start                 bigint,
  rec_stop                  bigint,
  name                      varchar(128),
  status                    integer,
  message                   clob,
  xref_id                   bigint,
  job_id                    bigint,
  constraint ck_ix_core_procrecord_status check (status in (0,1,2,3)),
  constraint pk_ix_core_procrecord primary key (id))
;

create table ix_core_pubauthor (
  id                        bigint not null,
  position                  integer,
  is_last                   boolean,
  correspondence            boolean,
  author_id                 bigint,
  constraint pk_ix_core_pubauthor primary key (id))
;

create table ix_core_publication (
  id                        bigint not null,
  pmid                      bigint,
  pmcid                     varchar(255),
  title                     clob,
  pages                     varchar(255),
  doi                       varchar(255),
  abstract_text             clob,
  journal_id                bigint,
  constraint uq_ix_core_publication_pmid unique (pmid),
  constraint uq_ix_core_publication_pmcid unique (pmcid),
  constraint pk_ix_core_publication primary key (id))
;

create table ix_ncats_hcs_reagent (
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  name                      varchar(255),
  barcode                   varchar(255),
  celltype                  integer,
  apptype                   varchar(255),
  color                     varchar(255),
  application               varchar(255),
  excitation                integer,
  emission                  integer,
  version                   bigint not null,
  constraint ck_ix_ncats_hcs_reagent_celltype check (celltype in (0,1,2)),
  constraint pk_ix_ncats_hcs_reagent primary key (id))
;

create table ix_core_role (
  id                        bigint not null,
  role                      integer,
  principal_id              bigint,
  constraint ck_ix_core_role_role check (role in (0,1,2,3)),
  constraint pk_ix_core_role primary key (id))
;

create table ix_core_stitch (
  id                        bigint not null,
  name                      varchar(255),
  impl                      varchar(1024),
  description               clob,
  constraint pk_ix_core_stitch primary key (id))
;

create table ix_core_structure (
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  digest                    varchar(128),
  molfile                   clob,
  smiles                    clob,
  formula                   varchar(255),
  stereo                    integer,
  optical                   integer,
  atropi                    integer,
  stereo_comments           clob,
  stereo_centers            integer,
  defined_stereo            integer,
  ez_centers                integer,
  charge                    integer,
  mwt                       double,
  version                   bigint not null,
  constraint ck_ix_core_structure_stereo check (stereo in (0,1,2,3,4,5)),
  constraint ck_ix_core_structure_optical check (optical in (0,1,2,3,4)),
  constraint ck_ix_core_structure_atropi check (atropi in (0,1,2)),
  constraint pk_ix_core_structure primary key (id))
;

create table ix_core_value (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  label                     varchar(255),
  text                      clob,
  lval                      double,
  rval                      double,
  average                   double,
  numval                    double,
  unit                      varchar(255),
  data                      blob,
  data_size                 integer,
  sha1                      varchar(40),
  mime_type                 varchar(32),
  term                      varchar(255),
  href                      clob,
  strval                    varchar(1024),
  intval                    bigint,
  major_topic               boolean,
  heading                   varchar(1024),
  constraint pk_ix_core_value primary key (id))
;

create table ix_core_xref (
  id                        bigint not null,
  namespace_id              bigint,
  created                   timestamp,
  modified                  timestamp,
  deprecated                boolean,
  refid                     varchar(40) not null,
  kind                      varchar(255) not null,
  version                   bigint not null,
  constraint pk_ix_core_xref primary key (id))
;


create table ix_core_acl_principal (
  ix_core_acl_id                 bigint not null,
  ix_core_principal_id           bigint not null,
  constraint pk_ix_core_acl_principal primary key (ix_core_acl_id, ix_core_principal_id))
;

create table ix_core_acl_group (
  ix_core_acl_id                 bigint not null,
  ix_core_group_id               bigint not null,
  constraint pk_ix_core_acl_group primary key (ix_core_acl_id, ix_core_group_id))
;

create table ix_ncats_clinical_trial_keyword (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_ncats_clinical_trial_keyword primary key (ix_ncats_clinical_trial_id, ix_core_value_id))
;

create table ix_ncats_clinical_trial_sponsor (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_core_organization_id        bigint not null,
  constraint pk_ix_ncats_clinical_trial_sponsor primary key (ix_ncats_clinical_trial_id, ix_core_organization_id))
;

create table ix_ncats_clinical_trial_intervention (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_ncats_clinical_intervention_id bigint not null,
  constraint pk_ix_ncats_clinical_trial_intervention primary key (ix_ncats_clinical_trial_id, ix_ncats_clinical_intervention_id))
;

create table ix_ncats_clinical_trial_condition (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_ncats_clinical_condition_id bigint not null,
  constraint pk_ix_ncats_clinical_trial_condition primary key (ix_ncats_clinical_trial_id, ix_ncats_clinical_condition_id))
;

create table ix_ncats_clinical_trial_outcome (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_ncats_clinical_outcome_id   bigint not null,
  constraint pk_ix_ncats_clinical_trial_outcome primary key (ix_ncats_clinical_trial_id, ix_ncats_clinical_outcome_id))
;

create table ix_ncats_clincial_trial_location (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_core_organization_id        bigint not null,
  constraint pk_ix_ncats_clincial_trial_location primary key (ix_ncats_clinical_trial_id, ix_core_organization_id))
;

create table ix_ncats_clincial_trial_publication (
  ix_ncats_clinical_trial_id     bigint not null,
  ix_core_publication_id         bigint not null,
  constraint pk_ix_ncats_clincial_trial_publication primary key (ix_ncats_clinical_trial_id, ix_core_publication_id))
;

create table _ix_ncats_cca46885_1 (
  ix_ncats_clinical_condition_synonym_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_cca46885_1 primary key (ix_ncats_clinical_condition_synonym_id, ix_core_value_id))
;

create table _ix_ncats_cca46885_2 (
  ix_ncats_clinical_condition_keyword_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_cca46885_2 primary key (ix_ncats_clinical_condition_keyword_id, ix_core_value_id))
;

create table _ix_ncats_cca46885_3 (
  ix_ncats_clinical_condition_wikipedia_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_cca46885_3 primary key (ix_ncats_clinical_condition_wikipedia_id, ix_core_value_id))
;

create table _ix_ncats_840372f9_1 (
  ix_ncats_clinical_eligibility_inclusion_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_840372f9_1 primary key (ix_ncats_clinical_eligibility_inclusion_id, ix_core_value_id))
;

create table _ix_ncats_840372f9_2 (
  ix_ncats_clinical_eligibility_exclusion_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_840372f9_2 primary key (ix_ncats_clinical_eligibility_exclusion_id, ix_core_value_id))
;

create table ix_core_event_figure (
  ix_core_event_id               bigint not null,
  ix_core_figure_id              bigint not null,
  constraint pk_ix_core_event_figure primary key (ix_core_event_id, ix_core_figure_id))
;

create table ix_ncats_grant_investigator (
  ix_ncats_grant_id              bigint not null,
  ix_core_investigator_id        bigint not null,
  constraint pk_ix_ncats_grant_investigator primary key (ix_ncats_grant_id, ix_core_investigator_id))
;

create table ix_ncats_grant_keyword (
  ix_ncats_grant_id              bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_ncats_grant_keyword primary key (ix_ncats_grant_id, ix_core_value_id))
;

create table ix_ncats_grant_publication (
  ix_ncats_grant_id              bigint not null,
  ix_core_publication_id         bigint not null,
  constraint pk_ix_ncats_grant_publication primary key (ix_ncats_grant_id, ix_core_publication_id))
;

create table ix_core_group_principal (
  ix_core_group_id               bigint not null,
  ix_core_principal_id           bigint not null,
  constraint pk_ix_core_group_principal primary key (ix_core_group_id, ix_core_principal_id))
;

create table _ix_ncats_4a162ae3_1 (
  ix_ncats_clinical_intervention_id bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk__ix_ncats_4a162ae3_1 primary key (ix_ncats_clinical_intervention_id, ix_core_value_id))
;

create table _ix_ncats_4a162ae3_2 (
  ix_ncats_clinical_intervention_id bigint not null,
  ix_ncats_clinical_arm_id       bigint not null,
  constraint pk__ix_ncats_4a162ae3_2 primary key (ix_ncats_clinical_intervention_id, ix_ncats_clinical_arm_id))
;

create table _ix_ncats_4a162ae3_3 (
  ix_ncats_clinical_intervention_id bigint not null,
  ix_ncats_clinical_cohort_id    bigint not null,
  constraint pk__ix_ncats_4a162ae3_3 primary key (ix_ncats_clinical_intervention_id, ix_ncats_clinical_cohort_id))
;

create table ix_core_payload_property (
  ix_core_payload_id             varchar(40) not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_payload_property primary key (ix_core_payload_id, ix_core_value_id))
;

create table ix_core_predicate_object (
  ix_core_predicate_id           bigint not null,
  ix_core_xref_id                bigint not null,
  constraint pk_ix_core_predicate_object primary key (ix_core_predicate_id, ix_core_xref_id))
;

create table ix_core_predicate_property (
  ix_core_predicate_id           bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_predicate_property primary key (ix_core_predicate_id, ix_core_value_id))
;

create table ix_core_publication_keyword (
  ix_core_publication_id         bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_publication_keyword primary key (ix_core_publication_id, ix_core_value_id))
;

create table ix_core_publication_mesh (
  ix_core_publication_id         bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_publication_mesh primary key (ix_core_publication_id, ix_core_value_id))
;

create table ix_core_publication_author (
  ix_core_publication_id         bigint not null,
  ix_core_pubauthor_id           bigint not null,
  constraint pk_ix_core_publication_author primary key (ix_core_publication_id, ix_core_pubauthor_id))
;

create table ix_core_publication_figure (
  ix_core_publication_id         bigint not null,
  ix_core_figure_id              bigint not null,
  constraint pk_ix_core_publication_figure primary key (ix_core_publication_id, ix_core_figure_id))
;

create table ix_ncats_hcs_reagent_tag (
  ix_ncats_hcs_reagent_id        bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_ncats_hcs_reagent_tag primary key (ix_ncats_hcs_reagent_id, ix_core_value_id))
;

create table ix_ncats_hcs_reagent_property (
  ix_ncats_hcs_reagent_id        bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_ncats_hcs_reagent_property primary key (ix_ncats_hcs_reagent_id, ix_core_value_id))
;

create table ix_core_stitch_attribute (
  ix_core_stitch_id              bigint not null,
  ix_core_attribute_id           bigint not null,
  constraint pk_ix_core_stitch_attribute primary key (ix_core_stitch_id, ix_core_attribute_id))
;

create table ix_core_structure_property (
  ix_core_structure_id           bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_structure_property primary key (ix_core_structure_id, ix_core_value_id))
;

create table ix_core_structure_link (
  ix_core_structure_id           bigint not null,
  ix_core_xref_id                bigint not null,
  constraint pk_ix_core_structure_link primary key (ix_core_structure_id, ix_core_xref_id))
;

create table ix_core_xref_property (
  ix_core_xref_id                bigint not null,
  ix_core_value_id               bigint not null,
  constraint pk_ix_core_xref_property primary key (ix_core_xref_id, ix_core_value_id))
;
create sequence ix_core_acl_seq;

create sequence ix_ncats_clinical_arm_seq;

create sequence ix_core_attribute_seq;

create sequence ix_ncats_clinical_trial_seq;

create sequence ix_ncats_clinical_cohort_seq;

create sequence ix_ncats_clinical_condition_seq;

create sequence ix_core_curation_seq;

create sequence ix_core_etag_seq;

create sequence ix_core_etagref_seq;

create sequence ix_ncats_clinical_eligibility_seq;

create sequence ix_core_event_seq;

create sequence ix_core_figure_seq;

create sequence ix_ncats_funding_seq;

create sequence ix_ncats_grant_seq;

create sequence ix_core_group_seq;

create sequence ix_ncats_clinical_intervention_seq;

create sequence ix_core_investigator_seq;

create sequence ix_core_journal_seq;

create sequence ix_core_namespace_seq;

create sequence ix_core_organization_seq;

create sequence ix_ncats_clinical_outcome_seq;

create sequence ix_core_predicate_seq;

create sequence ix_core_principal_seq;

create sequence ix_core_procjob_seq;

create sequence ix_core_procrecord_seq;

create sequence ix_core_pubauthor_seq;

create sequence ix_core_publication_seq;

create sequence ix_ncats_hcs_reagent_seq;

create sequence ix_core_role_seq;

create sequence ix_core_stitch_seq;

create sequence ix_core_structure_seq;

create sequence ix_core_value_seq;

create sequence ix_core_xref_seq;

alter table ix_core_attribute add constraint fk_ix_core_attribute_namespace_1 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_attribute_namespace_1 on ix_core_attribute (namespace_id);
alter table ix_ncats_clinical_trial add constraint fk_ix_ncats_clinical_trial_eli_2 foreign key (eligibility_id) references ix_ncats_clinical_eligibility (id) on delete restrict on update restrict;
create index ix_ix_ncats_clinical_trial_eli_2 on ix_ncats_clinical_trial (eligibility_id);
alter table ix_core_curation add constraint fk_ix_core_curation_curator_3 foreign key (curator_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_curation_curator_3 on ix_core_curation (curator_id);
alter table ix_core_etag add constraint fk_ix_core_etag_namespace_4 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_etag_namespace_4 on ix_core_etag (namespace_id);
alter table ix_core_etagref add constraint fk_ix_core_etagref_etag_5 foreign key (etag_id) references ix_core_etag (id) on delete restrict on update restrict;
create index ix_ix_core_etagref_etag_5 on ix_core_etagref (etag_id);
alter table ix_core_edit add constraint fk_ix_core_edit_editor_6 foreign key (editor_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_edit_editor_6 on ix_core_edit (editor_id);
alter table ix_core_figure add constraint fk_ix_core_figure_parent_7 foreign key (parent_id) references ix_core_figure (id) on delete restrict on update restrict;
create index ix_ix_core_figure_parent_7 on ix_core_figure (parent_id);
alter table ix_ncats_funding add constraint fk_ix_ncats_funding_ix_ncats_g_8 foreign key (grant_id) references ix_ncats_grant (id) on delete restrict on update restrict;
create index ix_ix_ncats_funding_ix_ncats_g_8 on ix_ncats_funding (grant_id);
alter table ix_core_investigator add constraint fk_ix_core_investigator_organi_9 foreign key (organization_id) references ix_core_organization (id) on delete restrict on update restrict;
create index ix_ix_core_investigator_organi_9 on ix_core_investigator (organization_id);
alter table ix_core_namespace add constraint fk_ix_core_namespace_owner_10 foreign key (owner_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_namespace_owner_10 on ix_core_namespace (owner_id);
alter table ix_core_payload add constraint fk_ix_core_payload_namespace_11 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_payload_namespace_11 on ix_core_payload (namespace_id);
alter table ix_core_predicate add constraint fk_ix_core_predicate_namespac_12 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_predicate_namespac_12 on ix_core_predicate (namespace_id);
alter table ix_core_predicate add constraint fk_ix_core_predicate_subject_13 foreign key (subject_id) references ix_core_xref (id) on delete restrict on update restrict;
create index ix_ix_core_predicate_subject_13 on ix_core_predicate (subject_id);
alter table ix_core_principal add constraint fk_ix_core_principal_namespac_14 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_principal_namespac_14 on ix_core_principal (namespace_id);
alter table ix_core_principal add constraint fk_ix_core_principal_selfie_15 foreign key (selfie_id) references ix_core_figure (id) on delete restrict on update restrict;
create index ix_ix_core_principal_selfie_15 on ix_core_principal (selfie_id);
alter table ix_core_principal add constraint fk_ix_core_principal_institut_16 foreign key (institution_id) references ix_core_organization (id) on delete restrict on update restrict;
create index ix_ix_core_principal_institut_16 on ix_core_principal (institution_id);
alter table ix_core_procjob add constraint fk_ix_core_procjob_owner_17 foreign key (owner_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_procjob_owner_17 on ix_core_procjob (owner_id);
alter table ix_core_procjob add constraint fk_ix_core_procjob_payload_18 foreign key (payload_id) references ix_core_payload (id) on delete restrict on update restrict;
create index ix_ix_core_procjob_payload_18 on ix_core_procjob (payload_id);
alter table ix_core_procrecord add constraint fk_ix_core_procrecord_xref_19 foreign key (xref_id) references ix_core_xref (id) on delete restrict on update restrict;
create index ix_ix_core_procrecord_xref_19 on ix_core_procrecord (xref_id);
alter table ix_core_procrecord add constraint fk_ix_core_procrecord_job_20 foreign key (job_id) references ix_core_procjob (id) on delete restrict on update restrict;
create index ix_ix_core_procrecord_job_20 on ix_core_procrecord (job_id);
alter table ix_core_pubauthor add constraint fk_ix_core_pubauthor_author_21 foreign key (author_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_pubauthor_author_21 on ix_core_pubauthor (author_id);
alter table ix_core_publication add constraint fk_ix_core_publication_journa_22 foreign key (journal_id) references ix_core_journal (id) on delete restrict on update restrict;
create index ix_ix_core_publication_journa_22 on ix_core_publication (journal_id);
alter table ix_ncats_hcs_reagent add constraint fk_ix_ncats_hcs_reagent_names_23 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_ncats_hcs_reagent_names_23 on ix_ncats_hcs_reagent (namespace_id);
alter table ix_core_role add constraint fk_ix_core_role_principal_24 foreign key (principal_id) references ix_core_principal (id) on delete restrict on update restrict;
create index ix_ix_core_role_principal_24 on ix_core_role (principal_id);
alter table ix_core_structure add constraint fk_ix_core_structure_namespac_25 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_structure_namespac_25 on ix_core_structure (namespace_id);
alter table ix_core_xref add constraint fk_ix_core_xref_namespace_26 foreign key (namespace_id) references ix_core_namespace (id) on delete restrict on update restrict;
create index ix_ix_core_xref_namespace_26 on ix_core_xref (namespace_id);



alter table ix_core_acl_principal add constraint fk_ix_core_acl_principal_ix_c_01 foreign key (ix_core_acl_id) references ix_core_acl (id) on delete restrict on update restrict;

alter table ix_core_acl_principal add constraint fk_ix_core_acl_principal_ix_c_02 foreign key (ix_core_principal_id) references ix_core_principal (id) on delete restrict on update restrict;

alter table ix_core_acl_group add constraint fk_ix_core_acl_group_ix_core__01 foreign key (ix_core_acl_id) references ix_core_acl (id) on delete restrict on update restrict;

alter table ix_core_acl_group add constraint fk_ix_core_acl_group_ix_core__02 foreign key (ix_core_group_id) references ix_core_group (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_keyword add constraint fk_ix_ncats_clinical_trial_ke_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_keyword add constraint fk_ix_ncats_clinical_trial_ke_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_sponsor add constraint fk_ix_ncats_clinical_trial_sp_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_sponsor add constraint fk_ix_ncats_clinical_trial_sp_02 foreign key (ix_core_organization_id) references ix_core_organization (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_intervention add constraint fk_ix_ncats_clinical_trial_in_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_intervention add constraint fk_ix_ncats_clinical_trial_in_02 foreign key (ix_ncats_clinical_intervention_id) references ix_ncats_clinical_intervention (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_condition add constraint fk_ix_ncats_clinical_trial_co_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_condition add constraint fk_ix_ncats_clinical_trial_co_02 foreign key (ix_ncats_clinical_condition_id) references ix_ncats_clinical_condition (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_outcome add constraint fk_ix_ncats_clinical_trial_ou_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clinical_trial_outcome add constraint fk_ix_ncats_clinical_trial_ou_02 foreign key (ix_ncats_clinical_outcome_id) references ix_ncats_clinical_outcome (id) on delete restrict on update restrict;

alter table ix_ncats_clincial_trial_location add constraint fk_ix_ncats_clincial_trial_lo_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clincial_trial_location add constraint fk_ix_ncats_clincial_trial_lo_02 foreign key (ix_core_organization_id) references ix_core_organization (id) on delete restrict on update restrict;

alter table ix_ncats_clincial_trial_publication add constraint fk_ix_ncats_clincial_trial_pu_01 foreign key (ix_ncats_clinical_trial_id) references ix_ncats_clinical_trial (id) on delete restrict on update restrict;

alter table ix_ncats_clincial_trial_publication add constraint fk_ix_ncats_clincial_trial_pu_02 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_1 add constraint fk__ix_ncats_cca46885_1_ix_nc_01 foreign key (ix_ncats_clinical_condition_synonym_id) references ix_ncats_clinical_condition (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_1 add constraint fk__ix_ncats_cca46885_1_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_2 add constraint fk__ix_ncats_cca46885_2_ix_nc_01 foreign key (ix_ncats_clinical_condition_keyword_id) references ix_ncats_clinical_condition (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_2 add constraint fk__ix_ncats_cca46885_2_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_3 add constraint fk__ix_ncats_cca46885_3_ix_nc_01 foreign key (ix_ncats_clinical_condition_wikipedia_id) references ix_ncats_clinical_condition (id) on delete restrict on update restrict;

alter table _ix_ncats_cca46885_3 add constraint fk__ix_ncats_cca46885_3_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table _ix_ncats_840372f9_1 add constraint fk__ix_ncats_840372f9_1_ix_nc_01 foreign key (ix_ncats_clinical_eligibility_inclusion_id) references ix_ncats_clinical_eligibility (id) on delete restrict on update restrict;

alter table _ix_ncats_840372f9_1 add constraint fk__ix_ncats_840372f9_1_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table _ix_ncats_840372f9_2 add constraint fk__ix_ncats_840372f9_2_ix_nc_01 foreign key (ix_ncats_clinical_eligibility_exclusion_id) references ix_ncats_clinical_eligibility (id) on delete restrict on update restrict;

alter table _ix_ncats_840372f9_2 add constraint fk__ix_ncats_840372f9_2_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_event_figure add constraint fk_ix_core_event_figure_ix_co_01 foreign key (ix_core_event_id) references ix_core_event (id) on delete restrict on update restrict;

alter table ix_core_event_figure add constraint fk_ix_core_event_figure_ix_co_02 foreign key (ix_core_figure_id) references ix_core_figure (id) on delete restrict on update restrict;

alter table ix_ncats_grant_investigator add constraint fk_ix_ncats_grant_investigato_01 foreign key (ix_ncats_grant_id) references ix_ncats_grant (id) on delete restrict on update restrict;

alter table ix_ncats_grant_investigator add constraint fk_ix_ncats_grant_investigato_02 foreign key (ix_core_investigator_id) references ix_core_investigator (id) on delete restrict on update restrict;

alter table ix_ncats_grant_keyword add constraint fk_ix_ncats_grant_keyword_ix__01 foreign key (ix_ncats_grant_id) references ix_ncats_grant (id) on delete restrict on update restrict;

alter table ix_ncats_grant_keyword add constraint fk_ix_ncats_grant_keyword_ix__02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_ncats_grant_publication add constraint fk_ix_ncats_grant_publication_01 foreign key (ix_ncats_grant_id) references ix_ncats_grant (id) on delete restrict on update restrict;

alter table ix_ncats_grant_publication add constraint fk_ix_ncats_grant_publication_02 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table ix_core_group_principal add constraint fk_ix_core_group_principal_ix_01 foreign key (ix_core_group_id) references ix_core_group (id) on delete restrict on update restrict;

alter table ix_core_group_principal add constraint fk_ix_core_group_principal_ix_02 foreign key (ix_core_principal_id) references ix_core_principal (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_1 add constraint fk__ix_ncats_4a162ae3_1_ix_nc_01 foreign key (ix_ncats_clinical_intervention_id) references ix_ncats_clinical_intervention (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_1 add constraint fk__ix_ncats_4a162ae3_1_ix_co_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_2 add constraint fk__ix_ncats_4a162ae3_2_ix_nc_01 foreign key (ix_ncats_clinical_intervention_id) references ix_ncats_clinical_intervention (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_2 add constraint fk__ix_ncats_4a162ae3_2_ix_nc_02 foreign key (ix_ncats_clinical_arm_id) references ix_ncats_clinical_arm (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_3 add constraint fk__ix_ncats_4a162ae3_3_ix_nc_01 foreign key (ix_ncats_clinical_intervention_id) references ix_ncats_clinical_intervention (id) on delete restrict on update restrict;

alter table _ix_ncats_4a162ae3_3 add constraint fk__ix_ncats_4a162ae3_3_ix_nc_02 foreign key (ix_ncats_clinical_cohort_id) references ix_ncats_clinical_cohort (id) on delete restrict on update restrict;

alter table ix_core_payload_property add constraint fk_ix_core_payload_property_i_01 foreign key (ix_core_payload_id) references ix_core_payload (id) on delete restrict on update restrict;

alter table ix_core_payload_property add constraint fk_ix_core_payload_property_i_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_predicate_object add constraint fk_ix_core_predicate_object_i_01 foreign key (ix_core_predicate_id) references ix_core_predicate (id) on delete restrict on update restrict;

alter table ix_core_predicate_object add constraint fk_ix_core_predicate_object_i_02 foreign key (ix_core_xref_id) references ix_core_xref (id) on delete restrict on update restrict;

alter table ix_core_predicate_property add constraint fk_ix_core_predicate_property_01 foreign key (ix_core_predicate_id) references ix_core_predicate (id) on delete restrict on update restrict;

alter table ix_core_predicate_property add constraint fk_ix_core_predicate_property_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_publication_keyword add constraint fk_ix_core_publication_keywor_01 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table ix_core_publication_keyword add constraint fk_ix_core_publication_keywor_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_publication_mesh add constraint fk_ix_core_publication_mesh_i_01 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table ix_core_publication_mesh add constraint fk_ix_core_publication_mesh_i_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_publication_author add constraint fk_ix_core_publication_author_01 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table ix_core_publication_author add constraint fk_ix_core_publication_author_02 foreign key (ix_core_pubauthor_id) references ix_core_pubauthor (id) on delete restrict on update restrict;

alter table ix_core_publication_figure add constraint fk_ix_core_publication_figure_01 foreign key (ix_core_publication_id) references ix_core_publication (id) on delete restrict on update restrict;

alter table ix_core_publication_figure add constraint fk_ix_core_publication_figure_02 foreign key (ix_core_figure_id) references ix_core_figure (id) on delete restrict on update restrict;

alter table ix_ncats_hcs_reagent_tag add constraint fk_ix_ncats_hcs_reagent_tag_i_01 foreign key (ix_ncats_hcs_reagent_id) references ix_ncats_hcs_reagent (id) on delete restrict on update restrict;

alter table ix_ncats_hcs_reagent_tag add constraint fk_ix_ncats_hcs_reagent_tag_i_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_ncats_hcs_reagent_property add constraint fk_ix_ncats_hcs_reagent_prope_01 foreign key (ix_ncats_hcs_reagent_id) references ix_ncats_hcs_reagent (id) on delete restrict on update restrict;

alter table ix_ncats_hcs_reagent_property add constraint fk_ix_ncats_hcs_reagent_prope_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_stitch_attribute add constraint fk_ix_core_stitch_attribute_i_01 foreign key (ix_core_stitch_id) references ix_core_stitch (id) on delete restrict on update restrict;

alter table ix_core_stitch_attribute add constraint fk_ix_core_stitch_attribute_i_02 foreign key (ix_core_attribute_id) references ix_core_attribute (id) on delete restrict on update restrict;

alter table ix_core_structure_property add constraint fk_ix_core_structure_property_01 foreign key (ix_core_structure_id) references ix_core_structure (id) on delete restrict on update restrict;

alter table ix_core_structure_property add constraint fk_ix_core_structure_property_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

alter table ix_core_structure_link add constraint fk_ix_core_structure_link_ix__01 foreign key (ix_core_structure_id) references ix_core_structure (id) on delete restrict on update restrict;

alter table ix_core_structure_link add constraint fk_ix_core_structure_link_ix__02 foreign key (ix_core_xref_id) references ix_core_xref (id) on delete restrict on update restrict;

alter table ix_core_xref_property add constraint fk_ix_core_xref_property_ix_c_01 foreign key (ix_core_xref_id) references ix_core_xref (id) on delete restrict on update restrict;

alter table ix_core_xref_property add constraint fk_ix_core_xref_property_ix_c_02 foreign key (ix_core_value_id) references ix_core_value (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists ix_core_acl;

drop table if exists ix_core_acl_principal;

drop table if exists ix_core_acl_group;

drop table if exists ix_ncats_clinical_arm;

drop table if exists ix_core_attribute;

drop table if exists ix_ncats_clinical_trial;

drop table if exists ix_ncats_clinical_trial_keyword;

drop table if exists ix_ncats_clinical_trial_sponsor;

drop table if exists ix_ncats_clinical_trial_intervention;

drop table if exists ix_ncats_clinical_trial_condition;

drop table if exists ix_ncats_clinical_trial_outcome;

drop table if exists ix_ncats_clincial_trial_location;

drop table if exists ix_ncats_clincial_trial_publication;

drop table if exists ix_ncats_clinical_cohort;

drop table if exists ix_ncats_clinical_condition;

drop table if exists _ix_ncats_cca46885_1;

drop table if exists _ix_ncats_cca46885_2;

drop table if exists _ix_ncats_cca46885_3;

drop table if exists ix_core_curation;

drop table if exists ix_core_etag;

drop table if exists ix_core_etagref;

drop table if exists ix_core_edit;

drop table if exists ix_ncats_clinical_eligibility;

drop table if exists _ix_ncats_840372f9_1;

drop table if exists _ix_ncats_840372f9_2;

drop table if exists ix_core_event;

drop table if exists ix_core_event_figure;

drop table if exists ix_core_figure;

drop table if exists ix_ncats_funding;

drop table if exists ix_ncats_grant;

drop table if exists ix_ncats_grant_investigator;

drop table if exists ix_ncats_grant_keyword;

drop table if exists ix_ncats_grant_publication;

drop table if exists ix_core_group;

drop table if exists ix_core_group_principal;

drop table if exists ix_ncats_clinical_intervention;

drop table if exists _ix_ncats_4a162ae3_1;

drop table if exists _ix_ncats_4a162ae3_2;

drop table if exists _ix_ncats_4a162ae3_3;

drop table if exists ix_core_investigator;

drop table if exists ix_core_journal;

drop table if exists ix_core_namespace;

drop table if exists ix_core_organization;

drop table if exists ix_ncats_clinical_outcome;

drop table if exists ix_core_payload;

drop table if exists ix_core_payload_property;

drop table if exists ix_core_predicate;

drop table if exists ix_core_predicate_object;

drop table if exists ix_core_predicate_property;

drop table if exists ix_core_principal;

drop table if exists ix_core_procjob;

drop table if exists ix_core_procrecord;

drop table if exists ix_core_pubauthor;

drop table if exists ix_core_publication;

drop table if exists ix_core_publication_keyword;

drop table if exists ix_core_publication_mesh;

drop table if exists ix_core_publication_author;

drop table if exists ix_core_publication_figure;

drop table if exists ix_ncats_hcs_reagent;

drop table if exists ix_ncats_hcs_reagent_tag;

drop table if exists ix_ncats_hcs_reagent_property;

drop table if exists ix_core_role;

drop table if exists ix_core_stitch;

drop table if exists ix_core_stitch_attribute;

drop table if exists ix_core_structure;

drop table if exists ix_core_structure_property;

drop table if exists ix_core_structure_link;

drop table if exists ix_core_value;

drop table if exists ix_core_xref;

drop table if exists ix_core_xref_property;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists ix_core_acl_seq;

drop sequence if exists ix_ncats_clinical_arm_seq;

drop sequence if exists ix_core_attribute_seq;

drop sequence if exists ix_ncats_clinical_trial_seq;

drop sequence if exists ix_ncats_clinical_cohort_seq;

drop sequence if exists ix_ncats_clinical_condition_seq;

drop sequence if exists ix_core_curation_seq;

drop sequence if exists ix_core_etag_seq;

drop sequence if exists ix_core_etagref_seq;

drop sequence if exists ix_ncats_clinical_eligibility_seq;

drop sequence if exists ix_core_event_seq;

drop sequence if exists ix_core_figure_seq;

drop sequence if exists ix_ncats_funding_seq;

drop sequence if exists ix_ncats_grant_seq;

drop sequence if exists ix_core_group_seq;

drop sequence if exists ix_ncats_clinical_intervention_seq;

drop sequence if exists ix_core_investigator_seq;

drop sequence if exists ix_core_journal_seq;

drop sequence if exists ix_core_namespace_seq;

drop sequence if exists ix_core_organization_seq;

drop sequence if exists ix_ncats_clinical_outcome_seq;

drop sequence if exists ix_core_predicate_seq;

drop sequence if exists ix_core_principal_seq;

drop sequence if exists ix_core_procjob_seq;

drop sequence if exists ix_core_procrecord_seq;

drop sequence if exists ix_core_pubauthor_seq;

drop sequence if exists ix_core_publication_seq;

drop sequence if exists ix_ncats_hcs_reagent_seq;

drop sequence if exists ix_core_role_seq;

drop sequence if exists ix_core_stitch_seq;

drop sequence if exists ix_core_structure_seq;

drop sequence if exists ix_core_value_seq;

drop sequence if exists ix_core_xref_seq;

