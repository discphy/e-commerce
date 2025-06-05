import http from 'k6/http';
import {sleep, check, group} from "k6";
import {randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '10s', target: 200 },
        { duration: '10s', target: 300 },
        { duration: '30s', target: 300 },
        { duration: '10s', target: 100 },
        { duration: '10s', target: 50 },
        { duration: '10s', target: 0 }
    ],
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.01']
    }
};

const BASE_URL = 'http://127.0.0.1:8080/api/v1';
const ORDER_CHECK_INTERVAL = 2; // 주문 상태 확인 간격(초)

export default function main() {
    // 1~1000 사이의 랜덤 사용자 ID 생성
    const userId = randomIntBetween(1, 1000);

    // 생성된 주문 ID를 저장할 변수
    let orderId = null;
    let shouldOrder = false;
    let shouldChargeBalance = false;
    let selectedProduct = null;

    group('주문/결제 시나리오', () => {
        // 1. 인기 상품 조회
        const popularProductsResponse = http.get(`http://127.0.0.1:8080/api/v0/products/ranks`, {
            tags: {name: '인기상품조회'}
        });

        check(popularProductsResponse, {
            '인기상품 조회 성공': (r) => r.status === 200,
            '인기상품 데이터 확인': (r) => {
                const body = JSON.parse(r.body);
                return body.data && Array.isArray(body.data.products) && body.data.products.length > 0;
            }
        });

        if (popularProductsResponse.status === 200) {
            const body = JSON.parse(popularProductsResponse.body);
            if (body.data && Array.isArray(body.data.products) && body.data.products.length > 0) {
                // 인기 상품 목록에서 랜덤하게 하나 선택
                const products = body.data.products;
                selectedProduct = products[Math.floor(Math.random() * products.length)];

                shouldChargeBalance = Math.random() < 0.2;
            }
        }

        // 2. 포인트 충전 및 조회 진행
        if (shouldChargeBalance) {
            const payload = JSON.stringify({
                amount: 10000
            });

            const params = {
                headers: {
                    'Content-Type': 'application/json',
                },
                tags: {name: '포인트충전'}
            };

            const chargeResponse = http.post(`${BASE_URL}/users/${userId}/balance/charge`, payload, params);

            check(chargeResponse, {
                '포인트 충전 성공': (r) => r.status === 200,
                '포인트 충전 확인': (r) => {
                    if (r.status === 200) {
                        const body = JSON.parse(r.body);
                        return body.code === 200 && body.message === 'OK';
                    }
                    return false;
                }
            });

            // 포인트 조회
            const balanceResponse = http.get(`${BASE_URL}/users/${userId}/balance`, {
                tags: {name: '포인트조회'}
            });

            check(balanceResponse, {
                '포인트 조회 성공': (r) => r.status === 200,
                '포인트 잔액 확인': (r) => {
                    if (r.status === 200) {
                        const body = JSON.parse(r.body);
                        return body.data && body.data.amount !== undefined;
                    }
                    return false;
                }
            });

            // 10% 확률로 주문 진행
            shouldOrder = Math.random() < 0.1;
        }

        // 3. 주문 결제 진행
        if (shouldOrder && selectedProduct) {
            const orderPayload = JSON.stringify({
                userId: userId,
                products: [
                    {
                        id: selectedProduct.id,
                        quantity: 1
                    }
                ]
            });

            const orderParams = {
                headers: {
                    'Content-Type': 'application/json',
                },
                tags: {name: '상품주문'}
            };

            const orderResponse = http.post(`${BASE_URL}/orders`, orderPayload, orderParams);

            check(orderResponse, {
                '주문 생성 성공': (r) => r.status === 200,
                '주문 확인': (r) => {
                    if (r.status === 200) {
                        const body = JSON.parse(r.body);
                        if (body.data && body.data.orderId) {
                            orderId = body.data.orderId;
                            return true;
                        }
                    }
                    return false;
                }
            });

            // 4. 주문 상태 확인
            if (orderId) {
                sleep(ORDER_CHECK_INTERVAL);

                const orderStatusResponse = http.get(`${BASE_URL}/orders/${orderId}`, {
                    tags: {name: '주문상태확인'}
                });

                check(orderStatusResponse, {
                    '주문 상태 조회 성공': (r) => r.status === 200,
                    '주문 상태 확인': (r) => {
                        if (r.status === 200) {
                            const body = JSON.parse(r.body);
                            if (body.data && body.data.status === 'COMPLETED') {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        } else {
            sleep(1);
        }
    });

    sleep(1);
}

