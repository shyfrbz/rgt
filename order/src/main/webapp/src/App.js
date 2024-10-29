import './App.css';
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Order from './Order'; // 주문 페이지 컴포넌트
import Dashboard from './Dashboard'; // 대시보드 페이지 컴포넌트

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Order />} /> 
        <Route path="/dashboard" element={<Dashboard />} /> 
      </Routes>
    </Router>
  );
}

export default App;
