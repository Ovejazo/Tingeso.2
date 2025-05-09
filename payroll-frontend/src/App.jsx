import './App.css'
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';
import EmployeeList from './components/EmployeesList';
import AddEditEmployee from './components/AddEditEmployee';
import BookingList from './components/BookingList';
import AddEditBooking from './components/AddEditBooking';
import NotFound from './components/NotFound';
import PaycheckList from './components/PaycheckList';
import PaycheckCalculate from './components/PaycheckCalculate';
import AnualReport from './components/AnualReport';
import Login from './components/Login';
import ClientList from './components/ClientList';
import AddEditClient from './components/AddEditClient';
import BookingReceipt from './components/BookingReceipt';

function App() {
  return (
      <Router>
          <div className="container">
          <Navbar></Navbar>
            <Routes>
              <Route path="/home" element={<Home/>} />
              <Route path="/login" element={<Login/>} />
              <Route path="/register" element={<Home/>} />
              <Route path="/employee/list" element={<EmployeeList/>} />
              <Route path="/employee/add" element={<AddEditEmployee/>} />
              <Route path="/client/list" element={<ClientList/>} />
              <Route path="/client/add" element={<AddEditClient/>} />
              <Route path="/employee/edit/:id" element={<AddEditEmployee/>} />
              <Route path="/paycheck/list" element={<PaycheckList/>} />
              <Route path="/paycheck/calculate" element={<PaycheckCalculate/>} />
              <Route path="/reports/AnualReport" element={<AnualReport/>} />
              <Route path="/extraHours/list" element={<BookingList/>} />
              <Route path="/booking/add" element={<AddEditBooking/>} />
              <Route path="/extraHours/edit/:id" element={<AddEditBooking/>} />
              <Route path="/booking/receipt/:id" element={<BookingReceipt/>} />
              <Route path="*" element={<NotFound/>} />
            </Routes>
          </div>
      </Router>
  );
}

export default App
