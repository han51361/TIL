# chap 05. 더 좋은 액션 만들기 


냄새 나는 코드 = 중복이 존재하는 코드 

- 비즈니스 요구사항 : 장바구니에 담긴 제품을 주문할 때 무료배송인지 확인하는 것 
- 기존 코드 : 
```js 
function gets_free_shipping(total, item_price) {
  return item_price + total >= 20;
}
```
해당 코드는 요구사항에 맞지 않게 제품의 합계 + 가격으로 확인 중이므로 이를 장바구니라는 엔티티로 변경
- 변경 코드 : 
```js 
function gets_free_shipping(cart) {
  return cart >= 20;
}
```

해당 코드를 사용하는 부분도 변경 
- 기존코드 : 
```js 
function update_shipping_icons() {
 ...
 for (var i = 0; i < buttons.length; i++) {
   var button = buttons[i];
   var item = button.item;
   
   if (gets_free_shipping(   // 기존 사용 코드 
         shopping_cart_total,
         item.price))
        button.show_free_shipping_icon();
    else 
        button.hide_free_shipping_icon(); 
   }
}
```

- 새 시그니처를 적용한 코드 
```js 
function update_shipping_icons() {
 ...
 for (var i = 0; i < buttons.length; i++) {
   var button = buttons[i];
   var item = button.item;
   var new_cart = add_item(shopping_cart, item.name, item.price);
   
   if (gets_free_shipping(new_cart))
        button.show_free_shipping_icon();
    else 
        button.hide_free_shipping_icon(); 
   }
}
```

### 원칙 : 암묵적 입력과 출력은 적을수록 좋다. 
해당 함수에서 암묵적 입력과 출력이 있다면 다른 컴포넌트와 강하게 연결된 컴포넌트 
- 다른 곳에서 사용할 수 없으므로 모듈이 아님 
- 압묵적 입력과 출력이 있는 함수는 아무 때나 실행할 수 없기 때문에 테스트하기 어려움 

### 원칙 : 설계는 엉켜있는 코드를 푸는 것 
- 함수를 통해 관심사를 자연스럽게 분리하도록 하자. 
- 재사용하기 쉽다. 
- 유지보수하기 쉽다
- 테스트하기 쉽다

장바구니에 아이템을 추가하는 함수를 예시로 보자 
```js 
function add_item(cart, name, price) {
  var new_cart = cart.slice(); // 복사 
  new_cart.push({
    name : name,  // item 객체 생성 , push 를 통해 복사본에 아이템 추가 
    price: price,
  }); 
  return new_cart; // 복사본 리턴 
}
```
item 객체 생성 코드 분리하기 
```js
function make_cart_item(name, price) {
  return {
    name: name,
    price: price
  }
}

function add_item(cart, item) {
  var new_cart = cart.slice();
  new_cart.push(item);
  return new_cart;
}

add_item(shopping_cart, make_cart_item("shoe",3.45));
}
```

- item 구조만 알고있는 함수(make_cart_iem)와 cart 구조만 알고 있는 함수(add_item)으로 나눠 고침 
- 복사하는 카피-온-라이트를 구현한 부분이므로 함께 두는 것이 좋다. 

### copy-on-write 패턴 빼내기 
앞선 add_item() 함수는 일반적인 함수이므로 다른 곳에서도 사용할 수 있다. 
- 어떤 배열이든 항목에도 쓸 수 있으므로 범용적으로 변경 
```js
function add_element_last(array, elem) {
 var new_array = array.slice();
 new_array.push(elem)
 return new_array;
}

function add_item(cart, item) {
  return add_element_last(cart, item);
}
```
