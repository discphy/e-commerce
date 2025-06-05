import http from 'k6/http';
import {sleep, check, group} from "k6";
import {randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import exec from 'k6/execution';

export const options = {
    stages: [
        {duration: '10s', target: 10},
        {duration: '10s', target: 10},
        {duration: '10s', target: 700},
        {duration: '10s', target: 10},
        {duration: '10s', target: 10},
        {duration: '10s', target: 1000},
        {duration: '10s', target: 10},
        {duration: '10s', target: 0}
    ],
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.05']
    },
};

const BASE_URL = 'http://127.0.0.1:8080/api/v0';

export default function main() {
    const userId = (exec.vu.idInTest * 1_000_000) + exec.vu.iterationInScenario;
    const couponId = 1;

    // 쿠폰 발급 요청
    group('쿠폰발급', () => {
        const payload = JSON.stringify({
            couponId: couponId
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
            },
            tags: {name: '쿠폰발급'}
        };

        const response = http.post(
            `${BASE_URL}/users/${userId}/coupons/publish`,
            payload,
            params
        );

        check(response, {
            '쿠폰 발급 성공': (r) => r.status === 200,
        });
    });

    sleep(randomIntBetween(1, 3));
}

