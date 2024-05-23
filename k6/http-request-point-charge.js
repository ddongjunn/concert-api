import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 100 }, // 30초 동안 100명까지 증가
        { duration: '1m', target: 200 }, // 1분 동안 200명까지 증가
        { duration: '30s', target: 0 }, // 30초 동안 사용자 감소
    ]
};

let iterationCount = 0;
export default function () {
    let userId = __VU;
    let point = generateRandomLong(1);

    charge(userId, point);
    get(userId);

    iterationCount++;
    sleep(1);
}

function charge(userId, point) {
    let headers = {
        'Content-Type': 'application/json',
    };

    let payload = JSON.stringify({
        userId: userId ,
        point: point
    });

    let res = http.patch('http://localhost:8080/point', payload, { headers: headers });

    check(res, {
        'status was 200': (r) => r.status === 200,
    });
}

function get(userId) {
    let res = http.get('http://localhost:8080/point/' + userId);

    check(res, {
        'status was 200': (r) => r.status === 200,
    });
}

function generateRandomLong(length) {
    const digits = '0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        let randomIndex = Math.floor(Math.random() * digits.length);
        result += digits[randomIndex];
    }
    return parseInt(result);
}

export function teardown() {
    let totalTime = __ENV.DURATION ? parseInt(__ENV.DURATION) : 120; // Total test duration in seconds
    let tps = iterationCount / totalTime;
    console.log(`Total Iterations: ${iterationCount}`);
    console.log(`Total Time (seconds): ${totalTime}`);
    console.log(`TPS (Transactions Per Second): ${tps.toFixed(2)}`);
}