import { useEffect, useState, useRef } from "react"; // Añadido useRef
import { useParams } from "react-router-dom";
import { useReactToPrint } from 'react-to-print'; // Añadido import
import html2pdf from 'html2pdf.js'; // Añadido import
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
  Button, // Añadido Button
  Stack, // Añadido Stack
} from "@mui/material";
import PrintIcon from '@mui/icons-material/Print';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf'; // Añadido PictureAsPdfIcon

const BookingReceipt = () => {
  const [voucher, setVoucher] = useState(null);
  const [booking, setBooking] = useState(null);
  const { id } = useParams();
  const componentRef = useRef();

  const handlePrint = useReactToPrint({
    content: () => componentRef.current,
  });

  // Función para generar PDF
  const generatePDF = () => {
    const element = componentRef.current;
    const opt = {
      margin: 1,
      filename: `comprobante-reserva-${id}.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2 },
      jsPDF: { unit: 'cm', format: 'a4', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save();
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Obtener el voucher basado en el ID de la reserva
        const voucherResponse = await bookingService.getVoucher(id);
        setVoucher(voucherResponse.data);
        
        // También obtenemos la reserva para mostrar datos adicionales
        const bookingResponse = await bookingService.get(id);
        setBooking(bookingResponse.data);
      } catch (error) {
        console.error("Error al cargar los datos:", error);
      }
    };

    fetchData();
  }, [id]);

  if (!voucher || !booking) {
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
      {/* Botones de acción */}
      <Stack 
        direction="row" 
        spacing={2} 
        sx={{ mb: 2 }}
        justifyContent="flex-end"
      >
        <Button 
          variant="contained" 
          startIcon={<PrintIcon />}
          onClick={handlePrint}
        >
          Imprimir
        </Button>
        <Button 
          variant="contained" 
          startIcon={<PictureAsPdfIcon />}
          onClick={generatePDF}
        >
          Descargar PDF
        </Button>
      </Stack>

      {/* Contenido para imprimir/PDF */}
      <Paper elevation={3} sx={{ p: 4 }} ref={componentRef}>
        <Typography variant="h4" gutterBottom>
          Comprobante de Reserva
        </Typography>
        
        <Divider sx={{ mb: 3 }} />

        {/* Información básica de la reserva */}
        <Grid container spacing={2} sx={{ mb: 4 }}>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Nombre Cliente:</Typography>
            <Typography>{voucher.name}</Typography>
          </Grid>
          
          <Grid item xs={6}>
            <Typography variant="subtitle2">RUT Cliente:</Typography>
            <Typography>{voucher.rut}</Typography>
          </Grid>
          
          <Grid item xs={6}>
            <Typography variant="subtitle2">Fecha de Reserva:</Typography>
            <Typography>{new Date(voucher.dateBooking).toLocaleDateString()}</Typography>
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

        <Card variant="outlined" sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Descuentos Aplicados
            </Typography>
            
            <Typography variant="body2">
              Descuento Total: {(voucher.discount != null ? (voucher.discount * 100).toFixed(0) : 0)}%
            </Typography>
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
                  ${voucher.fee.toLocaleString()}
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
                  ${voucher.iva.toLocaleString()}
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
                  ${((voucher.fee * (1 - voucher.discount)) + voucher.iva).toLocaleString()}
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
            ✓ Comprobante generado exitosamente
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