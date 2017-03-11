CREATE TABLE "i18n" (
  "_id" INTEGER PRIMARY KEY NOT NULL ,
  "en" TEXT NOT NULL ,
  "pl" TEXT NOT NULL
);
--  Add Indexes
CREATE INDEX IDX_i18n__id ON i18n ("_id" ASC);
CREATE UNIQUE INDEX IDX_i18n_en ON i18n ("en" ASC);