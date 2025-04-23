import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import bookingService from "../services/booking.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const AddEditBooking = () => {
  const [codigo, setCodigo] = useState("");
  const [dateBooking, setDateBooking] = useState("");
  const [initialTime, setInitialTime] = useState("");
  const [numberOfPerson, setNumberOfPerson] = useState("");
  const [finalTime, setFinalTime] = useState(""); // Cambiado de limiteTime a finalTime
  const [mainPerson, setMainPerson] = useState("");
  const [personRUT, setPersonRut] = useState("");
  const [optionFee, setOptionFee] = useState("1"); // Valor por defecto
  const [especialDay, setEspecialDay] = useState(false);
  const { id } = useParams();
  const [titleBookingForm, setTitleBookingForm] = useState("");
  const navigate = useNavigate();

  const formatDateTime = (date, time) => {
    if (!date || !time) return null;
    const [year, month, day] = date.split('-');
    const [hours, minutes] = time.split(':');
    return `${year}-${month}-${day}T${hours}:${minutes}:00`;
  };

  const saveBooking = (e) => {
    e.preventDefault();

    // Validaciones básicas
    if (!dateBooking || !initialTime || !numberOfPerson || !personRUT) {
      alert("Por favor complete todos los campos obligatorios");
      return;
    }

    // Crear la fecha actual para dateBooking
    const currentDate = new Date();
    const formattedCurrentDate = currentDate.toISOString();

    const booking = {
      codigo: parseInt(codigo),
      dateBooking: formattedCurrentDate, // Fecha y hora actual
      initialTime: formatDateTime(dateBooking, initialTime), // Fecha y hora de la reserva
      finalTime: formatDateTime(dateBooking, finalTime), // Fecha y hora final
      numberOfPerson: parseInt(numberOfPerson),
      limitTime: 30, // Este valor se calculará en el backend según la tarifa
      mainPerson: mainPerson,
      personRUT: personRUT,
      optionFee: parseInt(optionFee),
      especialDay: especialDay
    };

    console.log("Datos a enviar:", booking);

    if (id) {
      bookingService
        .update(booking)
        .then((response) => {
          console.log("Reserva actualizada:", response.data);
          navigate("/booking/list");
        })
        .catch((error) => {
          console.error("Error detallado:", error.response?.data);
          alert("Error al actualizar: " + (error.response?.data?.message || error.message));
        });
    } else {
      bookingService
        .create(booking)
        .then((response) => {
          console.log("Reserva creada:", response.data);
          navigate("/booking/list");
        })
        .catch((error) => {
          console.error("Error detallado:", error.response?.data);
          alert("Error al crear: " + (error.response?.data?.message || error.message));
        });
    }
  };

  // ... (useEffect se mantiene similar)

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
      sx={{ maxWidth: 600, mx: "auto", p: 2 }}
    >
      <h3>{titleBookingForm}</h3>
      <hr />
      <form style={{ width: "100%" }}>
        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="codigo"
            label="Código de Reserva"
            type="number"
            value={codigo}
            variant="outlined"
            onChange={(e) => setCodigo(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="dateBooking"
            label="Fecha de la Reserva"
            type="date"
            value={dateBooking}
            variant="outlined"
            InputLabelProps={{
              shrink: true,
            }}
            onChange={(e) => setDateBooking(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="initialTime"
            label="Hora de Inicio"
            type="time"
            value={initialTime}
            variant="outlined"
            InputLabelProps={{
              shrink: true,
            }}
            onChange={(e) => setInitialTime(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="finalTime"
            label="Hora Final"
            type="time"
            value={finalTime}
            variant="outlined"
            InputLabelProps={{
              shrink: true,
            }}
            onChange={(e) => setFinalTime(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="numberOfPerson"
            label="Número de Personas"
            type="number"
            value={numberOfPerson}
            variant="outlined"
            onChange={(e) => setNumberOfPerson(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="mainPerson"
            label="Persona Principal"
            value={mainPerson}
            variant="outlined"
            onChange={(e) => setMainPerson(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="personRUT"
            label="RUT"
            value={personRUT}
            variant="outlined"
            onChange={(e) => setPersonRut(e.target.value)}
            helperText="Formato: 12345678-9"
            required
          />
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="optionFee"
            label="Opción de Tarifa"
            select
            value={optionFee}
            variant="outlined"
            onChange={(e) => setOptionFee(e.target.value)}
            required
          >
            <MenuItem value="1">Tarifa A - $15.000 (30 min, 10 vueltas)</MenuItem>
            <MenuItem value="2">Tarifa B - $20.000 (35 min, 15 vueltas)</MenuItem>
            <MenuItem value="3">Tarifa C - $25.000 (40 min, 20 vueltas)</MenuItem>
          </TextField>
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <TextField
            id="especialDay"
            label="Día Especial"
            select
            value={especialDay}
            variant="outlined"
            onChange={(e) => setEspecialDay(e.target.value === "true")}
          >
            <MenuItem value="false">No</MenuItem>
            <MenuItem value="true">Sí</MenuItem>
          </TextField>
        </FormControl>

        <Button
          variant="contained"
          color="primary"
          onClick={saveBooking}
          startIcon={<SaveIcon />}
          fullWidth
        >
          Guardar Reserva
        </Button>
      </form>
      <Box sx={{ mt: 2 }}>
        <Link to="/extraHours/list">Volver a la Lista</Link>
      </Box>
    </Box>
  );
};

export default AddEditBooking;