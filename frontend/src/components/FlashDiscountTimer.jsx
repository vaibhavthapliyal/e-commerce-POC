import React from 'react';
import Countdown from 'react-countdown';
import { Box, Typography } from '@mui/material';
import TimerIcon from '@mui/icons-material/Timer';

const FlashDiscountTimer = ({ expiryTime }) => {
  // Renderer for the countdown timer
  const renderer = ({ hours, minutes, seconds, completed }) => {
    if (completed) {
      return (
        <Typography variant="body2" color="error">
          Offer expired!
        </Typography>
      );
    } else {
      return (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <TimerIcon 
            color="error" 
            fontSize="small" 
            sx={{ mr: 0.5 }} 
          />
          <Typography variant="caption" sx={{ fontSize: '0.85rem' }}>
            Offer ends in: {' '}
            <Box 
              component="span" 
              sx={{ 
                color: 'error.main', 
                fontWeight: 'bold' 
              }}
            >
              {hours.toString().padStart(2, '0')}:{minutes.toString().padStart(2, '0')}:{seconds.toString().padStart(2, '0')}
            </Box>
          </Typography>
        </Box>
      );
    }
  };

  return (
    <Countdown
      date={new Date(expiryTime)}
      renderer={renderer}
    />
  );
};

export default FlashDiscountTimer; 