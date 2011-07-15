#!/bin/sh

clear;clear

cd duel-mvc-archetype
mvn clean deploy -U -DperformRelease=true -Dgpg.keyname=EE82F9AB
cd ..

cd bootstrap
mvn clean deploy -U -DperformRelease=true -Dgpg.keyname=EE82F9AB
