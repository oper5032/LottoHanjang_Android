# 로또 한장

Kotlin + Jetpack Compose Android 앱. 최소 Android 8.0(API 26), compile/target SDK 35, JDK 17 이상.

## 실행

Android Studio에서 이 폴더를 열고 Gradle Sync 후 `app`을 실행합니다. SDK Manager에서 Android SDK Platform 35를 설치하세요. `local.properties`의 `sdk.dir`은 현재 컴퓨터 SDK 경로로 설정합니다.

```powershell
$env:JAVA_HOME = 'C:/Program Files/Android/Android Studio/jbr'
./gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

APK 경로: `app/build/outputs/apk/debug/app-debug.apk`

## 동작

- 매일 첫 게임 무료, 이후 보상형 광고 완료 콜백마다 즉시 1게임 생성. 무료 포함 최대 5게임.
- 각 게임은 SecureRandom으로 1~45에서 중복 없이 6개를 추출하고 정렬합니다.
- DataStore에 한국 날짜와 번호 목록을 함께 저장합니다. 제공 횟수는 저장된 게임 수입니다.
- Asia/Seoul 자정을 기준으로 초기화합니다. 실행 중 날짜 확인 및 앱 재진입/지급 직전 검증을 수행하며, 종료 중에는 다음 실행 때 초기화합니다. 기기의 시간대 설정과 무관합니다.
- 광고 실패/중간 종료는 지급하지 않습니다. 영속 보상 토큰을 한 번만 소비하며, 자정을 넘긴 이전 날짜 광고 보상은 지급하지 않습니다. 새 날짜 첫 게임은 무료입니다.
- Google 공식 테스트 앱 ID 및 보상형 광고 ID만 사용합니다. 광고는 인터넷과 Google Play 서비스가 있는 기기에서 확인하세요.
- 번호는 기기 내부에만 저장되며 백업은 비활성화했습니다. 앱 삭제/데이터 삭제 시 기록도 삭제됩니다. 날짜는 기기 시각에 의존합니다.

## 디자인 및 검증

Figma: https://www.figma.com/design/HyrRPDgDFZLlpM0tiu2HBY?node-id=2-30

Figma의 색상, 타이포 크기, 5줄 티켓, 번호 공, 제공 상태를 Compose로 구현했습니다. 상태바는 실제 Android 시스템 표시를 사용합니다. 한국어 글꼴은 시스템 sans-serif를 사용하며 Noto Sans KR 파일을 번들하지 않았습니다.

앱 아이콘은 제공된 `Brand/Icon & direction.png`의 왼쪽 160×160 영역을 사용합니다. 원본은 `design/brand-icon-and-direction.png`, 잘라낸 아이콘은 `design/launcher-icon.png`에 보관했습니다. mdpi~xxxhdpi PNG와 Android 8.0 이상 적응형 아이콘을 추가하고 Manifest의 일반/원형 런처 아이콘에 연결했습니다. 적응형 아이콘의 전경은 각 방향에 16% 여백을 적용해 원래 크기의 68%로 중앙 배치합니다. 녹색 배경은 전체 영역을 채워 원형 등의 런처 마스크에서도 티켓과 별이 잘리지 않게 합니다. 원본 아이콘 해상도는 160×160이므로 큰 크기에서는 선명도가 제한될 수 있습니다.

단위 테스트: 1,000회 번호 생성 규칙, 한국 자정 경계, 무료/보상 지급 및 5게임 한도.

검증 결과: 캐시된 Kotlin 컴파일러와 JUnit으로 핵심 로직 테스트 3개를 직접 실행해 모두 통과했습니다. 이 작업 환경에서는 네트워크 연결 및 Android SDK 접근 제한으로 전체 Android 빌드와 에뮬레이터 시각 검증은 완료하지 못했습니다. 기본 Wrapper는 배포 파일 다운로드 연결이 거부되었고, 설치된 Gradle로 시도한 오프라인 빌드는 필요한 Android 플러그인 캐시가 없어 중단되었습니다.

실기기 확인: 첫 무료 지급 → 앱 재실행 후 유지 → 광고 중간 종료 시 불변 → 완료 후 1게임 추가 → 5게임에서 버튼 비활성 → 한국 자정 후 초기화.

Google 보상형 광고 문서: https://developers.google.com/admob/android/rewarded
