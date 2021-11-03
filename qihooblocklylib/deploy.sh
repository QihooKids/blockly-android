#!/usr/bin/env bash
cd ..
pwd
./gradlew blocklylib-core:clean
./gradlew qihooblocklylib:clean
./gradlew blocklylib-core:build
./gradlew qihooblocklylib:build
./gradlew qihooblocklylib:publish
