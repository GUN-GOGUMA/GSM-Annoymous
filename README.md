# GSM-Annoymous

GSM SMP용 닉네임/스킨 익명화 Paper 플러그인입니다.

## 기능

- UUID 기반 고정 익명 닉네임 생성
- `/hide_nick`로 닉네임 익명화 ON/OFF
- `/hide_skin`으로 Steve 스킨 익명화 ON/OFF
- 플레이어별 익명화 상태 저장
- 채팅, tab list, display name, nametag, join/quit, death, advancement 메시지에 익명 닉네임 반영
- 관리자용 reload/status/reset/regenerate 명령 제공

## 익명 닉네임 규칙

```text
gsm_{sha1(uuid + salt).slice(0,6)}
```

- 같은 UUID와 같은 salt는 항상 같은 익명 닉네임을 만듭니다.
- salt를 바꾸면 모든 익명 닉네임이 달라집니다.
- 운영 서버에서는 기본 salt를 반드시 개인 salt로 변경해야 합니다.

## 데이터 파일

```text
plugins/GSM-Annoymous/players.yml
```

UUID 기준으로 다음 값을 저장합니다.

- `hideNick`
- `hideSkin`
- `anonymousName`
- `lastKnownName`
- `updatedAt`

## 설정

`plugins/GSM-Annoymous/config.yml`

- `anonymous.prefix`: 익명 닉네임 prefix, 기본 `gsm_`
- `anonymous.salt`: 익명 닉네임 생성 salt
- `anonymous.hide-nick-by-default`: 최초 접속 시 닉네임 익명화 기본값
- `anonymous.hide-skin-by-default`: 최초 접속 시 스킨 익명화 기본값

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

## 빌드

```bat
gradlew.bat build
```

산출물:

```text
build/libs/GSM-Annoymous-1.0.0.jar
```

## 배포 위치

```text
servers/GSM-SMP/plugins/
```
