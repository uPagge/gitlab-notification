#!/bin/bash

if [ "$DRONE_BUILD_STATUS" = "success" ]; then
  DEPLOY_STATUS="✅"
else
  DEPLOY_STATUS="❌"
fi

TIME="10"
URL="https://api.telegram.org/bot$NOTIFY_TELEGRAM_BOT_TOKEN/sendMessage"
TEXT="Deploy status: $DEPLOY_STATUS%0A-- -- -- -- --%0ABranch:+$DRONE_TARGET_BRANCH%0ATag:+$DRONE_TAG%0AProject:+$DRONE_REPO_NAME"

curl -s --max-time $TIME -d "chat_id=$NOTIFY_TELEGRAM_CHAT_ID&disable_web_page_preview=1&text=$TEXT" $URL >/dev/null
