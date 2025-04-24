import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/clients/');
}

const create = data => {
    return httpClient.post("/api/v1/clients/", data);
}


export default { getAll, create};