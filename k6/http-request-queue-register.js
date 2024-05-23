import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 200,
    duration: '10s'
};


export default function () {
    let userId = __VU;

    let payload = JSON.stringify({
        userId: userId ,
    });

    let headers = {
        'Content-Type': 'application/json',
    };

    let res = http.post('http://localhost:8081/concert/waiting/register', payload, { headers: headers });

    check(res, {
        'status was 200': (r) => r.status === 200,
    });

    sleep(1);
}
