#!/bin/sh
#Forward 80/443 traffic to Traefik's LAN IP.

iptables -t nat -C PREROUTING -i wg0 -p tcp --dport 80  -j DNAT --to-destination 192.168.0.201:80  2>/dev/null || \
iptables -t nat -A PREROUTING -i wg0 -p tcp --dport 80  -j DNAT --to-destination 192.168.0.201:80

iptables -t nat -C PREROUTING -i wg0 -p tcp --dport 443 -j DNAT --to-destination 192.168.0.201:443 2>/dev/null || \
iptables -t nat -A PREROUTING -i wg0 -p tcp --dport 443 -j DNAT --to-destination 192.168.0.201:443

iptables -t nat -C POSTROUTING -o eth0 -j MASQUERADE 2>/dev/null || \
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE

iptables -C FORWARD -i wg0 -o eth0 -p tcp --dport 80  -j ACCEPT 2>/dev/null || \
iptables -A FORWARD -i wg0 -o eth0 -p tcp --dport 80  -j ACCEPT

iptables -C FORWARD -i wg0 -o eth0 -p tcp --dport 443 -j ACCEPT 2>/dev/null || \
iptables -A FORWARD -i wg0 -o eth0 -p tcp --dport 443 -j ACCEPT

iptables -C FORWARD -i eth0 -o wg0 -m state --state ESTABLISHED,RELATED -j ACCEPT 2>/dev/null || \
iptables -A FORWARD -i eth0 -o wg0 -m state --state ESTABLISHED,RELATED -j ACCEPT