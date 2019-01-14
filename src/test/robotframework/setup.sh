#!/bin/bash
# this script installs all dependencies for executing robotframework tests
sudo apt-get update -qq
sudo apt-get -y install python-pip python-dev --allow-unauthenticated
pip install urllib3[secure] --user -I
pip install robotframework --user
pip install robotframework-extendedselenium2library --user
pip install pyyaml --user
pip install six --user
