#!/bin/bash

if [ "$USER" = "root" ]; then

eto=$(ls /sys/class/net | grep ^e)
select the1 in $eto; do
    echo "your choice is $the1"
    break
done

echo

#ip link | grep ens33 | awk -F 'master' '{print $2}' | awk '{print $1}'

if [ ! -n "$1" ]; then
    bridge=br0
else
    bridge=$1
fi


if [ -f /etc/netplan/$the1.yaml ]; then
    rm -rf /etc/netplan/$the1.yaml
fi
if [ -f /etc/netplan/00-*.yaml ]; then
    rm -rf /etc/netplan/00-*.yaml
fi
if [ -f /etc/netplan/50-*.yaml ]; then
    rm -rf /etc/netplan/50-*.yaml
fi

echo
echo "-------------------------------------------------------------------------------------------------"
echo "Enter $bridge staticip (like: 192.168.0.250):"
read lip
while [ ! -n "${lip}" ]; do
    echo "Staticip cannot be empty!!!"
    echo "Please enter Staticip again (like: 192.168.0.250):"
    read lip
done
echo "The IP address you entered is ${lip}"
echo
echo
echo "Enter $bridge prefix (like: 24):"
read mask
while [ ! -n "${mask}" ]; do
    echo "Prefix cannot be empty!!!"
    echo "Please enter prefix again (like: 24):"
    read mask
done
echo "The prefix(netmask) you entered is ${mask}"
echo
echo
echo "Enter $bridge gateway (like: 192.168.0.1):"
read gwy
while [ ! -n "${gwy}" ]; do
    echo "Gateway cannot be empty!!!"
    echo "Please enter gateway again (like: 192.168.0.1):"
    read gwy
done
echo "The gateway you entered is ${gwy}"
echo
echo
echo "Enter $bridge dns1 (like: 114.114.114.114):"
read dns1
while [ ! -n "${dns1}" ]; do
    echo "DNS1 cannot be empty!!!"
    echo "Please enter dns1 again (like: 114.114.114.114):"
    read dns1
done
echo "The DNS1 you entered is ${dns1}"
echo
echo
echo "Enter $bridge dns2 (like: 8.8.8.8):"
read dns2
while [ ! -n "${dns2}" ]; do
    echo "DNS2 cannot be empty!!!"
    echo "Please enter dns2 again (like: 8.8.8.8):"
    read dns2
done
echo "The DNS2 you entered is ${dns2}"
echo "-------------------------------------------------------------------------------------------------"
echo

cat > /etc/netplan/$bridge.yaml << EOF
# This is the network config written by 'subiquity'
network:
  ethernets:
    $the1:
      addresses: []
      dhcp4: no
  bridges:
    $bridge:     
      addresses:
      - ${lip}/${mask}
      gateway4: ${gwy}
      nameservers:
        addresses:
        - ${dns1}
        search:
        - ${dns2}
      interfaces:
        - $the1
      parameters:
        stp: false     
  version: 2
EOF

netplan apply
sleep 5
echo

else
    echo "Please use [[ sudo setbridge ]] !!!"
fi
