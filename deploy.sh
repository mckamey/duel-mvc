#!/bin/sh

clear;clear

cd duel-mvc
mvn clean deploy -U -DperformRelease=true -Dgpg.keyname=EE82F9AB
cd ..

cd duel-mvc-archetype
mvn clean deploy -U -DperformRelease=true -Dgpg.keyname=EE82F9AB
