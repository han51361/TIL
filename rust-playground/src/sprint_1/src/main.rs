/***
상수는 mut 키워드를 사용할 수 없다. 상수는 기본 선언만으로 불변 속성이 아니라 항상 불변이다.
상수는 let 키워드 대신 const 키워드를 사용한다.
상수는 반드시 상수 표현식에서 사용해서 값을 할당해야한다.
러스트에서는 상수이름에 대문자만 사용하며 단어사이에 밑줄을 추가하는 규칙을 사용한다.
 */

/***

정수 타입


크기	부호 있음	부호 없음
8bit	i8	u8
16bit	i16	u16
32bit	i32	u32
64bit	i64	u64
arch	isize	usize
isize와 usize 타입은 컴퓨터의 아키텍처 환경에 따라 크기가 달라진다.
그리고 다음과 같이 정수 리터럴을 표현할 수 있다.
바이트를 제외한 모든 숫자 리터럴에는 57u8과 같이 타입 접미사(suffix)를 붙일 수 있고, 1_000 과 같이 밑줄을 이용해 자릿수를 표현할 수도 있다.



숫자 리터럴	예시
Decimal	98_222
Hex	0xff
Octal	0o77
Binary	0b1111_0000
Byte(u8전용)	b'A'


부동 소수점 타입

러스트의 부동 수수점 타입인 f32와 f64 타입은 각각 32bit와 64bit 크기다.
 러스트는 f64를 기본 타입으로 규정하고 있다. 부동 소수점 숫자는 IEEE754 표준에 따라 표현한다.
 f32타입은 단정도(single-precision) 부동 소수점이며, f64는 배정도(double-percision) 부동 소수점을 표현한다.
 */
fn main() {
    println!("Hello, world!");
    let mut x = 5;
    const MAX_POINTS: u32 = 100_000;

    let mut counter = 0;
    // 반복문에는 loop, while, for 존재
    let result = loop {
        counter += 1;

        if counter == 10 {
            break counter * 2;
        }
    };
}
