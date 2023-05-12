
# chapter 04. 액션에서 계산 빼내기 
입력과 출력은 명시적이거나 암묵적일 수 있음
- 명시적 입력 : 인자(param) 
- 암묵적 입력 : 인자 외 다른 입력(전역변수를 읽는 것) 
- 명시적 출력 : 리턴값 
- 암묵적 출력 : 리턴값 외 다른 출력 (전역변수를 바꾸는 것도 암묵적 출력)

### 함수에 암묵적 입력과 출력이 있다면 액션이 된다 
- 함수형 프로그래밍에서 암묵적 입력과 출력을 부수 효과라고 한다. 

```js
function cacl_cart_total() {
  shopping_cart_total = 0;
  for(var i = 0; i<shopping_car.length; i++){
    var item = shopping_cart[i];
    shopping_cart_total += item.price;
   }
  set_cart_total_dom();
  update_shipping_icons();
  update_tax_dom();
}
```

바꾼 코드 
```js 
function calc_cart_total() {
  calc_total();
  set_cart_total_dom();
  update_shipping_icons();
  update_tax_dom();
}

function calc_total() {
 shopping_cart_total = 0; //출력
 for(var i = 0; i < shopping_car.length; i++){ // shopping_car.length 입력
   var item = shopping_cart[i];
   shopping_cart_total += item.price; // 출력 
  }
}
```

- 서브루틴 추출 : 계산에 해당하는 코드를 function으로 변환하는 리팩토링 
- 하지만 여전히 새로 만든 함수도 액션 
  - 현재 전역변수(shopping_cart_total) 을 변경하므로 해당 변수를 지역변수로 하여 리턴하도록 처리 

```js
function calc_cart_total() {
  shopping_cart_total = calc_total();
  set_cart_total_dom();
  update_shipping_icons();
  update_tax_dom();
}

function calc_total() {
 var total = 0; 
 for(var i = 0; i < shopping_cart.length; i++){ // shopping_car.length 암묵적 입력
   var item = shopping_cart[i];
   total += item.price; 
  }
  return total; 
}
```

암묵적 입력 없애기 
```js 
function calc_total(cart) {
 var total = 0; 
 for(var i = 0; i < cart.length; i++){ 
   var item = cart[i];
   total += item.price; 
  }
  return total; 
}
```

정리 
- 계산코드를 찾아 빼내기
- 새 함수에 암묵적 입력과 출력을 찾는다. 
- 암묵적 입력은 이잔로 암묵적 출력은 리턴값으로 벼녁 
- 이를 통해 함수형 원칙을 적용시, 액션은 줄어들고 계싼은 늘어난다. 
