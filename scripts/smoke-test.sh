#!/usr/bin/env bash
#
# Smoke test against a RUNNING instance of the Meeting Scheduler API.
# Seeds a small, realistic dataset and exercises every endpoint over real HTTP.
#
# Usage:
#   ./scripts/smoke-test.sh                       # defaults: http://localhost:8080 admin/admin123
#   BASE_URL=http://my-host:8080 API_USER=admin API_PASS=secret ./scripts/smoke-test.sh
#
# Requirements: bash + curl (no jq needed). Safe to re-run: room names get a per-run suffix.

set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
API_USER="${API_USER:-admin}"
API_PASS="${API_PASS:-admin123}"
API="$BASE_URL/api/v1"
RUN="$(date +%s)"
DAY="2031-06-15"          # far-future day so @Future validation always passes
NEXT_DAY="2031-06-16"

PASSED=0
FAILED=0
BODY=""
STATUS=""
HEADERS=""

green() { printf '\033[32m%s\033[0m' "$1"; }
red()   { printf '\033[31m%s\033[0m' "$1"; }
bold()  { printf '\033[1m%s\033[0m' "$1"; }

section() { printf '\n%s\n' "$(bold "▶ $1")"; }

# call METHOD PATH [curl args...]  -> sets STATUS, BODY, HEADERS
call() {
    local method="$1" url="$2"; shift 2
    local hfile; hfile="$(mktemp)"
    BODY="$(curl -s -X "$method" -D "$hfile" -o - -w '\n%{http_code}' "$@" "$url")"
    STATUS="${BODY##*$'\n'}"
    BODY="${BODY%$'\n'*}"
    HEADERS="$(cat "$hfile")"
    rm -f "$hfile"
}

# expect LABEL EXPECTED_STATUS [EXTRA_GREP_PATTERN]
expect() {
    local label="$1" expected="$2" pattern="${3:-}"
    if [[ "$STATUS" == "$expected" ]] && { [[ -z "$pattern" ]] || grep -q -- "$pattern" <<<"$BODY$HEADERS"; }; then
        PASSED=$((PASSED + 1)); printf '  %s %-62s %s\n' "$(green ✔)" "$label" "$STATUS"
    else
        FAILED=$((FAILED + 1)); printf '  %s %-62s got %s, expected %s %s\n' "$(red ✘)" "$label" "$STATUS" "$expected" "${pattern:+(+ /$pattern/)}"
        printf '      body: %.300s\n' "$BODY"
    fi
}

field() { sed -n "s/.*\"$1\":\"\{0,1\}\([^,\"}]*\).*/\1/p" <<<"$BODY" | head -1; }

post_json()  { local url="$1" data="$2"; shift 2; call POST "$url" -H 'Content-Type: application/json' --data "$data" "$@"; }
admin_post() { post_json "$1" "$2" -u "$API_USER:$API_PASS"; }

booking() {   # booking ROOM_ID PERSON START END
    printf '{"roomId": %s, "personInCharge": "%s", "startTime": "%s", "endTime": "%s"}' "$1" "$2" "$3" "$4"
}

printf '%s %s (run %s)\n' "$(bold '🧪 Meeting Scheduler smoke test against')" "$BASE_URL" "$RUN"

# ---------------------------------------------------------------------------
section "Platform: health, probes, docs, actuator security"
call GET "$BASE_URL/actuator/health";           expect "health is public and UP" 200 '"status":"UP"'
call GET "$BASE_URL/actuator/health/liveness";  expect "liveness probe" 200
call GET "$BASE_URL/actuator/health/readiness"; expect "readiness probe" 200
call GET "$BASE_URL/v3/api-docs";               expect "OpenAPI document" 200 'Meeting Scheduler API'
call GET "$BASE_URL/swagger-ui/index.html";     expect "Swagger UI" 200
call GET "$BASE_URL/actuator/metrics";          expect "metrics require auth" 401
call GET "$BASE_URL/actuator/metrics" -u "$API_USER:$API_PASS"; expect "metrics with auth" 200 'cache.gets'
call GET "$BASE_URL/actuator/info" -u "$API_USER:$API_PASS";    expect "build info" 200 '"artifact":"meeting-scheduler"'

