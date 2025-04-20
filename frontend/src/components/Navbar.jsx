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
  Box
} from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import MenuIcon from '@mui/icons-material/Menu';

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
    <AppBar position="static" color="primary" elevation={2}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ justifyContent: 'space-between' }}>
          <Typography
            variant="h6"
            component={RouterLink}
            to="/"
            sx={{
              fontWeight: 700,
              color: 'white',
              textDecoration: 'none',
              letterSpacing: '0.5px'
            }}
          >
            Telecom Shop
          </Typography>

          {isMobile ? (
            <Box>
              <IconButton
                edge="end"
                color="inherit"
                aria-label="menu"
                onClick={handleMenuOpen}
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
            </Box>
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Button 
                color="inherit" 
                component={RouterLink} 
                to="/"
                sx={{ mr: 2 }}
              >
                Products
              </Button>
              <IconButton 
                color="inherit" 
                component={RouterLink} 
                to="/cart"
                aria-label="cart"
              >
                <Badge badgeContent={itemCount} color="secondary">
                  <ShoppingCartIcon />
                </Badge>
              </IconButton>
            </Box>
          )}
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar; 