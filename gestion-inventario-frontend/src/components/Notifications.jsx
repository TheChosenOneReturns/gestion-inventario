import React, { useState, useEffect } from "react";
// 1. RUTA DE IMPORTACIÓN CORREGIDA
import { getAlerts, markAlertAsRead } from "../services/api";
import { useAuth } from "../context/AuthContext";
// 2. RUTA CSS CORREGIDA (asumiendo que está en src/components/client/)
import "./client/Notifications.css";

const BellIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
    </svg>
);

function Notifications() {
    const { user } = useAuth();
    const [alerts, setAlerts] = useState([]);
    const [showNotifications, setShowNotifications] = useState(false);

    useEffect(() => {
        if (!user?.clientId) return;

        const fetchAlerts = async () => {
            try {
                const response = await getAlerts(user.clientId);
                setAlerts(response.data);
            } catch (error) {
                console.error('Error fetching alerts:', error);
            }
        };

        fetchAlerts();
        const interval = setInterval(fetchAlerts, 60000);
        return () => clearInterval(interval);
    }, [user]);

    const toggleNotifications = () => {
        setShowNotifications(!showNotifications);
    };

    const markAsRead = async (alertId) => {
        if (!user?.clientId) return;
        try {
            await markAlertAsRead(user.clientId, alertId);
            setAlerts(prevAlerts => prevAlerts.filter(alert => alert.id !== alertId));
        } catch (error) {
            console.error('Error marking alert as read:', error);
        }
    };

    const unreadAlertCount = alerts.length;

    return (
        <div className="notifications-container">
            <button onClick={toggleNotifications} className="notifications-button">
                <BellIcon />
                {unreadAlertCount > 0 && (
                    <span className="notification-badge">{unreadAlertCount}</span>
                )}
            </button>
            {showNotifications && (
                <div className="notifications-dropdown">
                    <h4>Alertas de Stock Bajo</h4>
                    {alerts.length === 0 ? (
                        <p>No hay alertas nuevas.</p>
                    ) : (
                        <ul>
                            {alerts.map(alert => (
                                <li key={alert.id}>
                                    <p>{alert.message}</p>
                                    <button onClick={() => markAsRead(alert.id)}>Marcar como leído</button>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>
    );
}

export default Notifications;