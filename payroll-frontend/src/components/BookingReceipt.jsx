import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import bookingService from "../services/booking.service";
import {
  Paper,
  Typography,
  Grid,
  Divider,
  Container,
  Box,
  Card,
  CardContent,
} from "@mui/material";

const BookingReceipt = () => {
  const [booking, setBooking] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const response = await bookingService.get(id);
        setBooking(response.data);
      } catch (error) {
        console.error("Error al cargar la reserva:", error);
      }
    };

    fetchBooking();
  }, [id]);

  if (!booking) {
    return <Typography>Cargando...</Typography>;
  }

  // Función para obtener el nombre de la tarifa según la opción
  const getTarifaName = (optionFee) => {
    switch (optionFee) {
      case 1:
        return "Básica - $15.000 (30 min - 10 vueltas)";
      case 2:
        return "Estándar - $20.000 (35 min - 15 vueltas)";
      case 3:
        return "Premium - $25.000 (40 min - 20 vueltas)";
      default:
        return "Desconocida";
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Comprobante de Reserva
        </Typography>
        
        <Divider sx={{ mb: 3 }} />

        {/* Información básica de la reserva */}
        <Grid container spacing={2} sx={{ mb: 4 }}>
          <Grid item xs={6}>
            <Typography variant="subtitle2">RUT Cliente:</Typography>
            <Typography>{booking.personRUT}</Typography>
          </Grid>
          
          <Grid item xs={6}>
            <Typography variant="subtitle2">Fecha de Reserva:</Typography>
            <Typography>{new Date(booking.dateBooking).toLocaleDateString()}</Typography>
          </Grid>

          <Grid item xs={6}>
            <Typography variant="subtitle2">Número de Personas:</Typography>
            <Typography>{booking.numberOfPerson}</Typography>
          </Grid>

          <Grid item xs={6}>
            <Typography variant="subtitle2">Tarifa Seleccionada:</Typography>
            <Typography>{getTarifaName(booking.optionFee)}</Typography>
          </Grid>
        </Grid>

        {/* Caja de descuentos */}
        <Card variant="outlined" sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Descuentos Aplicados
            </Typography>
            
            {/* Descuento por grupo */}
            {booking.numberOfPerson >= 3 && (
              <Box sx={{ mb: 1 }}>
                <Typography variant="body2">
                  Descuento por grupo ({booking.numberOfPerson} personas):
                  {booking.numberOfPerson >= 11 ? " 30%" :
                   booking.numberOfPerson >= 6 ? " 20%" : " 10%"}
                </Typography>
              </Box>
            )}

            {/* Descuento por frecuencia */}
            {booking.client?.frecuency >= 2 && (
              <Box sx={{ mb: 1 }}>
                <Typography variant="body2">
                  Descuento por frecuencia ({booking.client.frecuency} visitas):
                  {booking.client.frecuency >= 7 ? " 30%" :
                   booking.client.frecuency >= 5 ? " 20%" : " 10%"}
                </Typography>
              </Box>
            )}

            {/* Descuento por cumpleaños */}
            {booking.client?.dateOfBirth === booking.dateBooking && booking.numberOfPerson >= 3 && (
              <Box sx={{ mb: 1 }}>
                <Typography variant="body2">
                  Descuento por cumpleaños: 50%
                </Typography>
              </Box>
            )}

            {/* Descuento por día especial */}
            {booking.especialDay && (
              <Box sx={{ mb: 1 }}>
                <Typography variant="body2">
                  Descuento por día especial: 5%
                </Typography>
              </Box>
            )}
          </CardContent>
        </Card>

        {/* Resumen de pago */}
        <Card variant="outlined">
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Resumen de Pago
            </Typography>
            
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography>Tarifa Base:</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography align="right">
                  ${booking.optionFee === 1 ? "15.000" :
                     booking.optionFee === 2 ? "20.000" : "25.000"}
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <Divider sx={{ my: 1 }} />
              </Grid>

              <Grid item xs={6}>
                <Typography>IVA (19%):</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography align="right">
                  ${(booking.optionFee === 1 ? 2850 :
                     booking.optionFee === 2 ? 3800 : 4750).toLocaleString()}
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <Divider sx={{ my: 1 }} />
              </Grid>

              <Grid item xs={6}>
                <Typography variant="h6">Total:</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="h6" align="right">
                  ${(booking.optionFee === 1 ? 17850 :
                     booking.optionFee === 2 ? 23800 : 29750).toLocaleString()}
                </Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
        <Box sx={{ mt: 2, mb: 1, display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
          <Typography 
            variant="body2" 
            color="success.main" 
            sx={{ 
              backgroundColor: 'success.light', 
              color: 'success.contrastText',
              padding: '8px 16px',
              borderRadius: '4px',
              display: 'inline-flex',
              alignItems: 'center'
            }}
          >
            ✓ Comprobante enviado al correo electrónico
          </Typography>
        </Box>

        <Typography variant="caption" display="block" sx={{ mt: 2, textAlign: 'right' }}>
          Emitido el: {new Date().toLocaleString()}
        </Typography>
      </Paper>
    </Container>
  );
};

export default BookingReceipt;