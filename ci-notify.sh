#!/bin/bash

TIME="10"
URL="https://api.telegram.org/bot$DEPLOY_TELEGRAM_BOT_TOKEN/sendMessage"
TEXT="Deploy status: $1%0A-- -- -- -- --%0ABranch:+$CI_COMMIT_REF_SLUG%0AProject:+$CI_PROJECT_TITLE"

curl -s --max-time $TIME --proxy $PROXY_SETTING -d "chat_id=$TELEGRAM_CHAT_ID&disable_web_page_preview=1&text=$TEXT" $URL >/dev/null
