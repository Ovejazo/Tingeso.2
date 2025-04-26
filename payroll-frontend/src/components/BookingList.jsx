import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import bookingService from "../services/booking.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import ReceiptIcon from '@mui/icons-material/Receipt';

const BookingList = () => {
  const [booking, setBooking] = useState([]);
  const navigate = useNavigate();

  const init = () => {
    bookingService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todos los clientes.", response.data);
        setBooking(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los clientes.",
          error
        );
      });
  };

  useEffect(() => {
    init();
  }, []);

  const handleDelete = (id) => {
    console.log("Printing id", id);
    const confirmDelete = window.confirm(
      "¿Esta seguro que desea borrar este cliente?"
    );
    if (confirmDelete) {
      bookingService
        .remove(id)
        .then((response) => {
          console.log("Cliente ha sido eliminado.", response.data);
          init();
        })
        .catch((error) => {
          console.log(
            "Se ha producido un error al intentar eliminar al cliente",
            error
          );
        });
    }
  };

  const handleEdit = (id) => {
    console.log("Printing id", id);
    navigate(`/booking/edit/${id}`);
  };

  const handleShowReceipt = (id) => {
    console.log("Mostrando comprobante para id:", id);
    navigate(`/booking/receipt/${id}`);
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/booking/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<PersonAddIcon />}
        >
          Añadir Booking
        </Button>
      </Link>
      <br /> <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Codigo
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Fecha de reserva
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Hora de inicio
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Hora de fin
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Tiempo limite
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Rut del cliente
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Grupo de la persona
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {booking.map((booking) => {
            const formatDate = (date) => {
              if (!date) return '';
              const options = {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
              };
              return new Date(date).toLocaleDateString('es-CL', options);
            };

            const formatTime = (date) => {
              if (!date) return '';
              const options = {
                hour: '2-digit',
                minute: '2-digit'
              };
              return new Date(date).toLocaleTimeString('es-CL', options);
            };

            const initialTime = formatTime(booking.initialTime);
            const finalTime = formatTime(booking.finalTime);
            const dateBooking = formatDate(booking.dateBooking);

            return (
              <TableRow
                key={booking.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="left">{booking.codigo}</TableCell>
                <TableCell align="left">{dateBooking}</TableCell>
                <TableCell align="left">{initialTime}</TableCell>
                <TableCell align="left">{finalTime}</TableCell>
                <TableCell align="right">{booking.limitTime}</TableCell>
                <TableCell align="right">{booking.personRUT}</TableCell>
                <TableCell align="right">{booking.numberOfPerson}</TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="info"
                    size="small"
                    onClick={() => handleEdit(booking.id)}
                    style={{ marginLeft: "0.5rem" }}
                    startIcon={<EditIcon />}
                  >
                    Editar
                  </Button>

                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    onClick={() => handleDelete(booking.id)}
                    style={{ marginLeft: "0.5rem" }}
                    startIcon={<DeleteIcon />}
                  >
                    Eliminar
                  </Button>

                  <Button
                    variant="contained"
                    color="success"
                    size="small"
                    onClick={() => handleShowReceipt(booking.id)}
                    style={{ marginLeft: "0.5rem" }}
                    startIcon={<ReceiptIcon />}
                  >
                    Comprobante
                  </Button>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default BookingList;