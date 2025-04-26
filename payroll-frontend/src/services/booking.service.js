import httpBooking from "../http-common";

const getAll = () => {
    return httpBooking.get('/api/v1/booking/');
}

const create = data => {
    return httpBooking.post("/api/v1/booking/", data);
}

const remove = id => {
    return httpBooking.delete(`/api/v1/booking/${id}`);
}

const get = id => {
    return httpBooking.get(`/api/v1/booking/${id}`);
}

const getVoucher = id => {
    return httpBooking.get(`/api/v1/booking/voucher/${id}`); // Cambia la URL según tu API
}

export default { getAll, create, remove, get, getVoucher};