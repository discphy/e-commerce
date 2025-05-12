import http from 'k6/http';
import { sleep, check, group } from "k6";

export const options = {
    stages: [
        { duration: '10s', target: 25 },
        { duration: '10s',target: 50 },
        { duration: '10s', target: 100 },
        { duration: '10s', target: 100 },
        { duration: '10s',target: 50 },
        { duration: '10s', target: 25 },
        { duration: '10s', target: 0 }
    ],
};

export default function main() {

    group("인기상품 캐싱 성능 테스트", function () {
        let url = 'http://127.0.0.1:8080/api/v1/products/ranks';

        const res = http.get(url);

        check(res, {
            '응답 상태 200': (r) => r.status === 200
        });
    });

    sleep(1);
}