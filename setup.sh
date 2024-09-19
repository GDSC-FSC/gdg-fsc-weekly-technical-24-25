#!/bin/bash

ls=("byo-kafka" "byo-dns-server" "byo-interpreter" "byo-sqlite" "byo-git" "byo-shell" "byo-redis" "byo-bit-torrent" "byo-http-server" "byo-grep")

count=0
for i in "${!ls[@]}"; do
  session="${ls[$i]}"
  week="week-$((i + 1))-$session"
  echo "Creating session $session"
  mkdir $week
done
