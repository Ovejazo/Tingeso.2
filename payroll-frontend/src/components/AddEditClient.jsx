import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const AddEditClient = () => {
  const [name, setName] = useState("");
  const [rut, setRut] = useState("");
  const [cash, setCash] = useState("");
  const [frecuency, setFrecuency] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const { id } = useParams();
  const [titleClientForm, setTitleClientForm] = useState("");
  const navigate = useNavigate();

  const saveClient = (e) => {
    e.preventDefault();

    const client = { name, rut, cash, frecuency, dateOfBirth, id };
    if (id) {
      //Actualizar Datos Empelado
      clientService
        .update(client)
        .then((response) => {
          console.log("Empleado ha sido actualizado.", response.data);
          navigate("/client/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar actualizar datos del empleado.",
            error
          );
        });
    } else {
      //Crear nuevo empleado
      clientService
        .create(client)
        .then((response) => {
          console.log("Empleado ha sido añadido.", response.data);
          navigate("/client/list");
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
      setTitleClientForm("Editar Empleado");
      clientService
        .get(id)
        .then((client) => {
          setName(client.data.name);
          setRut(client.data.rut);
          setCash(client.data.cash);
          setFrecuency(client.data.frecuency);
          setDateOfBirth(client.data.dateOfBirth);
        })
        .catch((error) => {
          console.log("Se ha producido un error.", error);
        });
    } else {
      setTitleClientForm("Nuevo Empleado");
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
      <h3> {titleClientForm} </h3>
      <hr />
      <form>
        <FormControl fullWidth>
          <TextField
            id="rut"
            label="Rut"
            value={rut}
            variant="standard"
            onChange={(e) => setRut(e.target.value)}
            helperText="Ej. 12.587.698-8"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="name"
            label="Name"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="cash"
            label="Cash"
            type="number"
            value={cash}
            variant="standard"
            onChange={(e) => setCash(e.target.value)}
            helperText="Salario mensual en Pesos Chilenos"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="frecuency"
            label="Frecuency"
            type="number"
            value={frecuency}
            variant="standard"
            onChange={(e) => setFrecuency(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="dateOfBirth"
            label="Fecha de Nacimiento"
            type="date"
            value={dateOfBirth}
            variant="standard"
            onChange={(e) => setDateOfBirth(e.target.value)}
            InputLabelProps={{
              shrink: true, // Esto asegura que la etiqueta no se superponga al valor
            }}
          />
        </FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveEmployee(e)}
            style={{ marginLeft: "0.5rem" }}
            startIcon={<SaveIcon />}
          >
            Grabar
          </Button>
        </FormControl>
      </form>
      <hr />
      <Link to="/client/list">Back to List</Link>
    </Box>
  );
};

export default AddEditClient;