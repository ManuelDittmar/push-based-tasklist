import React, { useEffect, useState } from "react";
import { Tile } from "@carbon/react";
import axios from "axios";

const Dashboard = () => {
    const [taskCount, setTaskCount] = useState({});

    useEffect(() => {
        axios.get("/api/tasks/statistics/count-by-status")
            .then(response => setTaskCount(response.data))
            .catch(error => console.error("Error fetching task statistics:", error));
    }, []);

    return (
        <div>
            <h2>Task Statistics</h2>
            <Tile>
                <h3>Count by Status</h3>
                {Object.entries(taskCount).map(([status, count]) => (
                    <p key={status}>{status}: {count}</p>
                ))}
            </Tile>
        </div>
    );
};

export default Dashboard;
