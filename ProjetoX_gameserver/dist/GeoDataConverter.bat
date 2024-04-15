@echo off
title Project X geodata converter

java -Xmx512m -cp ./libs/*; com.px.geodataconverter.GeoDataConverter

pause
