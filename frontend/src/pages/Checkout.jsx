import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { updateCheckoutInfo, clearCheckoutInfo } from '../store/userSlice';
import { clearCart } from '../store/cartSlice';
import { paymentApi, orderApi } from '../api/api';
import ExitIntentModal from '../components/ExitIntentModal';
import {
  Box,
  Button,
  CircularProgress,
  Container,
  Grid,
  Paper,
  TextField,
  Typography,
  Divider,
  Alert,
} from '@mui/material';

// Initialize Stripe promise - in production you'd use your actual publishable key
const stripePromise = loadStripe('pk_test_TYooMQauvdEDq54NiTphI7jx');

const CheckoutForm = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const stripe = useStripe();
  const elements = useElements();
  
  const cartItems = useSelector(state => state.cart.items);
  const cartTotal = useSelector(state => state.cart.total);
  const checkoutInfo = useSelector(state => state.user.checkoutInfo);
  
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [formData, setFormData] = useState({
    name: checkoutInfo.name || '',
    email: checkoutInfo.email || '',
    phone: checkoutInfo.phone || '',
    address: checkoutInfo.address || '',
  });
  const [showExitIntent, setShowExitIntent] = useState(false);
  const [exitIntentShown, setExitIntentShown] = useState(false);

  // Redirect if cart is empty
  useEffect(() => {
    if (cartItems.length === 0) {
      navigate('/');
    }
  }, [cartItems, navigate]);

  // Set up exit intent detection
  useEffect(() => {
    if (exitIntentShown) return;

    const handleMouseLeave = (event) => {
      // Only trigger if mouse leaves at the top of the page
      if (
        event.clientY <= 0 && 
        !exitIntentShown && 
        !sessionStorage.getItem('checkoutExitShown')
      ) {
        setShowExitIntent(true);
        setExitIntentShown(true);
        sessionStorage.setItem('checkoutExitShown', 'true');
      }
    };

    document.addEventListener('mouseleave', handleMouseLeave);

    return () => {
      document.removeEventListener('mouseleave', handleMouseLeave);
    };
  }, [exitIntentShown]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    
    // Store checkout info in Redux
    dispatch(updateCheckoutInfo({ [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!stripe || !elements) {
      return;
    }
    
    setProcessing(true);
    setError(null);
    
    try {
      // Create payment intent on the server
      const response = await paymentApi.createPaymentIntent({
        orderId: `order-${Date.now()}`,
        amount: Math.round(cartTotal * 100), // Convert to cents
        paymentMethod: 'CREDIT_CARD',
        status: 'PENDING'
      });
      
      const { clientSecret } = response;
      
      // Confirm card payment
      const result = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: elements.getElement(CardElement),
          billing_details: {
            name: formData.name,
            email: formData.email,
            phone: formData.phone,
            address: {
              line1: formData.address
            }
          },
        },
      });
      
      if (result.error) {
        setError(result.error.message);
      } else if (result.paymentIntent.status === 'succeeded') {
        // Payment succeeded, create order in database
        const orderData = {
          items: cartItems.map(item => ({
            productId: item.id,
            quantity: item.quantity,
            price: item.finalPrice,
            name: item.name
          })),
          totalAmount: cartTotal,
          customer: {
            name: formData.name,
            email: formData.email,
            phone: formData.phone,
            address: formData.address
          },
          paymentId: result.paymentIntent.id,
          paymentStatus: 'PAID'
        };
        
        const orderResponse = await orderApi.createOrder(orderData);
        
        // Clear cart and checkout info
        dispatch(clearCart());
        dispatch(clearCheckoutInfo());
        
        // Redirect to order confirmation
        navigate('/order-confirmation', { 
          state: { 
            orderId: orderResponse.id || result.paymentIntent.id,
            total: cartTotal,
          } 
        });
      }
    } catch (err) {
      console.error('Payment error:', err);
      setError('Payment failed. Please try again.');
    } finally {
      setProcessing(false);
    }
  };

  const handleCloseExitIntent = () => {
    setShowExitIntent(false);
  };

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        color: '#424770',
        '::placeholder': {
          color: '#aab7c4',
        },
      },
      invalid: {
        color: '#9e2146',
      },
    },
  };

  return (
    <Box sx={{ py: 4 }}>
      <Container maxWidth="md">
        <Typography variant="h4" component="h1" gutterBottom>
          Checkout
        </Typography>
        
        <Paper elevation={2} sx={{ p: 3, mt: 3 }}>
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  Contact Information
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  id="name"
                  name="name"
                  label="Full Name"
                  value={formData.name}
                  onChange={handleInputChange}
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  id="email"
                  name="email"
                  label="Email Address"
                  type="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  id="phone"
                  name="phone"
                  label="Phone Number"
                  type="tel"
                  value={formData.phone}
                  onChange={handleInputChange}
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  id="address"
                  name="address"
                  label="Billing Address"
                  multiline
                  rows={3}
                  value={formData.address}
                  onChange={handleInputChange}
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12}>
                <Divider sx={{ my: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Payment Details
                </Typography>
                <Box 
                  sx={{ 
                    border: '1px solid rgba(0, 0, 0, 0.23)', 
                    borderRadius: 1, 
                    p: 2,
                    '&:focus-within': {
                      borderColor: 'primary.main',
                      boxShadow: '0 0 0 2px rgba(25, 118, 210, 0.2)'
                    }
                  }}
                >
                  <CardElement options={cardElementOptions} />
                </Box>
              </Grid>
              
              {error && (
                <Grid item xs={12}>
                  <Alert severity="error">{error}</Alert>
                </Grid>
              )}
              
              <Grid item xs={12}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                  <Typography variant="h6">
                    Total: ${cartTotal.toFixed(2)}
                  </Typography>
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    size="large"
                    disabled={!stripe || processing}
                    startIcon={processing && <CircularProgress size={20} color="inherit" />}
                  >
                    {processing ? 'Processing...' : `Pay Now`}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        </Paper>
      </Container>
      
      {/* Exit intent modal */}
      {showExitIntent && (
        <ExitIntentModal onClose={handleCloseExitIntent} />
      )}
    </Box>
  );
};

const Checkout = () => {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutForm />
    </Elements>
  );
};

export default Checkout; 