import { useState } from "react";
import { useNavigate } from "react-router-dom";
import clientService from "../services/employee.service"; // Asegúrate de que este servicio esté configurado para manejar la búsqueda de clientes
import { TextField, Button, Box } from "@mui/material";

const Login = () => {
  const [rut, setRut] = useState("");
  const [name, setName] = useState("");
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    // Revisamos en la base de datos si el cliente existe
    clientService
      .findByRutAndName(rut, name) // Este método debe estar implementado en el servicio
      .then((response) => {
        if (response.data) {
          console.log("Usuario encontrado:", response.data);
          alert("Inicio de sesión exitoso");
          navigate("/home"); // Redirige a la página principal
        } else {
          alert("Usuario no encontrado. Verifica los datos ingresados.");
        }
      })
      .catch((error) => {
        console.log("Error al intentar iniciar sesión:", error);
        alert("Ocurrió un error al intentar iniciar sesión.");
      });
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      sx={{ mt: 4 }}
    >
      <h2>Iniciar Sesión</h2>
      <form onSubmit={handleLogin}>
        <TextField
          label="Rut"
          variant="outlined"
          value={rut}
          onChange={(e) => setRut(e.target.value)}
          sx={{ mb: 2, width: "300px" }}
          helperText="Ej. 12.345.678-9"
        />
        <TextField
          label="Nombre"
          variant="outlined"
          value={name}
          onChange={(e) => setName(e.target.value)}
          sx={{ mb: 2, width: "300px" }}
        />
        <Button type="submit" variant="contained" color="primary">
          Iniciar Sesión
        </Button>
      </form>
    </Box>
  );
};

export default Login;