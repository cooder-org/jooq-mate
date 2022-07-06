#!/bin/sh


if [ $1 ]; then
  group=$1
else
  group="org.cooder.jooq.demo"
fi

if [ $2 ]; then
  artifact=$2
  name=$2
else
  artifact="demo"
  name="demo"
fi

if [ $3 ]; then
  packageName=$3
else
  packageName="org.cooder.jooq.demo"
fi

curl -G https://start.spring.io/starter.zip -d groupId=$group -d artifactId=$artifact -d name=$name -d applicationName=Application -d packaging=jar -d javaVersion=8 -d dependencies=web,mysql,jooq,lombok -o ${name}.zip

unzip ${name}.zip -d ${name}
rm -f ${name}.zip

PROP_FILE="${name}/src/main/resources/application.properties"
echo "spring.datasource.url=jdbc:mysql://localhost:3306/nerve-center?useUnicode=true&characterEncoding=utf-8&useSSL=false" >> $PROP_FILE
echo "spring.datasource.username=root" >> $PROP_FILE
echo "spring.datasource.password=root" >> $PROP_FILE
