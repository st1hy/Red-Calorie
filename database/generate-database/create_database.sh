#!/bin/bash
set -e

NAME=database.db
BUILD_ASSETS="build/generated/assets/databases"
ZIPNAME="generated/assets/databases/$NAME.zip"

rm -rf build/*
mkdir -p $BUILD_ASSETS
sqlite3 "build/$NAME" '.read sql.sql'
cd build
zip -q $ZIPNAME $NAME
