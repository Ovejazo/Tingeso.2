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
  const [limiteTime, setLimiteTime] = useState("");
  const [mainPerson, setMainPerson] = useState("");
  const [personRUT, setPersonRut] = useState("");
  const [optionFee, setOptionFee] = useState("");
  const [especialDay, setEspecialDay] = useState("");
  const { id } = useParams();
  const [titleBookingForm, setTitleBookingForm] = useState("");
  const navigate = useNavigate();

  const saveBooking = (e) => {
    e.preventDefault();

    const booking = { codigo, dateBooking, initialTime, numberOfPerson, limiteTime, mainPerson, personRUT, optionFee, especialDay, id };
    if (id) {
      //Actualizar Datos Empelado
      bookingService
        .update(booking)
        .then((response) => {
          console.log("Empleado ha sido actualizado.", response.data);
          navigate("/booking/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar actualizar datos del empleado.",
            error
          );
        });
    } else {
      //Crear nuevo empleado
      bookingService
        .create(booking)
        .then((response) => {
          console.log("Empleado ha sido añadido.", response.data);
          navigate("/booking/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar crear nuevo empleado.",
            error
          );
        });
    }
  };

  useEffect(() => {
    if (id) {
      setTitleBookingForm("Editar Empleado");
      bookingService
        .get(id)
        .then((booking) => {
          setCodigo(booking.data.codigo);
          setDateBooking(booking.data.dateBooking);
          setEspecialDay(booking.data.especialDay);
          setInitialTime(booking.data.initialTime);
          setLimiteTime(booking.data.limiteTime);
          setMainPerson(booking.data.mainPerson);
          setNumberOfPerson(booking.data.numberOfPerson);
          setOptionFee(booking.data.optionFee);
          setPersonRut(booking.data.personRUT);
        })
        .catch((error) => {
          console.log("Se ha producido un error.", error);
        });
    } else {
      setTitleBookingForm("Nuevo Empleado");
    }
  }, []);

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
    >
      <h3> {titleBookingForm} </h3>
      <hr />
      <form>
        <FormControl fullWidth>
          <TextField
            id="codigo"
            label="codigo"
            value={codigo}
            variant="standard"
            onChange={(e) => setCodigo(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="dateBooking"
            label="dateBooking"
            value={dateBooking}
            variant="standard"
            onChange={(e) => setDateBooking(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="especialDay"
            label="especialDay"
            type="especialDay"
            value={especialDay}
            variant="standard"
            onChange={(e) => setEspecialDay(e.target.value)}
            helperText="Salario mensual en Pesos Chilenos"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="initialTime"
            label="initialTime"
            type="initialTime"
            value={initialTime}
            variant="standard"
            onChange={(e) => setInitialTime(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="limiteTime"
            label="limiteTime"
            value={limiteTime}
            variant="standard"
            onChange={(e) => setRut(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="mainPerson"
            label="mainPerson"
            value={mainPerson}
            variant="standard"
            onChange={(e) => setMainPerson(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="numberOfPerson"
            label="numberOfPerson"
            value={numberOfPerson}
            variant="standard"
            onChange={(e) => setNumberOfPerson(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="optionFee"
            label="optionFee"
            value={optionFee}
            select
            variant="standard"
            defaultValue="1"
            onChange={(e) => setOptionFee(e.target.value)}
            style={{ width: "25%" }}
          >
            <MenuItem value={"1"}>A</MenuItem>
            <MenuItem value={"2"}>B</MenuItem>
            <MenuItem value={"3"}>C</MenuItem>
          </TextField>
        </FormControl>
        
        <FormControl fullWidth>
          <TextField
            id="personRUT"
            label="personRUT"
            value={personRUT}
            variant="standard"
            onChange={(e) => setPersonRut(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveBooking(e)}
            style={{ marginLeft: "0.5rem" }}
            startIcon={<SaveIcon />}
          >
            Grabar
          </Button>
        </FormControl>
      </form>
      <hr />
      <Link to="/booking/list">Back to List</Link>
    </Box>
  );
};

export default AddEditBooking;