@echo off
title KB

cd ../Verifier
start "Verifier" java Verifier 127.0.0.1
cd ../Eliminator
start "Eliminator" java Eliminator 127.0.0.1