# ---------------------------------------------------------------------------
section "Rooms: create (seed dataset), validation, conflicts"
post_json "$API/rooms" '{"name":"Ghost","capacity":2}'
expect "anonymous create is rejected" 401 'WWW-Authenticate'
post_json "$API/rooms" '{"name":"Ghost","capacity":2}' -u "$API_USER:wrong-password"
expect "wrong password is rejected" 401

declare -A ROOM
for spec in "Orion|8|2nd floor, east wing" "Vega|4|2nd floor, phone booth" "Andromeda|12|3rd floor, boardroom" "Lyra|6|Ground floor"; do
    IFS='|' read -r name capacity location <<<"$spec"
    admin_post "$API/rooms" "{\"name\":\"$name $RUN\",\"capacity\":$capacity,\"location\":\"$location\"}"
    expect "create room $name (capacity $capacity)" 201 'Location: .*/api/v1/rooms/'
    ROOM[$name]="$(field id)"
done

admin_post "$API/rooms" "{\"name\":\"orion $RUN\",\"capacity\":3}"
expect "duplicate name (case-insensitive) -> 409" 409 'already exists'
admin_post "$API/rooms" '{"name":"  ","capacity":0}'
expect "blank name + zero capacity -> 400 with field errors" 400 '"field":"capacity"'
admin_post "$API/rooms" '{"name":"Too big","capacity":9999}'
expect "capacity above 500 -> 400" 400 '"field":"capacity"'
admin_post "$API/rooms" '{"name": '
expect "malformed JSON -> 400" 400 'Malformed request'

section "Rooms: queries"
call GET "$API/rooms";                     expect "list rooms (public)" 200 "Andromeda $RUN"
call GET "$API/rooms/${ROOM[Orion]}";      expect "get room by id" 200 "Orion $RUN"
call GET "$API/rooms/999999999";           expect "unknown room -> 404" 404 'was not found'
call GET "$API/rooms/abc";                 expect "non-numeric id -> 400" 400 '"field":"id"'

# ---------------------------------------------------------------------------
section "Bookings: create, overlap rules, validation"
O="${ROOM[Orion]}"; V="${ROOM[Vega]}"; A="${ROOM[Andromeda]}"

admin_post "$API/bookings" "$(booking "$O" "Ada Lovelace" "${DAY}T09:00:00Z" "${DAY}T10:00:00Z")"
expect "Orion 09:00-10:00 (Ada)" 201 '"status":"ACTIVE"'
ADA="$(field id)"
admin_post "$API/bookings" "$(booking "$O" "Grace Hopper" "${DAY}T10:00:00Z" "${DAY}T11:00:00Z")"
expect "Orion 10:00-11:00 back-to-back is allowed" 201
GRACE="$(field id)"
admin_post "$API/bookings" "$(booking "$V" "Linus Torvalds" "${DAY}T09:00:00Z" "${DAY}T10:00:00Z")"
expect "Vega 09:00-10:00 same time, other room" 201
admin_post "$API/bookings" "$(booking "$A" "Margaret Hamilton" "${NEXT_DAY}T14:00:00Z" "${NEXT_DAY}T16:00:00Z")"
expect "Andromeda next day 14:00-16:00" 201

admin_post "$API/bookings" "$(booking "$O" "Intruder" "${DAY}T09:30:00Z" "${DAY}T10:30:00Z")"
expect "partial overlap -> 409" 409 'already booked'
admin_post "$API/bookings" "$(booking "$O" "Intruder" "${DAY}T09:15:00Z" "${DAY}T09:45:00Z")"
expect "slot inside existing booking -> 409" 409
admin_post "$API/bookings" "$(booking "$O" "Intruder" "${DAY}T08:00:00Z" "${DAY}T12:00:00Z")"
expect "slot wrapping two bookings -> 409" 409
admin_post "$API/bookings" "$(booking "$O" "Ada" "${DAY}T11:00:00Z" "${DAY}T10:00:00Z")"
expect "end before start -> 400 on endTime" 400 '"field":"endTime"'
admin_post "$API/bookings" "$(booking "$O" "Ada" "2001-01-01T09:00:00Z" "2001-01-01T10:00:00Z")"
expect "start in the past -> 400 on startTime" 400 '"field":"startTime"'
admin_post "$API/bookings" '{}'
expect "empty body -> 400 listing every field" 400 '"field":"personInCharge"'
admin_post "$API/bookings" "$(booking 999999999 "Ada" "${DAY}T13:00:00Z" "${DAY}T14:00:00Z")"
expect "unknown room -> 404" 404
post_json "$API/bookings" "$(booking "$O" "Anon" "${DAY}T15:00:00Z" "${DAY}T16:00:00Z")"
expect "anonymous booking -> 401" 401

