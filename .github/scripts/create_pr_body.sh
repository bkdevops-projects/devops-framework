#!/bin/bash

set -e

if [ "$#" -lt 4 ]; then
    echo "Expected actor,repository,run_id,commit_id arguments"
    exit 1
fi

ACTOR=$1
REPOSITORY=$2
RUN_ID=$3
COMMIT_ID=$4

echo "
你好 @$ACTOR!

这个PR是发布工作流自动创建的: https://github.com/$REPOSITORY/actions/runs/$RUN_ID。
我已经修改了版本号在这个commit: $COMMIT_ID。

合并这个PR将会创建一个GitHub release。
"