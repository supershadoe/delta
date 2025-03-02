#!/usr/bin/env bash

edit_version_code() {
    echo "Determining version code..."
    versionCode=$(grep "versionCode =" \
        buildSrc/src/main/kotlin/delta/buildsrc/VersionConfig.kt |\
        cut -d= -f2 |\
        cut -d, -f1 |\
        xargs \
    )
    echo "Current version code is $versionCode"
    versionCode=$((versionCode + 1))
    echo "Updating it to $versionCode"
    sed -i "s/versionCode = [0-9]\+/versionCode = $versionCode/" \
        buildSrc/src/main/kotlin/delta/buildsrc/VersionConfig.kt
}

edit_version_name() {
    echo "Determining version name..."
    oldVersionName=$(grep "versionName =" \
    buildSrc/src/main/kotlin/delta/buildsrc/VersionConfig.kt |\
        cut -d= -f2 |\
        cut -d\" -f2 |\
        xargs \
    )
    echo "Current version name is $oldVersionName"
    oldDate=$(echo $oldVersionName | cut -d+ -f1)
    buildNum=$(echo $oldVersionName | cut -d+ -f2)
    newDate=$(date +%Y.%m)
    echo "Current date is $newDate"
    if [ "$1" = "pre" ]; then
        if [ $buildNum -ge 0 ]; then
            echo "Pre-release call, not updating build number..."
        else
            echo "Unknown build number, resetting it..."
            buildNum=0
        fi
    elif [ $oldDate != $newDate ]; then
        echo "Date doesn't match current date, resetting build number..."
        buildNum=0
    else
        echo "Date matches the current date, incrementing build number..."
        buildNum=$((buildNum + 1))
    fi
    versionName="$newDate+$buildNum"
    echo "New version name is $versionName"
    sed -i "s/versionName = \"\S\+\"/versionName = \"$versionName\"/" \
        buildSrc/src/main/kotlin/delta/buildsrc/VersionConfig.kt
}

case $1 in
    "pre")
        edit_version_name $1
        ;;
    "post")
        edit_version_code
        edit_version_name $1
        ;;
    *)
        echo "Enter a valid argument - pre|post"
        ;;
esac
