import React from 'react';
import FlashDiscountTimer from './FlashDiscountTimer';
import {
  Card,
  CardActions,
  CardContent,
  CardMedia,
  Button,
  Typography,
  Box,
  Chip,
  Rating
} from '@mui/material';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import DevicesIcon from '@mui/icons-material/Devices';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import SimCardIcon from '@mui/icons-material/SimCard';

// Reliable static images
const FALLBACK_IMAGES = {
  DEFAULT: '/images/fallback-product.jpg',
  TARIFF: '/images/plan-fallback.jpg',
  APPLE: '/images/apple-fallback.jpg',
  SAMSUNG: '/images/samsung-fallback.jpg',
  GOOGLE: '/images/google-fallback.jpg',
  HUAWEI: '/images/huawei-fallback.jpg',
  DEVICE: '/images/device-fallback.jpg',
};

const ProductCard = ({ product, onAddToCart }) => {
  const hasDiscount = product.discountPercentage && product.discountExpiry;
  
  const handleAddToCart = () => {
    // Call the provided callback
    onAddToCart(product, 1);
    
    // Request notification permission on first add
    if (Notification.permission === 'default') {
      try {
        Notification.requestPermission().then(permission => {
          // This would typically call an API to update user preferences
          console.log('Notification permission:', permission);
        });
      } catch (error) {
        console.error('Error requesting notification permission:', error);
      }
    }
  };
  
  // Calculate the discounted price if applicable
  const finalPrice = hasDiscount 
    ? product.price * (1 - (product.discountPercentage / 100)) 
    : product.price;
    
  // Get appropriate product image based on type (using very reliable placeholder service)
  const getProductImage = () => {
    // Use default reliable placeholder
    return `https://placehold.co/400x250/e91e63/ffffff?text=${encodeURIComponent(product.name)}`;
  };

  return (
    <Card 
      sx={{ 
        height: '100%', 
        display: 'flex', 
        flexDirection: 'column', 
        transition: 'transform 0.3s, box-shadow 0.3s',
        '&:hover': {
          transform: 'translateY(-8px)',
          boxShadow: '0 12px 20px rgba(0, 0, 0, 0.1)',
        }
      }}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="200"
          image={getProductImage()}
          alt={product.name}
          sx={{ objectFit: 'cover' }}
        />
      </Box>
      
      {/* Separate chips from content to prevent overlap */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        p: 1.5, 
        pb: 0 
      }}>
        <Chip
          icon={product.type === 'tariff' ? <SimCardIcon /> : <PhoneIphoneIcon />}
          label={product.type === 'tariff' ? 'Plan' : 'Device'}
          size="small"
          color="primary"
          variant="filled"
          sx={{ fontWeight: 'medium' }}
        />
        
        {hasDiscount && (
          <Chip
            label={`${product.discountPercentage}% OFF`}
            color="secondary"
            size="small"
            sx={{ fontWeight: 'bold', px: 1 }}
          />
        )}
      </Box>
      
      <CardContent sx={{ flexGrow: 1, p: 2.5, pt: 1.5 }}>
        <Typography gutterBottom variant="h6" component="div" sx={{ fontWeight: 'medium', mb: 1 }}>
          {product.name}
        </Typography>
        
        {product.type === 'device' && (
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
            <Rating value={4.5} precision={0.5} size="small" readOnly />
            <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>
              4.5
            </Typography>
          </Box>
        )}
        
        <Typography 
          variant="body2" 
          color="text.secondary" 
          sx={{ 
            mb: 2, 
            height: '3em', 
            overflow: 'hidden',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical'
          }}
        >
          {product.description}
        </Typography>
        
        {product.type === 'tariff' && product.dataAllowance && (
          <Chip 
            label={`${product.dataAllowance} Data`}
            size="small"
            color="primary"
            variant="outlined"
            sx={{ mb: 2 }}
          />
        )}
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Box>
            {hasDiscount ? (
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Typography 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ textDecoration: 'line-through', mr: 1 }}
                >
                  ${product.price.toFixed(2)}
                </Typography>
                <Typography variant="h6" color="secondary" fontWeight="bold">
                  ${finalPrice.toFixed(2)}
                </Typography>
              </Box>
            ) : (
              <Typography variant="h6" color="primary" fontWeight="bold">
                ${product.price.toFixed(2)}
              </Typography>
            )}
          </Box>
        </Box>
        
        {hasDiscount && (
          <Box sx={{ mb: 1 }}>
            <FlashDiscountTimer expiryTime={product.discountExpiry} />
          </Box>
        )}
      </CardContent>
      
      <CardActions sx={{ p: 2, pt: 0 }}>
        <Button 
          fullWidth 
          variant="contained" 
          color="primary"
          startIcon={<AddShoppingCartIcon />}
          onClick={handleAddToCart}
          sx={{ borderRadius: 2 }}
        >
          Add to Cart
        </Button>
      </CardActions>
    </Card>
  );
};

export default ProductCard; 