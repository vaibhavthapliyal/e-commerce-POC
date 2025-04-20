import React, { useEffect, useState } from 'react';
import { Link as RouterLink, useLocation, Navigate } from 'react-router-dom';
import { orderApi } from '../api/api';
import {
  Box,
  Button,
  Container,
  Paper,
  Typography,
  Divider,
  CircularProgress,
  Alert,
} from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';

const OrderConfirmation = () => {
  const location = useLocation();
  const { orderId, total } = location.state || {};
  const [orderDetails, setOrderDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // If no order info is provided, redirect to home
  if (!orderId) {
    return <Navigate to="/" />;
  }

  // Fetch order details from the API
  useEffect(() => {
    const fetchOrderDetails = async () => {
      if (!orderId) return;
      
      setLoading(true);
      try {
        const response = await orderApi.getOrderById(orderId);
        setOrderDetails(response);
        setError(null);
      } catch (err) {
        console.error('Error fetching order details:', err);
        setError('Unable to fetch order details. Please contact customer support.');
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetails();
  }, [orderId]);

  // Calculate estimated delivery date (5 business days from now)
  const getEstimatedDelivery = () => {
    const deliveryDate = new Date();
    let businessDays = 5;
    
    while (businessDays > 0) {
      deliveryDate.setDate(deliveryDate.getDate() + 1);
      
      // Skip weekends
      if (deliveryDate.getDay() !== 0 && deliveryDate.getDay() !== 6) {
        businessDays--;
      }
    }
    
    return deliveryDate.toLocaleDateString('en-US', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  return (
    <Container maxWidth="md" sx={{ py: 6 }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 2 }}>
        {loading ? (
          <Box display="flex" justifyContent="center" my={8}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mb: 4 }}>
            {error}
          </Alert>
        ) : (
          <>
            <Box textAlign="center" mb={4}>
              <CheckCircleOutlineIcon
                color="success"
                sx={{ fontSize: 64, mb: 2 }}
              />
              
              <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
                Order Confirmed!
              </Typography>
              
              <Typography color="text.secondary" paragraph>
                Thank you for your purchase.
              </Typography>
            </Box>
            
            <Divider sx={{ my: 3 }} />
            
            <Box>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="body1" color="text.secondary">
                  Order ID:
                </Typography>
                <Typography variant="body1" fontWeight="medium">
                  {orderId}
                </Typography>
              </Box>
              
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="body1" color="text.secondary">
                  Total Amount:
                </Typography>
                <Typography variant="body1" fontWeight="medium">
                  ${(orderDetails?.totalAmount || total).toFixed(2)}
                </Typography>
              </Box>
              
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="body1" color="text.secondary">
                  Estimated Delivery:
                </Typography>
                <Typography variant="body1" fontWeight="medium">
                  {getEstimatedDelivery()}
                </Typography>
              </Box>
              
              {orderDetails?.customer && (
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                  <Typography variant="body1" color="text.secondary">
                    Shipping To:
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {orderDetails.customer.name}
                  </Typography>
                </Box>
              )}
            </Box>
            
            <Divider sx={{ my: 3 }} />
            
            <Box textAlign="center">
              <Typography color="text.secondary" paragraph>
                We've sent a confirmation email with all the details of your order.
                If you have any questions, please contact our customer support.
              </Typography>
              
              <Button 
                component={RouterLink} 
                to="/"
                variant="contained"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Continue Shopping
              </Button>
            </Box>
          </>
        )}
      </Paper>
    </Container>
  );
};

export default OrderConfirmation; 