import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { TextInput, Button, Form } from "@carbon/react";
import axios from "axios";

const TaskDetails = () => {
    const { taskId } = useParams();
    const [task, setTask] = useState(null);
    const [formData, setFormData] = useState({});

    useEffect(() => {
        axios.get(`/api/tasks/${taskId}`)
            .then(response => {
                setTask(response.data);
                setFormData(response.data);
            })
            .catch(error => console.error("Error fetching task details:", error));
    }, [taskId]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleUpdate = () => {
        axios.put(`/api/tasks/${taskId}`, formData)
            .then(() => alert("Task updated successfully!"))
            .catch(error => console.error("Failed to update task:", error));
    };

    const handleComplete = () => {
        axios.post(`/api/tasks/${taskId}/complete`, {})
            .then(() => alert("Task completed!"))
            .catch(error => console.error("Failed to complete task:", error));
    };

    if (!task) return <p>Loading...</p>;

    return (
        <Form>
            <h3>Task Details</h3>
            <TextInput id="name" name="userTaskName" labelText="Task Name" value={formData.userTaskName} onChange={handleChange} />
            <TextInput id="assignee" name="assignee" labelText="Assignee" value={formData.assignee} onChange={handleChange} />
            <TextInput id="priority" name="priority" labelText="Priority" value={formData.priority} onChange={handleChange} />
            <TextInput id="dueDate" name="dueDate" labelText="Due Date" value={formData.dueDate} onChange={handleChange} />
            <Button kind="primary" onClick={handleUpdate}>Update</Button>
            <Button kind="secondary" onClick={handleComplete}>Complete Task</Button>
        </Form>
    );
};

export default TaskDetails;