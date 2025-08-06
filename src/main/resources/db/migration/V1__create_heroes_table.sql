
CREATE TABLE files (
    id UUID PRIMARY KEY NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    extension VARCHAR(64) NOT NULL,
    mime_type VARCHAR(128) NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL
);


CREATE TABLE heroes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(128) NOT NULL,
    nickname VARCHAR(128),
    franchise VARCHAR(256) NOT NULL,
    company_origin VARCHAR(128) NOT NULL,
    debut_game VARCHAR(256) NOT NULL,
    year_debut BIGINT NOT NULL,
    notes TEXT,
    image_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_hero_image FOREIGN KEY (image_id) REFERENCES files(id) ON DELETE SET NULL
);