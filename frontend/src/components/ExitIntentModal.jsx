import React from 'react';
import { useNavigate } from 'react-router-dom';

const ExitIntentModal = ({ onClose }) => {
  const navigate = useNavigate();

  const handleContinueShopping = () => {
    onClose();
  };

  const handleCompleteCheckout = () => {
    navigate('/checkout');
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md mx-4 relative">
        <button 
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-gray-700"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        
        <div className="text-center">
          <h3 className="text-xl font-semibold mb-2">Wait! Don't Miss Out!</h3>
          
          <div className="mb-4 text-5xl font-bold text-secondary">5% OFF</div>
          
          <p className="mb-6">
            Complete your purchase now to receive an exclusive 5% discount on your entire order!
          </p>
          
          <div className="flex flex-col sm:flex-row space-y-3 sm:space-y-0 sm:space-x-4">
            <button
              onClick={handleContinueShopping}
              className="py-2 px-4 border border-gray-300 rounded-md hover:bg-gray-100 transition duration-300"
            >
              Continue Shopping
            </button>
            
            <button
              onClick={handleCompleteCheckout}
              className="py-2 px-4 bg-secondary text-white rounded-md hover:bg-opacity-90 transition duration-300"
            >
              Complete Purchase
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExitIntentModal; 