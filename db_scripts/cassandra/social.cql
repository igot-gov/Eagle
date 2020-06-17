CREATE TABLE IF NOT EXISTS bodhi.post (
    root_org text,
    org text,
    post_id uuid,
    accesspaths list<text>,
    activity_end_date timestamp,
    admin_date_deletion timestamp,
    date_created timestamp,
    date_last_modified timestamp,
    date_published timestamp,
    hashtags list<text>,
    is_accepted_answer boolean,
    is_admin_deleted boolean,
    is_flagged boolean,
    last_edited map<text, text>,
    option map<text, text>,
    parent_id uuid,
    post_content map<text, text>,
    post_creator map<text, text>,
    post_editor list<frozen<map<text, text>>>,
    post_kind text,
    primarylanguage text,
    recipients list<frozen<map<text, text>>>,
    root_parent_id uuid,
    source map<text, text>,
    status text,
    tags list<frozen<map<text, text>>>,
    thumbnail text,
    PRIMARY KEY ((root_org, org), post_id)
) WITH CLUSTERING ORDER BY (post_id ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

	
CREATE TABLE IF NOT EXISTS bodhi.post_count (
    root_org text,
    org text,
    post_id uuid,
    comment_count counter,
    dislike counter,
    down_vote counter,
    flag counter,
    like counter,
    share counter,
    up_vote counter,
    PRIMARY KEY ((root_org, org), post_id)
) WITH CLUSTERING ORDER BY (post_id ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS bodhi.follow_master (
    root_org text,
    org text,
    sourceid text,
    targetid text,
    type text,
    follow_date timestamp,
    id uuid,
    last_modified_date timestamp,
    status text,
    PRIMARY KEY ((root_org, org), sourceid, targetid, type)
) WITH CLUSTERING ORDER BY (sourceid ASC, targetid ASC, type ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.follow_by_sourceid AS
    SELECT root_org, org, sourceid, status, type, targetid, follow_date
    FROM bodhi.follow_master
    WHERE sourceid IS NOT NULL AND root_org IS NOT NULL AND org IS NOT NULL AND status IS NOT NULL AND targetid IS NOT NULL AND type IS NOT NULL AND follow_date IS NOT NULL
    PRIMARY KEY ((root_org, org), sourceid, status, type, targetid)
    WITH CLUSTERING ORDER BY (sourceid ASC, status ASC, type ASC, targetid ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.follow_by_targetid AS
    SELECT root_org, org, targetid, status, type, sourceid, follow_date
    FROM bodhi.follow_master
    WHERE sourceid IS NOT NULL AND root_org IS NOT NULL AND org IS NOT NULL AND status IS NOT NULL AND targetid IS NOT NULL AND type IS NOT NULL AND follow_date IS NOT NULL
    PRIMARY KEY ((root_org, org), targetid, status, type, sourceid)
    WITH CLUSTERING ORDER BY (targetid ASC, status ASC, type ASC, sourceid ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS bodhi.post_history (
    root_org text,
    org text,
    post_history_id uuid,
    abstract text,
    body text,
    date_edited timestamp,
    editor uuid,
    post_id uuid,
    title text,
    PRIMARY KEY ((root_org, org), post_history_id)
) WITH CLUSTERING ORDER BY (post_history_id ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

	
CREATE TABLE IF NOT EXISTS bodhi.user_tag_post_mapping (
    root_org text,
    org text,
    post_id uuid,
    tag_id uuid,
    date_activity_performed timestamp,
    activity text,
    user_id uuid,
    PRIMARY KEY ((root_org, org), post_id, tag_id, date_activity_performed)
) WITH CLUSTERING ORDER BY (post_id ASC, tag_id ASC, date_activity_performed ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE IF NOT EXISTS bodhi.user_hashtag_post_mapping (
    root_org text,
    org text,
    hashtag_history_id uuid,
    date_activity_performed timestamp,
    hashtag list<text>,
    post_id uuid,
    user_id uuid,
    PRIMARY KEY ((root_org, org), hashtag_history_id)
) WITH CLUSTERING ORDER BY (hashtag_history_id ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';