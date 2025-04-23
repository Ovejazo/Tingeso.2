import httpBooking from "../http-common";

const getAll = () => {
    return httpBooking.get('/api/v1/booking/');
}

const create = data => {
    return httpBooking.post("/api/v1/booking/", data);
}

export default { getAll, create};