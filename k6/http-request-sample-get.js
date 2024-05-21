import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 500,
    duration: '30s',
};


export default function () {
    let userId = __VU;
    console.log(userId);

    let payload = JSON.stringify({
        userId: userId,
    });

    let headers = {
        'Content-Type': 'application/json',
    };

    let res = http.post('http://localhost:8080/concert/waiting/register', payload, { headers: headers });

    check(res, {
        'status was 200': (r) => r.status === 200,
    });

    sleep(0.5);
}