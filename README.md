# 편의점

![image](https://github.com/user-attachments/assets/811be804-81f8-4fad-a5d2-ebf9f9fac646)

## 📚목차

- [🎥 시연 영상](#-시연-영상)
- [🚀 편의점 프로젝트 개요](#-편의점-프로젝트-개요)
- [📌 편의점 규칙 및 설명](#-편의점-규칙-및-설명)
    - [재고 관리](#재고-관리)
    - [프로모션 할인](#프로모션-할인)
    - [멤버십 할인](#멤버십-할인)
    - [영수증 출력](#영수증-출력)
- [💎 기능 목록](#-기능-목록)

## 🎥 시연 영상

![화면 기록 2](https://github.com/user-attachments/assets/34fe34e9-51d0-4340-97ab-162c38d4aded)

## 🚀 편의점 프로젝트 개요

이 프로젝트는 구매자의 할인 혜택과 재고 상황을 고려하여 최종 결제 금액을 계산하고 안내하는 결제 시스템입니다.

- 사용자가 입력한 상품의 가격과 수량을 기반으로 최종 결제 금액을 계산합니다.
- 총구매액은 상품별 가격과 수량을 곱하여 계산하며, 프로모션 및 멤버십 할인 정책을 반영하여 최종 결제 금액을 산출합니다.
- 구매 내역과 산출한 금액 정보를 영수증으로 출력합니다.
- 영수증 출력 후 추가 구매를 진행할지 또는 종료할지를 선택할 수 있습니다.

## 📌 편의점 규칙 및 설명

**재고 관리**

- 고객이 상품을 구매할 때마다, 결제된 수량만큼 해당 상품의 재고에서 차감됩니다.
- 재고를 차감함으로써 시스템은 최신 재고 상태를 유지하며, 다음 고객이 구매할 때 정확한 재고 정보가 제공됩니다.
- 재고는 처음 환영 인사 때 제공됩니다.
    - 상품명, 가격, 프로모션 이름, 재고가 안내됩니다.
        - 만약 재고가 0개라면 `재고 없음`이 출력됩니다.

<img width="264" alt="image" src="https://github.com/user-attachments/assets/94be4ffa-275d-4b1e-9a45-8e7dcd8347be">


**프로모션 할인**

- 오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용됩니다.
- 프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행됩니다.
- 1+1 또는 2+1 프로모션이 각각 지정된 상품에 적용되며, 동일 상품에 여러 프로모션이 적용되지 않습니다.
- 프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.
- 프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고가 사용됩니다.
    - 프로모션 기간 중이 아니라도 프로모션 재고가 우선적으로 차감됩니다. 이때 결제는 프로모션 혜택없이 진행됩니다.
- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음이 안내됩니다.
    - 예시(2+1 상품)
        - 고객이 2개만 가져왔을 시 증정 받을 수 있는 상품을 안내한다.
        - <안내 메시지> 현재 {상품명}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
            - Y: 증정 받을 수 있는 상품을 추가됩니다.
            - N: 증정 받을 수 있는 상품을 추가되지 않습니다.
        - 고객이 1개만 가져왔을 시 프로모션 혜택은 적용되지 않고 따로 안내가 되지 않는다.
- 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제해야하고 이를 안내받을 수 있습니다.
    - <안내 메시지> 현재 {상품명} {수량}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
        - Y: 일부 수량에 대해 정가로 결제한다.
        - N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.

**멤버십 할인**

- 멤버십 회원은 프로모션 미적용 금액의 30%를 할인이 적용됩니다.
- 프로모션 적용 후 남은 금액에 대해 멤버십 할인이 적용됩니다.
- 멤버십 할인의 최대 한도는 8,000원입니다.
- 최종 결제전 안내 메시지가 전달됩니다.
    - <안내 메시지> 멤버십 할인을 받으시겠습니까? (Y/N)
        - Y: 멤버십 할인이 적용됩니다.
        - N: 멤버십 할인이 적용되지 않습니다.

**영수증 출력**

- 영수증은 고객의 구매 내역과 할인을 요약하여 출력됩니다.
- 영수증 항목은 아래와 같습니다.
    - 구매 상품 내역: 구매한 상품명, 수량, 가격
    - 증정 상품 내역: 프로모션에 따라 무료로 제공된 증정 상품의 목록
    - 금액 정보
        - 총구매액: 구매한 상품의 총 수량과 총 금액
        - 행사할인: 프로모션에 의해 할인된 금액
        - 멤버십할인: 멤버십에 의해 추가로 할인된 금액
        - 내실돈: 최종 결제 금액
- 영수증은 구성 요소를 보기 좋게 정렬됩니다.

<img width="320" alt="image" src="https://github.com/user-attachments/assets/a82b976c-2673-426e-bab3-16e45a4c0ab8">

## 💎 기능 목록

- [x] (1) 재고 출력
    - [예외] 상품 재고 파일 경로가 올바르지 않을 때
    - [예외] 상품 재고 파일이 올바른 형식으로 작성되지 않았을 때
- [x] (2) 구매할 상품명과 수량 입력 받기
- [x] (3) 구매할 상품명과 수량 유효성 검사
    - 예외상황이라면 error 출력후 다시 입력
    - 프로모션 적용가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우 안내 메시지 출력후 추가여부 입력
    - 프로모션 재고가 부족하다면 일부 수량에 대해 정가로 결제할지 여부 입력
    - [예외] 구매할 상품과 수량 형식이 올바르지 않은 경우
    - [예외] 존재하지 않는 상품을 입력한 경우
    - [예외] 구매 수량이 재고 수량을 초과한 경우
    - [예외] 기타 잘못된 입력의 경우
- [x] (4) 멤버십 할인 적용 여부 입력 받기
    - [예외] Y/N이 아닌 잘못된 입력의 경우
- [x] (5) 구매 상품 내역, 증정 상품 내역, 금액 정보 출력
- [x] (6) 추가 구매 여부 입력 받기
    - yes 이면 다시 no이면 종료
    - [예외] Y/N이 아닌 잘못된 입력의 경우
