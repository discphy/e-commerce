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

// k6 run --out influxdb=http://localhost:8086/k6 k6/cache/rank.js --summary-trend-stats="avg,min,med,max,p(90),p(95),p(99)"