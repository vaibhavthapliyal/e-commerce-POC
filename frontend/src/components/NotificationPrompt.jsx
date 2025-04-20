import React from 'react';
import { useDispatch } from 'react-redux';
import { setNotificationPermission } from '../store/userSlice';
import { notificationApi } from '../api/api';

const NotificationPrompt = ({ onClose }) => {
  const dispatch = useDispatch();

  const handleAccept = async () => {
    try {
      const permission = await Notification.requestPermission();
      dispatch(setNotificationPermission(permission));
      
      // Send notification permission to backend
      await notificationApi.sendNotification({
        recipientEmail: localStorage.getItem('userEmail') || 'user@example.com',
        subject: 'Notification Permission',
        content: `User granted notification permission: ${permission}`,
        type: 'SYSTEM'
      });
      
      onClose();
    } catch (error) {
      console.error('Error requesting notification permission:', error);
      onClose();
    }
  };

  const handleDecline = () => {
    dispatch(setNotificationPermission('denied'));
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md mx-4">
        <h3 className="text-xl font-semibold mb-4">Stay Updated!</h3>
        
        <p className="mb-4">
          Would you like to receive notifications about special offers and order updates?
        </p>
        
        <div className="flex space-x-4">
          <button
            onClick={handleDecline}
            className="flex-1 py-2 px-4 border border-gray-300 rounded-md hover:bg-gray-100 transition duration-300"
          >
            No, thanks
          </button>
          
          <button
            onClick={handleAccept}
            className="flex-1 py-2 px-4 bg-primary text-white rounded-md hover:bg-opacity-90 transition duration-300"
          >
            Yes, I'm in!
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotificationPrompt; 