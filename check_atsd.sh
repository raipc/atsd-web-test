#!/bin/bash
#set -x
# exit code description:
# 0 - test success
# 1 - script arguments error
# 2 - atsd version mismatch
# 3 - atsd installation failed
# 4 - latest pages check failed

#ATSD_REQUIRED_REVISION="$1"
if [ "$ATSD_REQUIRED_REVISION" = "" ]; then
    echo "Specify ATSD revision to be checked in the run command 'docker run -e ATSD_REQUIRED_REVISION=\"12345\" imageName ' to continue."
    exit 1
fi

AXIBASE_REPO_FILE="/etc/apt/sources.list.d/axibase.list"
PACKAGES_PATH="/tmp/packages"


function main {
    check_download_pages
    check_package_links
    check_repository_atsd_revision
    install_atsd
    check_installed_atsd_revision
    run_webtests
}

DOWNLOAD_URL="https://axibase.com/public"

function check_download_pages {
    cd /root

    debPage="atsd_deb_latest.htm"
    rmpPage="atsd_rpm_latest.htm"
    slesPage="atsd_rpm_sles_latest.htm"

    curl -f -O -J "${DOWNLOAD_URL}/${debPage}"

    if ! grep -q "atsd_${ATSD_REQUIRED_REVISION}_amd64.deb" ${debPage}; then
       echo "The download page doesn't contain required revision ${ATSD_REQUIRED_REVISION}: ${DOWNLOAD_URL}/${debPage}"
       exit 2
    else 
        echo "Download page ${debPage} contains the required revision ${ATSD_REQUIRED_REVISION}."
    fi

    curl -f -O -J "${DOWNLOAD_URL}/${rmpPage}"

    if ! grep -q "atsd_${ATSD_REQUIRED_REVISION}_amd64.rpm" ${rmpPage}; then
       echo "The download page doesn't contain required revision ${ATSD_REQUIRED_REVISION}: ${DOWNLOAD_URL}/${rmpPage}"
       exit 2
    else 
        echo "Download page ${rmpPage} contains the required revision ${ATSD_REQUIRED_REVISION}."
    fi

    curl -f -O -J "${DOWNLOAD_URL}/${slesPage}"

    if ! grep -q "atsd_${ATSD_REQUIRED_REVISION}_amd64_sles.rpm" ${slesPage}; then
       echo "The download page doesn't contain required revision ${ATSD_REQUIRED_REVISION}: ${DOWNLOAD_URL}/${slesPage}"
       exit 2
    else 
        echo "Download page ${slesPage} contains the required revision ${ATSD_REQUIRED_REVISION}."
    fi  

    echo "Download pages checked."
}

function check_package_links {
    cd /root
    
    minlen=200000000
    debFile="atsd_${ATSD_REQUIRED_REVISION}_amd64.deb"
    
    deblen=$(curl -f -s -I "${DOWNLOAD_URL}/${debFile}" | grep "Content-Length" | awk '{print $2}' | tr -d '[:space:]')

    if ((${deblen} < ${minlen})); then
       echo "Package file length for ${debFile} is too small: ${deblen}"
       exit 2
    else
        echo "Package file length for ${debFile} is ${deblen}."
    fi

    rpmFile="atsd_${ATSD_REQUIRED_REVISION}_amd64.rpm"
    rpmlen=$(curl -f -s -I "${DOWNLOAD_URL}/${rpmFile}" | grep "Content-Length" | awk '{print $2}' | tr -d '[:space:]')

    if ((${rpmlen} < ${minlen})); then
       echo "Package file length for ${rpmFile} is too small: ${rpmlen}"
       exit 2
    else
        echo "Package file length for ${rpmFile} is ${rpmlen}."
    fi

    slesFile="atsd_${ATSD_REQUIRED_REVISION}_amd64_sles.rpm"
    sleslen=$(curl -f -s -I "${DOWNLOAD_URL}/${slesFile}" | grep "Content-Length" | awk '{print $2}' | tr -d '[:space:]')

    if ((${sleslen} < ${minlen})); then
       echo "Package file length for ${slesFile} is too small: ${sleslen}"
       exit 2
    else
        echo "Package file length for ${slesFile} is ${sleslen}."
    fi

    echo "Package files checked for size."
}

function run_webtests {
    cd /root/atsd_webtests
    export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
    mvn --quiet test
}

function infinity_loop {
    while true; do sleep 1; done
}

function check_repository_atsd_revision {
    apt-get update
    availableRevisions="`apt-cache policy atsd`"
    if ! echo "$availableRevisions" | grep -q "$ATSD_REQUIRED_REVISION"; then
        echo "Can not find the specified revision in deb repository."
        echo "Required Revision: $ATSD_REQUIRED_REVISION"
        echo "Available Revisions: "
        echo "$availableRevisions"
        exit 2
    fi
}

function install_atsd {
    mkdir -p $PACKAGES_PATH
    cd $PACKAGES_PATH
    DEBIAN_FRONTEND=noninteractive apt-get install -y atsd
}

function check_installed_atsd_revision {
    installedVersion="`dpkg -s atsd | grep Version | cut -d" " -f2`"
    if [ "$installedVersion" != "$ATSD_REQUIRED_REVISION" ]; then
        echo "Version mismatch."
        echo "Required Revision : $ATSD_REQUIRED_REVISION"
        echo "Installed Revision: $installedVersion"
        exit 2
    fi
}

main "$@"
