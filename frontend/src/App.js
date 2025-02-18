import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Dashboard from "./Dashboard";
import TaskList from "./TaskList";
import TaskDetails from "./TaskDetails";
import Layout from "./Layout";

const basename = "/app";

function App() {
    return (
        <Router basename={basename}>
            <Layout>
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/tasks" element={<TaskList />} />
                    <Route path="/tasks/:taskId" element={<TaskDetails />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;
