#!/bin/bash -x
LIBSDIR=./selenium-java-3.14.0/libs
LIBS=./selenium-java-3.14.0/client-combined-3.14.0.jar:$LIBSDIR/byte-buddy-1.8.15.jar:$LIBSDIR/commons-codec-1.10.jar:$LIBSDIR/commons-exec-1.3.jar:$LIBSDIR/commons-logging-1.2.jar:$LIBSDIR/guava-25.0-jre.jar:$LIBSDIR/httpclient-4.5.5.jar:$LIBSDIR/httpcore-4.4.9.jar:$LIBSDIR/okhttp-3.10.0.jar:$LIBSDIR/okio-1.14.1.jar


install_gecko_driver(){
    wget "https://github.com/mozilla/geckodriver/releases/download/v0.22.0/geckodriver-v0.22.0-linux64.tar.gz"
    tar -xzvf geckodriver-v0.22.0-linux64.tar.gz
}


run(){
    scala -classpath $LIBS main.scala
}

run
