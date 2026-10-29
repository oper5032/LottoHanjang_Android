# 로또 한장 출시 전 정책 확인

## 앱에 반영한 내용
- compileSdk/targetSdk 36, 복권 구매 사이트 이동 기능 제거.
- debug는 Google 테스트 ID, release는 운영 AdMob ID 사용.
- UMP 4.0.0으로 실행 시 동의 정보 갱신, 필요한 동의 폼 및 개인정보 선택 변경 제공.
- canRequestAds 확인 전 광고 SDK 초기화 및 광고 요청 차단. 광고 없이 무료 첫 번호와 기존 번호 이용 가능.
- 광고 최대 콘텐츠 등급 G 설정. 이 설정은 도박 광고 카테고리 차단 또는 앱의 연령 분류를 대체하지 않음.
- 광고 시청 전 선택·보상·중도 종료·자정 초기화 조건 안내.
- 앱 설정에 개인정보처리방침 본문 제공. 개발자·운영자: 제로&원, 문의: oper5032@gmail.com.

## 출시를 막는 미완료 항목
1. 개인정보처리방침에 제로&원 / oper5032@gmail.com 반영 완료. Play Console 공개 개발자명과 대조할 것.
2. 완성한 개인정보처리방침을 로그인 없이 열리는 공개 웹페이지에 게시하고 Play Console URL 입력. 앱 본문과 일치시킬 것.
3. AdMob 개인정보 보호 및 메시지에서 실제 앱 ID에 적용되는 메시지를 만들고 게시. 지원 지역별 동의 및 선택 변경을 기기에서 확인.
4. AdMob 차단 관리에서 도박·베팅 관련 광고 카테고리 확인 및 차단. 콘텐츠 등급 G만으로는 대체 불가.
5. Play Console 데이터 보안: 사용 중인 Mobile Ads 및 UMP SDK, 콘솔 설정과 배포 국가에 맞춰 작성. 단순 '수집 없음' 금지. 대략적인 위치(IP), 기기/기타 ID, 앱 상호작용, 진단·성능 데이터 등의 실제 수집·공유·목적·선택 여부 확인.
6. Play Console 광고 포함 여부, 광고 ID 사용, 대상 연령, 콘텐츠 등급, 배포 국가를 실제 내용대로 작성. 아동 대상/Families로 임의 등록하지 말 것.
7. 스토어 설명/스크린샷에 당첨 보장, 확률 향상, 공식 제휴, 현금·복권 지급을 암시하지 말 것.
8. 현재 Android Studio가 AGP 8.8.0까지만 지원하여 AGP 8.8.0 유지. compileSdk/targetSdk는 36 유지. SDK 36을 공식 지원하는 Android Studio 및 AGP로 함께 업데이트한 뒤 출시 빌드 검증 필요. 현재 조합에서는 SDK 지원 경고/추가 호환성 문제가 발생할 수 있음.
9. 서명된 release AAB 생성 및 실기기 테스트. 개인정보 동의 수락/거절/재설정, 오프라인, 광고 로드 실패, 중도 닫기, 보상 완료, 재실행, 자정 경계를 확인.

## 공식 참고
- https://support.google.com/googleplay/android-developer/answer/10144311
- https://developers.google.com/admob/android/privacy
- https://developers.google.com/admob/android/privacy/play-data-disclosure
- https://support.google.com/admob/answer/7313578
- https://support.google.com/googleplay/android-developer/answer/9877032

코드 보완은 심사 통과나 모든 법률·정책 준수를 보장하지 않으며 콘솔/실제 광고/스토어 정보까지 함께 검증해야 함.
