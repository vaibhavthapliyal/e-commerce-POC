import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  IconButton, 
  Badge,
  Container,
  useMediaQuery,
  useTheme,
  Menu,
  MenuItem,
  Box,
  Avatar
} from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import MenuIcon from '@mui/icons-material/Menu';
import StorefrontIcon from '@mui/icons-material/Storefront';

const Navbar = () => {
  const cartItems = useSelector(state => state.cart.items);
  const itemCount = cartItems.reduce((count, item) => count + item.quantity, 0);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [anchorEl, setAnchorEl] = useState(null);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <AppBar position="static" elevation={0} sx={{ backgroundColor: 'white', borderBottom: '1px solid #e0e0e0' }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ justifyContent: 'space-between', py: 1 }}>
          <Box component={RouterLink} to="/" sx={{ display: 'flex', alignItems: 'center', textDecoration: 'none' }}>
            <Avatar sx={{ bgcolor: 'primary.main', mr: 1 }}>
              <StorefrontIcon />
            </Avatar>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 700,
                color: 'primary.main',
                letterSpacing: '0.5px'
              }}
            >
              Telecom Shop
            </Typography>
          </Box>

          {isMobile ? (
            <Box>
              <IconButton
                color="primary"
                aria-label="menu"
                onClick={handleMenuOpen}
                sx={{ mr: 1 }}
              >
                <MenuIcon />
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
              >
                <MenuItem 
                  component={RouterLink} 
                  to="/" 
                  onClick={handleMenuClose}
                >
                  Products
                </MenuItem>
                <MenuItem 
                  component={RouterLink} 
                  to="/cart" 
                  onClick={handleMenuClose}
                >
                  Cart
                </MenuItem>
              </Menu>
              <IconButton
                component={RouterLink}
                to="/cart"
                color="primary"
                aria-label="cart"
              >
                <Badge badgeContent={itemCount} color="secondary">
                  <ShoppingCartIcon />
                </Badge>
              </IconButton>
            </Box>
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Button 
                component={RouterLink} 
                to="/" 
                color="primary"
                sx={{ 
                  fontWeight: 500, 
                  mr: 2,
                  '&:hover': { backgroundColor: 'rgba(233, 30, 99, 0.08)' }
                }}
              >
                Products
              </Button>
              <Button 
                component={RouterLink} 
                to="/cart"
                variant="contained"
                color="primary"
                startIcon={
                  <Badge badgeContent={itemCount} color="secondary">
                    <ShoppingCartIcon />
                  </Badge>
                }
                sx={{ borderRadius: 2, px: 2 }}
              >
                Cart
              </Button>
            </Box>
          )}
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar; 