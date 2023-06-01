import './App.css';
import Chatting from './Chatting';
import Home from './Home';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <Router>
      <Routes>
        <Route exact path="/" element={<Home />} />
        <Route path="/chat" element={<Chatting />} />
      </Routes>
    </Router>
  );
}

export default App;
