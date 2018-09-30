#!/usr/bin/env bash
# 1234
#openssl req -x509 -newkey rsa:4096 -keyout clave-key.pem -out clave-cert.pem -days 365


openssl pkcs12 -export -out clave.pfx -inkey clave-key.pem -in clave-cert.pem
keytool -list -v -keystore clave.pfx -storepass "" -storetype PKCS12
