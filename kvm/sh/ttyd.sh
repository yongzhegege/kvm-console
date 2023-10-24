ttydStatus=$(lsof -i:14377 | grep LISTEN)
if [ -z "$ttydStatus" ]; then
pkill -9 ttyd
ttyd -p 14377 login
fi
