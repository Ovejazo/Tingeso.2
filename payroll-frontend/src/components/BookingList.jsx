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
import ReceiptIcon from '@mui/icons-material/Receipt'; // Nuevo import para el ícono de comprobante

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

  // Nueva función para manejar la visualización del comprobante
  const handleShowReceipt = (id) => {
    console.log("Mostrando comprobante para id:", id);
    // Aquí puedes agregar la lógica para mostrar el comprobante
    // Por ejemplo, navegar a una nueva ruta o abrir un modal
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
          {booking.map((booking) => (
            <TableRow
              key={booking.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="left">{booking.codigo}</TableCell>
              <TableCell align="left">{booking.dateBooking}</TableCell>
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
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default BookingList;