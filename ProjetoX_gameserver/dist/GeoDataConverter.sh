#! /bin/sh

if ! [ -d ./log/ ]; then
	mkdir log
fi

java -Xmx512m -cp ./libs/*; com.px.geodataconverter.GeoDataConverter > log/stdout.log 2>&1