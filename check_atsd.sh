#!/bin/bash
#set -x
# exit code description:
# 0 - test success
# 1 - script arguments error
# 2 - atsd  version missmatch
# 3 - atsd installation fail
# 4 - latest pages check fail
#ATSD_REQUIRED_REVISION="$1"
if [ "$ATSD_REQUIRED_REVISION" = "" ]; then
    echo "Please specify version by 'docker run -e ATSD_REQUIRED_REVISION=\"12345\" -it imageName ' to continue."
    exit 1
fi
AXIBASE_REPO_FILE="/etc/apt/sources.list.d/axibase.list"
PACKAGES_PATH="/tmp/packages"


function main {
    dpkg -s atsd >/dev/null 2>&1;
    if [ $? -eq 0 ]; then
        echo "ATSD already installed. Starting..."
        service ssh start
        /opt/atsd/bin/atsd-all.sh start
        infinity_loop
    fi

    check_latest_pages
    prepare_repository
    check_repository_atsd_revision
    install_atsd
    check_installed_atsd_revision
    run_webtests
    #infinity_loop
}

function check_latest_pages {
    cd /root
    pagenamedeb="atsd_ce_deb_latest.htm"
    pagenamerpm="atsd_ce_rpm_latest.htm"
    pagenamerpmsles="atsd_ce_rpm_sles_latest.htm"
    wget https://axibase.com/public/$pagenamedeb -O $pagenamedeb
    wget https://axibase.com/public/$pagenamerpm -O $pagenamerpm
    wget https://axibase.com/public/$pagenamerpmsles -O $pagenamerpmsles
    fail="false"
    #check deb
    #if ! grep -q '<meta http-equiv="refresh" content="0; URL=atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64.deb">' $pagenamedeb; then
    #    echo "fail to check latest autodownload deb"
    #    fail="true"
    #fi
    #if ! grep -q '<p>Your download will begin shortly. If your download does not start, please use the <a title="Download ATSD" href="http://axibase.com/public/atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64.deb">Direct Link</a></p>' $pagenamedeb; then
    #    echo "fail to check latest link deb"
    #    fail="true"
    #fi

    ##check rpm
    #if ! grep -q '<meta http-equiv="refresh" content="0; URL=atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64.rpm">' $pagenamerpm; then
    #    echo "fail to check latest autodownload rpm"
    #    fail="true"
    #fi
    #if ! grep -q '<p>Your download will begin shortly. If your download does not start, please use the <a title="Download ATSD" href="http://axibase.com/public/atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64.rpm">Direct Link</a></p>' $pagenamerpm; then
    #    echo "fail to check latest link rpm"
    #    fail="true"
    #fi

    ##check rpm sles
    #if ! grep -q '<meta http-equiv="refresh" content="0; URL=atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64_sles.rpm">' $pagenamerpmsles; then
    #    echo "fail to check latest autodownload rpm sles"
    #    fail="true"
    #fi
    #if ! grep -q '<p>Your download will begin shortly. If your download does not start, please use the <a title="Download ATSD" href="http://axibase.com/public/atsd_ce_'$ATSD_REQUIRED_REVISION'_amd64_sles.rpm">Direct Link</a></p>' $pagenamerpmsles; then
    #    echo "fail to check latest link rpm sles"
    #    fail="true"
    #fi
    #if [ "$fail" = "true" ]; then
    #    exit 4
    #else
    #    echo "all latest pages was checked successfully"
    #fi
}

function run_webtests {
    apt-get install -y maven
    cd /root/atsd_webtests
    export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
    mvn test
}

function infinity_loop {
    while true; do sleep 1; done
}

function prepare_repository {

    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 26AEE425A57967CFB323846008796A6514F3CB79
    echo "deb [arch=amd64] http://axibase.com/public/repository/deb/ ./" >> $AXIBASE_REPO_FILE
    apt-get update
}

function check_repository_atsd_revision {
    availableRevisions="`apt-cache policy atsd`"
    if ! echo "$availableRevisions" | grep -q "$ATSD_REQUIRED_REVISION"; then
        echo "Can not find specified revision in repository."
        echo "Required Revision: $ATSD_REQUIRED_REVISION"
        echo "Available Revisions: "
        echo "$availableRevisions"
        exit 2
    fi
}

function install_atsd {
    mkdir -p $PACKAGES_PATH
    cd $PACKAGES_PATH
    #apt-get --print-uris --yes install atsd | grep ^\' | cut -d\' -f2 | xargs wget
    #dpkg -i install *
    #dpkg -i install atsd*
    apt-get install -y atsd
    su axibase -c /opt/atsd/install_user.sh
    if [ $? -ne 0 ]; then
        exit 3
    fi
}

function check_installed_atsd_revision {
    installedVersion="`dpkg -s atsd | grep Version | cut -d" " -f2`"
    if [ "$installedVersion" != "$ATSD_REQUIRED_REVISION" ]; then
        echo "version missmatch."
        echo "Required Revision : $ATSD_REQUIRED_REVISION"
        echo "Installed Revision: $installedVersion"
        exit 2
    fi
}

main "$@"
