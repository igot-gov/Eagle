/*	PLAYLIST TABLE SCRIPT STARTS  */ 													


/* 	user_playlist Creation Script 	*/
CREATE TABLE IF NOT EXISTS bodhi.user_playlist (
    root_org text,
    user_id text,
    playlist_id uuid,
    created_on timestamp,
    isshared int,
    last_updated_on timestamp,
    playlist_title text,
    resource_ids list<text>,
    shared_by text,
    source_playlist_id uuid,
    visibility text,
    PRIMARY KEY ((root_org, user_id), playlist_id)
);

/* 	Index on source_playlist_id 	*/
CREATE INDEX IF NOT EXISTS id_userplaylist ON bodhi.user_playlist (source_playlist_id);


/* 		playlist_shared Creation Script 	*/
CREATE TABLE IF NOT EXISTS bodhi.playlist_shared (
	root_org text,
    shared_with text,
    playlist_id uuid,
    last_updated_on timestamp,
    playlist_title text,
    resource_ids list<text>,
    shared_by text,
    shared_on timestamp,
    visibility text,
    PRIMARY KEY ((root_org, shared_with), playlist_id)
);


/* 	playlist_recent creation script	*/
CREATE TABLE IF NOT EXISTS bodhi.playlist_recent (
    root_org text,
    user_id text,
    playlist_id uuid,
    resource_id text,
    last_updated_on timestamp,
    PRIMARY KEY ((root_org, user_id), playlist_id, resource_id)
) WITH CLUSTERING ORDER BY (playlist_id ASC, resource_id ASC);

CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.mv_playlist_recent AS
    SELECT root_org, user_id, last_updated_on, playlist_id, resource_id
    FROM bodhi.playlist_recent
    WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND last_updated_on IS NOT NULL AND playlist_id IS NOT NULL AND resource_id IS NOT NULL
    PRIMARY KEY ((root_org, user_id), last_updated_on, playlist_id, resource_id)
    WITH CLUSTERING ORDER BY (last_updated_on DESC, playlist_id ASC, resource_id ASC);


/*	PLAYLIST TABLE SCRIPT ENDS	*/ 		