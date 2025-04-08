#!/usr/bin/env bash

edit_version_code() {
    echo "Determining version code..." >&2
    versionCode=$(grep "versionCode =" \
        gradle/build-logic/src/main/kotlin/delta/buildsrc/VersionConfig.kt |\
        cut -d= -f2 |\
        cut -d, -f1 |\
        xargs \
    )
    echo "Current version code is $versionCode" >&2
    versionCode=$((versionCode + 1))
    echo "Updating it to $versionCode" >&2
    sed -i "s/versionCode = [0-9]\+/versionCode = $versionCode/" \
        gradle/build-logic/src/main/kotlin/delta/buildsrc/VersionConfig.kt
    echo "versionCode=$versionCode"
}

edit_version_name() {
    echo "Determining version name..." >&2
    oldVersionName=$(grep "versionName =" \
    gradle/build-logic/src/main/kotlin/delta/buildsrc/VersionConfig.kt |\
        cut -d= -f2 |\
        cut -d\" -f2 |\
        xargs \
    )
    echo "Current version name is $oldVersionName" >&2
    oldDate=$(echo $oldVersionName | cut -d+ -f1)
    buildNum=$(echo $oldVersionName | cut -d+ -f2)
    newDate=$(date +%Y.%m)
    echo "Current date is $newDate" >&2
    if [ $oldDate != $newDate ]; then
        echo "Date doesn't match current date, resetting build number..." >&2
        buildNum=0
    elif [ "$1" = "pre" ]; then
        if [ $buildNum -ge 0 ]; then
            echo "Pre-release call, not updating build number..." >&2
        else
            echo "Unknown build number, resetting it..." >&2
            buildNum=0
        fi
    else
        echo "Date matches the current date, incrementing build number..." >&2
        buildNum=$((buildNum + 1))
    fi
    versionName="$newDate+$buildNum"
    echo "New version name is $versionName" >&2
    sed -i "s/versionName = \"\S\+\"/versionName = \"$versionName\"/" \
        gradle/build-logic/src/main/kotlin/delta/buildsrc/VersionConfig.kt
    echo "versionName=$versionName"
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
        echo "Enter a valid argument - pre|post" >&2
        ;;
esac