section "Bookings: lookup, search, pagination"
call GET "$API/bookings/$ADA";             expect "get booking by id (public)" 200 '"personInCharge":"Ada Lovelace"'
call GET "$API/bookings/999999999";        expect "unknown booking -> 404" 404
call GET "$API/bookings?roomId=$O&date=$DAY"
expect "filter by room + day -> 2 results" 200 '"totalElements":2'
call GET "$API/bookings?roomId=$O&date=$DAY&size=1"
expect "page size 1 -> 2 pages" 200 '"totalPages":2'
call GET "$API/bookings?roomId=$O&date=$DAY&size=1&page=1"
expect "second page holds the later booking (Grace)" 200 'Grace Hopper'
call GET "$API/bookings?roomId=$A&date=$DAY"
expect "Andromeda has nothing on $DAY" 200 '"totalElements":0'
call GET "$API/bookings?roomId=$A&date=$NEXT_DAY"
expect "Andromeda booking found on $NEXT_DAY" 200 '"totalElements":1'
call GET "$API/bookings";                  expect "no filters -> paginated list" 200 '"page":0'
call GET "$API/bookings?size=500";         expect "size > 100 -> 400" 400 '"field":"size"'
call GET "$API/bookings?page=-1";          expect "negative page -> 400" 400 '"field":"page"'
call GET "$API/bookings?date=15-06-2031";  expect "bad date format -> 400" 400 '"field":"date"'

# ---------------------------------------------------------------------------
section "Bookings: cancellation (and cache eviction)"
call DELETE "$API/bookings/$ADA";                     expect "anonymous cancel -> 401" 401
call DELETE "$API/bookings/$ADA" -u "$API_USER:$API_PASS";    expect "cancel Ada's booking" 204
call DELETE "$API/bookings/$ADA" -u "$API_USER:$API_PASS";    expect "cancel twice -> 409" 409 'already cancelled'
call DELETE "$API/bookings/999999999" -u "$API_USER:$API_PASS"; expect "cancel unknown -> 404" 404
call GET "$API/bookings/$ADA";                        expect "cached booking now shows CANCELLED" 200 '"status":"CANCELLED"'
call GET "$API/bookings?roomId=$O&date=$DAY"
expect "cached search reflects cancellation" 200 '"status":"CANCELLED"'
admin_post "$API/bookings" "$(booking "$O" "Barbara Liskov" "${DAY}T09:00:00Z" "${DAY}T10:00:00Z")"
expect "freed slot can be booked again" 201

# ---------------------------------------------------------------------------
section "Concurrency: 10 simultaneous requests for one slot"
L="${ROOM[Lyra]}"
tmp="$(mktemp -d)"
for i in $(seq 1 10); do
    curl -s -o /dev/null -w '%{http_code}\n' -u "$API_USER:$API_PASS" -H 'Content-Type: application/json' \
        --data "$(booking "$L" "Racer $i" "${DAY}T12:00:00Z" "${DAY}T13:00:00Z")" \
        "$API/bookings" > "$tmp/$i" &
done
wait
created="$(cat "$tmp"/* | grep -c '^201$')"
conflicts="$(cat "$tmp"/* | grep -c '^409$')"
rm -rf "$tmp"
STATUS="$created/$conflicts"; BODY=""; HEADERS=""
expect "exactly one 201 and nine 409 (created/conflicts)" "1/9"

call GET "$BASE_URL/actuator/caches" -u "$API_USER:$API_PASS"
expect "caches endpoint lists Caffeine caches" 200 'bookingSearch'

# ---------------------------------------------------------------------------
printf '\n%s %s passed, %s failed\n' "$(bold 'Result:')" "$(green "$PASSED")" "$( ((FAILED)) && red "$FAILED" || echo 0)"
((FAILED == 0))
