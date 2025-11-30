import { useEffect, useState } from 'react';

function App() {
    const [message, setMessage] = useState<string>('Loading...');

    useEffect(() => {
        // Because of the Vite proxy, this goes to http://localhost:8080/api/hello
        fetch('/api/hello')
            .then((res) => res.text())
            .then(setMessage)
            .catch((err) => {
                console.error(err);
                setMessage('Error contacting backend');
            });
    }, []);

    return (
        <div style={{ fontFamily: 'sans-serif', padding: '2rem' }}>
            <h1>React + Spring Boot Demo</h1>
            <p>Backend says: <strong>{message}</strong></p>
        </div>
    );
}

export default App;
