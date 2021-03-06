drop table if exists <SCHEMA>.apollo_mv_minute;

CREATE TABLE <SCHEMA>.apollo_mv_minute (
  row_id bigint NOT NULL ,
  ad_id bigint NOT NULL,
  campaign_id bigint NOT NULL,
  day varchar(30) NOT NULL,
  placement_id bigint NOT NULL,
  publisher_id bigint NOT NULL,
  tactic_id bigint NOT NULL,
  line_item_id bigint NOT NULL,
  rfi_clicks double NOT NULL,
  pub_clicks double NOT NULL,
  adv_clicks double NOT NULL,
  rfi_client_views double NOT NULL,
  pub_client_views double NOT NULL,
  adv_client_views double NOT NULL,
  rfi_server_views double NOT NULL,
  pub_server_views double NOT NULL,
  adv_server_views double NOT NULL,
  cost double NOT NULL,
  rfi_revenue double NOT NULL,
  pub_revenue double NOT NULL,
  adv_revenue double NOT NULL,
  rfi_value double NOT NULL,
  pub_value double NOT NULL,
  adv_value double NOT NULL,
  rfi_conversions double NOT NULL,
  adv_conversions double NOT NULL,
  rfi_thisday_conversions double NOT NULL,
  adv_thisday_conversions double NOT NULL,
  refresh_time varchar(30) NOT NULL,
  tier smallint NOT NULL DEFAULT 3,
  flight_id bigint NOT NULL,
  pub_clicks_source smallint DEFAULT NULL,
  adv_clicks_source smallint DEFAULT NULL,
  pub_client_views_source smallint DEFAULT NULL,
  adv_client_views_source smallint DEFAULT NULL,
  adv_server_views_source smallint DEFAULT NULL,
  pub_server_views_source smallint DEFAULT NULL,
  adv_conversions_source smallint DEFAULT NULL,
  first_touches double NOT NULL DEFAULT 0,
  multi_touches double NOT NULL DEFAULT 0,
  data_cost double DEFAULT NULL,
  media_cost double DEFAULT NULL,
  rfi_client_revenue double NOT NULL,
  adv_client_revenue double NOT NULL,
  media_type smallint NOT NULL DEFAULT 1,
  rfi_thisday_value double NOT NULL DEFAULT 0,
  adv_thisday_value double NOT NULL DEFAULT 0,
  rfi_thisday_client_revenue double NOT NULL,
  adv_thisday_client_revenue double NOT NULL,
  adv_thisday_conversion_source smallint DEFAULT NULL,
  advertiser_id bigint DEFAULT NULL,
  rest_count bigint DEFAULT NULL,
  rfi_conversions_click_through double NOT NULL DEFAULT 0,
  adv_conversions_click_through double NOT NULL DEFAULT 0,
  rfi_thisday_conversions_click_through double NOT NULL DEFAULT 0,
  adv_thisday_conversions_click_through double NOT NULL DEFAULT 0,
  day_campaign_timezone varchar(30) NOT NULL DEFAULT '0000-00-00 00:00:00',
  time_zone varchar(255) NOT NULL DEFAULT 'America/New_York',
  sub_network_id int NOT NULL DEFAULT 1,
  campaign_currency_type char(3) NOT NULL DEFAULT 'USD',
  exchange_rate decimal(13,7) NOT NULL DEFAULT 1.0000000,
  cost_to_advertiser double NOT NULL DEFAULT 0,
  PRIMARY KEY (ad_id)
);

