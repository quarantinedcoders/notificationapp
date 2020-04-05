import { API_BASE_URL, TOPIC_LIST_SIZE, ACCESS_TOKEN } from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response =>
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function getAllTopics(page, size) {
    page = page || 0;
    size = size || TOPIC_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/topic?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function createTopic(topicData) {
    return request({
        url: API_BASE_URL + "/topic",
        method: 'POST',
        body: JSON.stringify(topicData)
    });
}

export function createSubscribe(subscribeData) {
    return request({
        url: API_BASE_URL + "/subscription/" + subscribeData.topicId + "/subscribe",
        method: 'POST',
        body: JSON.stringify(subscribeData)
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/signin",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_BASE_URL + "/user/checkUsernameAvailability?username=" + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + "/user/checkEmailAvailability?email=" + email,
        method: 'GET'
    });
}


export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

export function getUserProfile(username) {
    return request({
        url: API_BASE_URL + "/user/" + username,
        method: 'GET'
    });
}

export function getUserCreatedTopics(username, page, size) {
    page = page || 0;
    size = size || TOPIC_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/topic/user/" + username + "/topics?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function getUserSubscribedTopics(username, page, size) {
    page = page || 0;
    size = size || TOPIC_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/topic/user/" + username + "/subscriptions?page=" + page + "&size=" + size,
        method: 'GET'
    });
}
