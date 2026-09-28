#!/usr/bin/env bash
# Capture what each demo actually prints, into captures/, so the video can put
# the real output on screen rather than a retyped approximation of it.
#
# The header line is the command that was ACTUALLY RUN. That is deliberate: a
# header claiming a flag nobody passed is a fabricated screenshot, which is
# worse than no screenshot.
set -uo pipefail
mkdir -p captures
for c in RetryNoKey RetryWithKey StaleCache; do
  out="captures/${c}.txt"
  { echo "\$ java src/${c}.java"; echo; ./run.sh "$c"; } > "$out"
  echo "captured $out"
done
