#!/bin/sh
free -m | awk '{if($3>1024 && NR==2){printf "%0.1fGB/%0.1fGB (%.2f%%)\n",$3/1024,$2/1024,$3*100/$2 } else if($3<1024 && $2<1024 && NR ==2){printf "%dMB/%dMB (%.2f%%)\n",$3,$2,$3*100/$2} if($3<1024 && $2>1024 && NR ==2){printf "%dMB/%0.1fGB (%.2f%%)\n",$3,$2/1024,$3*100/$2}}'
df -h | awk '$NF=="/"{printf "%s/%s (%s)\n", $3,$2,$5}'
top -bn1 | grep Cpu | awk -F 'id' '{print $1}' | awk -F 'ni' '{print $2}' | awk '{print $2}'
