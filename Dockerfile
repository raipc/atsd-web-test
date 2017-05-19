FROM ubuntu:14.04
MAINTAINER ATSD Developers <dev-atsd@axibase.com>



#put script to docker
ADD check_atsd.sh /root/
ADD atsd_webtests /root/atsd_webtests
ADD phantomjs /opt/bin/

#configure system
RUN locale-gen en_US.UTF-8 && mkdir -p /var/run/sshd && chmod +x /root/check_atsd.sh &&  adduser --disabled-password --quiet --gecos "" axibase;
RUN apt-get update && apt-get install -y wget

ENTRYPOINT ["/bin/bash","/root/check_atsd.sh"]

