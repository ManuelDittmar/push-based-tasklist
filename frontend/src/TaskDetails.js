import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    TextInput, Button, DatePicker, DatePickerInput
} from "@carbon/react";
import axios from "axios";

const TaskDetails = () => {
    const { taskId } = useParams();
    const [task, setTask] = useState(null);
    const [formData, setFormData] = useState({
        dueDate: null,
        followUpDate: null,
        assignee: "",
        candidateUsers: [],
        candidateGroups: [],
        priority: ""
    });

    useEffect(() => {
        axios.get(`/api/tasks/${taskId}`)
            .then(response => {
                setTask(response.data);
                setFormData({
                    dueDate: response.data.dueDate ? new Date(response.data.dueDate) : null,
                    followUpDate: response.data.followUpDate ? new Date(response.data.followUpDate) : null,
                    assignee: response.data.assignee || "",
                    candidateUsers: response.data.candidateUsers || [],
                    candidateGroups: response.data.candidateGroups || [],
                    priority: response.data.priority ?? ""
                });
            })
            .catch(error => console.error("Error fetching task details:", error));
    }, [taskId]);

    const handleChange = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const cleanFormData = () => {
        const formatDate = (date) => date ? date.toISOString().split(".")[0] + "Z" : null;

        return {
            ...formData,
            candidateUsers: formData.candidateUsers.length ? formData.candidateUsers : [],
            candidateGroups: formData.candidateGroups.length ? formData.candidateGroups : [],
            dueDate: formatDate(formData.dueDate),
            followUpDate: formatDate(formData.followUpDate),
        };
    };

    const handleUpdate = () => {
        const payload = cleanFormData();

        axios.put(`/api/tasks/${taskId}`, payload)
            .then(() => alert("Task updated successfully!"))
            .catch(error => console.error("Error updating task:", error));
    };

    if (!task) return <p>Loading...</p>;

    return (
        <div style={{
            display: "grid",
            gridTemplateColumns: "3fr 0.1fr 1fr",
            gap: "20px",
            padding: "20px",
            margin: "auto",
            alignItems: "start",
            width: "100%",
            overflow: "hidden"
        }}>
            {/* Left Column - Read-Only Information */}
            <div style={{ display: "flex", flexDirection: "column", gap: "10px", padding: "20px" }}>
                <h3>{task.userTaskName}</h3>
                <p>Case: {task.customCaseId} / Task ID: {task.customUserTaskId}</p>

                {/* Read-Only Fields in Two Columns */}
                <div style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "10px"
                }}>
                    <TextInput id="createdAt" labelText="Created" value={task.createdAt} readOnly style={{ width: "100%" }} />
                    <TextInput id="source" labelText="Source" value={task.source} readOnly style={{ width: "100%" }} />
                    <TextInput id="processInstanceId" labelText="Instance ID" value={task.processInstanceId} readOnly style={{ width: "100%" }} />
                    <TextInput id="taskState" labelText="Task State" value={task.taskState} readOnly style={{ width: "100%" }} />
                </div>
            </div>

            {/* Vertical Divider */}
            <div style={{
                backgroundColor: "#ccc",
                width: "2px",
                height: "100%"
            }} />

            {/* Right Column - Editable Fields + Submit Button */}
            <div style={{
                display: "flex",
                flexDirection: "column",
                gap: "16px",
                justifyContent: "flex-start",
                alignItems: "stretch",
                padding: "20px",
                width: "100%"
            }}>
                <TextInput
                    id="assignee"
                    labelText="Assignee"
                    value={formData.assignee}
                    onChange={(e) => handleChange("assignee", e.target.value)}
                    style={{ width: "100%", minWidth: 0 }}
                />
                <TextInput
                    id="priority"
                    labelText="Priority"
                    type="number"
                    value={formData.priority}
                    onChange={(e) => handleChange("priority", e.target.value)}
                    style={{ width: "100%", minWidth: 0 }}
                />
                <TextInput
                    id="candidateUsers"
                    labelText="Candidate Users"
                    value={formData.candidateUsers.join(", ")}
                    placeholder="Comma-separated"
                    onChange={(e) => handleChange("candidateUsers", e.target.value.split(",").map(u => u.trim()))}
                    style={{ width: "100%", minWidth: 0 }}
                />
                <TextInput
                    id="candidateGroups"
                    labelText="Candidate Groups"
                    value={formData.candidateGroups.join(", ")}
                    placeholder="Comma-separated"
                    onChange={(e) => handleChange("candidateGroups", e.target.value.split(",").map(g => g.trim()))}
                    style={{ width: "100%", minWidth: 0 }}
                />

                {/* Date Pickers and Button Wrapper */}
                <div style={{ display: "flex", gap: "10px", width: "100%" }}>
                    <DatePicker datePickerType="single" onChange={(dates) => handleChange("followUpDate", dates.length > 0 ? dates[0] : null)}>
                        <DatePickerInput
                            id="followUpDate"
                            labelText="Follow-up Date"
                            placeholder="yyyy-mm-dd"
                            readOnly
                            style={{ width: "100%", minWidth: 0 }}
                        />
                    </DatePicker>

                    <DatePicker datePickerType="single" onChange={(dates) => handleChange("dueDate", dates.length > 0 ? dates[0] : null)}>
                        <DatePickerInput
                            id="dueDate"
                            labelText="Due Date"
                            placeholder="yyyy-mm-dd"
                            readOnly
                            style={{ width: "100%", minWidth: 0 }}
                        />
                    </DatePicker>
                </div>

                {/* Button that takes the full width of Date Pickers */}
                <div style={{ width: "100%" }}>
                    <Button kind="primary" onClick={handleUpdate} style={{ width: "100%" }}>
                        Update
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default TaskDetails;