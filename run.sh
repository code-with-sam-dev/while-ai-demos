#!/usr/bin/env bash
# Run one demo and print exactly what the machine says.
#
# Every claim in the video about this code is checked by running it, which is
# the only reason any of it is on screen. Usage: ./run.sh RetryNoKey
set -uo pipefail
SRC="${1:?usage: run.sh <ClassName>}"
J="${JAVA_HOME:-$HOME/.sdkman/candidates/java/21.0.5-amzn}"
"$J/bin/java" "src/${SRC}.java" 2>&1
