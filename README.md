# GSM-Annoymous

## 기능

- UUID 기준 고정 익명 닉네임 생성
- `/hide_nick` 명령어로 닉네임 익명 ON/OFF
- `/hide_skin` 명령어로 Steve 스킨 익명 ON/OFF
- 유저별 익명 상태를 `players.yml`에 저장
- 채팅, tab list, display name, nametag, 입장/퇴장, 사망, 도전과제 메시지에 익명 닉네임 반영
- 설정 리로드, 상태 조회, 초기화, 익명 닉네임 재생성을 위한 관리자 명령어 제공

## 익명 닉네임

익명 닉네임은 플레이어 UUID와 설정된 salt를 기준으로 생성합니다.

```text
gsm_{sha1(uuid + salt).slice(0,6)}
```

UUID를 기준으로 하므로 마인크래프트 닉네임을 바꿔도 익명 닉네임은 유지됩니다. `anonymous.salt`를 바꾸면 생성되는 익명 닉네임이 달라집니다.

## 데이터 저장

유저별 상태는 아래 파일에 저장됩니다.

```text
plugins/GSM-Annoymous/players.yml
```

각 플레이어는 UUID 기준으로 저장되며, 다음 값을 가집니다.

- `hideNick`
- `hideSkin`
- `anonymousName`
- `lastKnownName`
- `updatedAt`

## 명령어

```text
/hide_nick
/hide_skin
/annoymous reload
/annoymous status <player>
/annoymous reset <player>
/annoymous regenerate <player>
```

## 권한

```text
gsmannoymous.command.hide_nick
gsmannoymous.command.hide_skin
gsmannoymous.admin.reload
gsmannoymous.admin.status
gsmannoymous.admin.reset
```

## 주의사항

- 운영 서버에서 사용하기 전에 `anonymous.salt`를 반드시 개인 salt 값으로 변경해야 합니다.
- 실제 스킨 복구는 플레이어가 접속할 때 캐시한 실제 프로필 텍스처를 사용합니다.
- 실제 스킨 캐시가 없는 상태라면 스킨 복구를 위해 재접속이 필요할 수 있습니다.
- nametag 익명화는 Bukkit/Paper의 `customName`을 사용합니다. 최종 표시 방식은 실서버에서 직접 확인해야 합니다.
