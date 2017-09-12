FROM ubuntu:16.04

LABEL com.axibase.maintainer="ATSD Developers <dev-atsd@axibase.com>"

#put script to docker
ADD check_atsd.sh /root/
ADD atsd_webtests /root/atsd_webtests
ADD phantomjs /opt/bin/

#configure system
RUN apt-get update && apt-get install -y wget locales
RUN locale-gen en_US.UTF-8 && mkdir -p /var/run/sshd && chmod +x /root/check_atsd.sh &&  adduser --disabled-password --quiet --gecos "" axibase;

ENTRYPOINT ["/bin/bash","/root/check_atsd.sh"]

