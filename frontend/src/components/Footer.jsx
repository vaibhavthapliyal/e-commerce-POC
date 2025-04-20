import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Box, 
  Container, 
  Grid, 
  Typography, 
  Link, 
  Divider
} from '@mui/material';

const Footer = () => {
  return (
    <Box component="footer" sx={{ bgcolor: 'grey.900', color: 'white', py: 6, mt: 'auto' }}>
      <Container maxWidth="lg">
        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Telecom Shop
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ color: 'grey.400', mb: 2 }}>
              Your trusted provider of telecom services and devices.
            </Typography>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Quick Links
            </Typography>
            <Box component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
              <Box component="li" sx={{ mb: 1 }}>
                <Link 
                  component={RouterLink} 
                  to="/" 
                  color="inherit" 
                  sx={{ '&:hover': { color: 'grey.400' } }}
                  underline="hover"
                >
                  Products
                </Link>
              </Box>
              <Box component="li" sx={{ mb: 1 }}>
                <Link 
                  component={RouterLink} 
                  to="/cart" 
                  color="inherit" 
                  sx={{ '&:hover': { color: 'grey.400' } }}
                  underline="hover"
                >
                  Cart
                </Link>
              </Box>
            </Box>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Contact Us
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ color: 'grey.400', mb: 1 }}>
              123 Telecom Street
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ color: 'grey.400', mb: 1 }}>
              Phone: (123) 456-7890
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ color: 'grey.400', mb: 1 }}>
              Email: support@telecomshop.com
            </Typography>
          </Grid>
        </Grid>
        
        <Divider sx={{ borderColor: 'grey.800', my: 4 }} />
        
        <Typography variant="body2" color="text.secondary" align="center" sx={{ color: 'grey.500' }}>
          &copy; {new Date().getFullYear()} Telecom Shop. All rights reserved.
        </Typography>
      </Container>
    </Box>
  );
};

export default Footer; 